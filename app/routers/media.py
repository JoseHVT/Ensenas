from fastapi import APIRouter, Depends, HTTPException, status
from sqlalchemy.orm import Session

from ..crud import media as crud_media
from ..dependencies import get_db, get_current_user

router = APIRouter(
    prefix="/media",
    tags=["Media"]
)

@router.get("/video/{sign_id}", response_model=str)
def get_video_url_for_sign(
    sign_id: int,
    db: Session = Depends(get_db),
    #  sololo usuarios logueados pueden pedir URLs
    current_user: dict = Depends(get_current_user) 
):
    """
    (Protegido) Obtiene una URL de video temporal y segura 
    para una sena especifica.
    
    La URL expira en 10 minutos.
    """
    url = crud_media.get_signed_video_url(db, sign_id=sign_id)
    
    if not url:
        raise HTTPException(status_code=404, detail="Seña o archivo de video no encontrado")
        
    # El response_model=str se encarga de devolver la URL
    # como un string simple, no como un JSON.
    return url