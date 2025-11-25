from fastapi import APIRouter, Depends, status
from sqlalchemy.orm import Session
from typing import List

from ..crud import progress as crud_progress
from .. import schemas
from ..dependencies import get_db, get_current_user

router = APIRouter(
    tags=["Progress & Stats"] # Agrupamos todos bajo la misma etiqueta
)

@router.post("/progress", response_model=schemas.UserModuleProgress)
def update_my_progress(
    progress_in: schemas.UserModuleProgressCreate,
    current_user: dict = Depends(get_current_user),
    db: Session = Depends(get_db)
):
    """
    (Protegido) Actualiza (o crea) el porcentaje de progreso 
    de un usuario en un modulo especifico.
    """
    user_id = current_user["uid"]
    return crud_progress.upsert_user_progress(db, user_id=user_id, progress_in=progress_in)

@router.get("/progress", response_model=List[schemas.UserModuleProgress])
def get_my_progress(
    current_user: dict = Depends(get_current_user),
    db: Session = Depends(get_db)
):
    """
    (Protegido) Obtiene una lista de todo el progreso 
    del usuario actual (todos los modulos que ha iniciado).
    """
    user_id = current_user["uid"]
    return crud_progress.get_user_progress(db, user_id=user_id)

@router.get("/stats/summary", response_model=schemas.StatsSummary)
def get_my_stats_summary(
    current_user: dict = Depends(get_current_user),
    db: Session = Depends(get_db)
):
    """
    (Protegido) Obtiene un resumen de estadisticas 
    (precision, tiempo, etc.) para el dashboard del usuario.
    """
    user_id = current_user["uid"]
    return crud_progress.get_user_stats_summary(db, user_id=user_id)