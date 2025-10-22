from fastapi import Depends, HTTPException, status
from fastapi.security import OAuth2PasswordBearer
from firebase_admin import auth, credentials
import firebase_admin

from .database import SessionLocal # Importamos nuestra SessionLocal
# --- Dependencia para la Sesión de BD ---
def get_db():
    """
    Esta fun crea una sesion de base de datos por cada petición
    y se asegura de cerrarla al terminar.
    """
    db = SessionLocal()

    try:
        yield db # "yield" entrega la sesioj a la dun del endpoint
    finally:
        db.close() # Esto se ejecuta al final, cerrando la sesión
# --------------------------------------
#es necesario el archivo de credenciales de firebase
try:
    cred = credentials.Certificate("firebase-service-account.json")
    firebase_admin.initialize_app(cred)
except ValueError:
    pass # Evita que se queje si se recarga y ya estaba inicializado
except FileNotFoundError:
    print("ADVERTENCIA: No se encontro 'firebase-service-account.json'. La autenticacion fallara.")
    pass

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