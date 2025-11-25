from fastapi import Depends, HTTPException, status
from fastapi.security import OAuth2PasswordBearer
from firebase_admin import auth, credentials
import firebase_admin

import os
import json

from .database import SessionLocal # Importamos nuestra SessionLocal
# --- Dependencia para la Sesión de BD ---

def get_db():
    """
    Esta funcion es una dependencia de FastAPI que nos
    proporciona una sesion de base de datos por request.
    """
    db = SessionLocal()
    try:
        yield db
    finally:
        db.close()

# 1. Buscamos si existe la variable de entorno con el JSON completo (produccion/Nube)
firebase_creds_json = os.getenv("FIREBASE_CREDENTIALS_JSON")

try:
    if firebase_creds_json:
        # Si estamos en la nube, cargamos el JSON desde la variable
        cred_dict = json.loads(firebase_creds_json)
        cred = credentials.Certificate(cred_dict)
    else:
        # Si estamos en local, buscamos el archivo
        cred = credentials.Certificate("firebase-service-account.json")
        
    # Inicializamos la app (verificamos si ya existe para no reiniciarla)
    try:
        firebase_admin.get_app()
    except ValueError:
        firebase_admin.initialize_app(cred)

except Exception as e:
    print(f"ADVERTENCIA CRIT: No se pudo cargar credenciales de Firebase. Error: {e}")
    # No lanzamos error aqi para que la app arranque, 
    # pero los endpoints protegidos fallaran si esto no funciona.

# Esto le dice a FastAPI "busca un token en la cabecera 'Authorization'"
oauth2_scheme = OAuth2PasswordBearer(tokenUrl="token")

# --- Guardian ---
async def get_current_user(token: str = Depends(oauth2_scheme)):
    try:
        # 1. Le pedimos a Firebase que verifique el token
        decoded_token = auth.verify_id_token(token)

        # 2. Si es valido, extraemos el ID de usuario (uid)
        uid = decoded_token.get("uid")
        if not uid:
            raise HTTPException(
                status_code=status.HTTP_401_UNAUTHORIZED,
                detail="Token no vsalifo, no se encontro UID",
            )
        

        # 3. Devolvemos el UID para que el endpoint lo use
        return {"uid": uid}

    except auth.InvalidIdTokenError:
        # El token es invalido o expiro
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail="Token no valido  o expirado",
        )
    except Exception as e:
        # Otro error (ej. no se pudo conectar a Firebase)
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail=f"Error de auth: {e}",
        )