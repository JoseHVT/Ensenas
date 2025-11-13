from sqlalchemy.orm import Session
from firebase_admin import storage
from datetime import timedelta
from .. import models

def get_signed_video_url(db: Session, sign_id: int) -> str:
    """
    Busca la ruta de un video en nuestra BD y genera una 
    URL firmada y temporal (10 minutos) de Firebase Storage.
    """
    
    # 1. Obtenemos la informacion de la sena (para saber su ruta)
    sign = db.query(models.Sign).filter(models.Sign.id == sign_id).first()
    
    if not sign:
        return None # La sena no existe

    # Esta es la ruta que guardamos en nuestra db, ej: "videos/saludos/hola.mp4"
    video_path = sign.video_path 
    
    try:
        # 2. Obtenemos el "bucket" (el almacen) de Firebase Storage
        # revisar que 'firebase-service-account.json' tenga permisos
        bucket = storage.bucket() # Usa el bucket por defecto configurado
        
        # 3. Le pedimos al bucket que nos firme la URL para ese archivo
        blob = bucket.blob(video_path)
        
        # 4. Generamos la URL con una caducidad de 10 minutos
        signed_url = blob.generate_signed_url(expiration=timedelta(minutes=10))
        
        return signed_url
        
    except Exception as e:
        print(f"Error generando URL firmada: {e}")
        return None