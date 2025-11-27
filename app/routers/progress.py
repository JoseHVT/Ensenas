from fastapi import APIRouter, Depends, status
from sqlalchemy.orm import Session
from typing import List

from ..crud import progress as crud_progress
from ..crud import xp as crud_xp
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
    de un usuario en un módulo específico.
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
    del usuario actual (todos los módulos que ha iniciado).
    """
    user_id = current_user["uid"]
    return crud_progress.get_user_progress(db, user_id=user_id)

@router.get("/stats/summary", response_model=schemas.StatsSummary)
def get_my_stats_summary(
    current_user: dict = Depends(get_current_user),
    db: Session = Depends(get_db)
):
    """
    (Protegido) Obtiene un resumen de estadísticas 
    (precisión, tiempo, etc.) para el dashboard del usuario.
    """
    user_id = current_user["uid"]
    return crud_progress.get_user_stats_summary(db, user_id=user_id)


@router.get("/streak", response_model=schemas.StreakInfo)
def get_my_streak(
    current_user: dict = Depends(get_current_user),
    db: Session = Depends(get_db)
):
    """
    (Protegido) Obtiene información completa de la racha del usuario,
    incluyendo racha actual, racha más larga, calendario semanal, etc.
    """
    user_id = current_user["uid"]
    return crud_progress.get_streak_info(db, user_id=user_id)


@router.post("/streak/update", response_model=schemas.DailyActivity)
def update_my_streak(
    streak_update: schemas.StreakUpdate,
    current_user: dict = Depends(get_current_user),
    db: Session = Depends(get_db)
):
    """
    (Protegido) Actualiza la actividad diaria del usuario.
    Se debe llamar cada vez que el usuario complete una actividad
    (quiz, lección, memory game) para mantener la racha actualizada.
    """
    user_id = current_user["uid"]
    return crud_progress.update_daily_activity(
        db, 
        user_id=user_id, 
        activity_type=streak_update.activity_type,
        xp_earned=streak_update.xp_earned
    )


@router.get("/xp/level", response_model=schemas.UserLevelInfo)
def get_my_level_info(
    current_user: dict = Depends(get_current_user),
    db: Session = Depends(get_db)
):
    """
    (Protegido) Obtiene información completa del nivel del usuario,
    incluyendo XP total, nivel actual, progreso, etc.
    """
    user_id = current_user["uid"]
    return crud_xp.get_user_level_info(db, user_id=user_id)


@router.post("/xp/award", response_model=schemas.XPAwardResponse)
def award_xp_to_user(
    xp_request: schemas.XPAwardRequest,
    current_user: dict = Depends(get_current_user),
    db: Session = Depends(get_db)
):
    """
    (Protegido) Otorga XP al usuario y actualiza su nivel.
    Debe llamarse después de completar actividades (quiz, memory game, etc.)
    """
    user_id = current_user["uid"]
    return crud_xp.award_xp(
        db,
        user_id=user_id,
        amount=xp_request.amount,
        source=xp_request.source,
        source_id=xp_request.source_id,
        description=xp_request.description
    )


@router.get("/xp/transactions", response_model=List[schemas.XPTransaction])
def get_my_xp_transactions(
    skip: int = 0,
    limit: int = 50,
    current_user: dict = Depends(get_current_user),
    db: Session = Depends(get_db)
):
    """
    (Protegido) Obtiene el historial de transacciones de XP del usuario.
    """
    user_id = current_user["uid"]
    return crud_xp.get_xp_transactions(db, user_id=user_id, skip=skip, limit=limit)