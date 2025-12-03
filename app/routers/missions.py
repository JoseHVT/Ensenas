from fastapi import APIRouter, Depends
from sqlalchemy.orm import Session
from typing import List

from .. import schemas
from ..crud import missions as crud_missions # Importamos su propio crud
from ..dependencies import get_db, get_current_user

router = APIRouter(
    prefix="/missions",
    tags=["Gamification"] # Le damos su propia cat en la swagger
)

@router.get("/daily", response_model=List[schemas.Quiz])
def get_daily_missions(
    current_user: dict = Depends(get_current_user),
    db: Session = Depends(get_db)
):
    """
    Obtiene las 3 misiones diarias activas para el usuario.
    """
    user_id = current_user["uid"]
    return crud_missions.get_daily_missions(db, user_id=user_id)