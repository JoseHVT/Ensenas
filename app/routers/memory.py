from fastapi import APIRouter, Depends, Query, HTTPException, status
from sqlalchemy.orm import Session
from typing import List

from ..crud import memory as crud_memory
from .. import schemas
from ..dependencies import get_db, get_current_user

router = APIRouter(
    prefix="/memory",
    tags=["Memory Game"]
)

@router.get("/deck", response_model=List[schemas.SignPair])
def get_game_deck(
    size: int = Query(8, ge=4, le=12, description="Numero de pares (ej. 8 pares = 16 cartas)"),
    db: Session = Depends(get_db)
):
    """
    Obtiene un mazo aleatorio de pares palabra-seña para el juego.
    """
    deck = crud_memory.get_memory_deck(db, size=size)
    return deck

@router.post("/attempt", response_model=schemas.MemoryRun)
def submit_memory_run(
    run_data: schemas.MemoryRunCreate,
    # prtgdo Solo usuarios logueados pueden guardar su puntaje
    current_user: dict = Depends(get_current_user), 
    db: Session = Depends(get_db)
):
    """
    Guarda el resultado (intentos, duracion, etc) de una partida.
    Requiere autenticacin.
    """
    user_id = current_user["uid"]
    return crud_memory.create_memory_run(db, run=run_data, user_id=user_id)


# --- Endpoint Temporal para crear pares  ---

@router.post("/create-pair", 
             response_model=schemas.SignPair, 
             tags=["_TEMP_Dev_Helpers"]
)
def create_a_sign_pair(
    sign_id: int,
    word: str,
    db: Session = Depends(get_db)
):
    """
    TEMP Crea un par palabra-seña para el juego.
    se debe haber creado una Seña (con POST /dictionary) primero.
    """
    pair = crud_memory.create_sign_pair(db, sign_id=sign_id, word=word)
    if not pair:
        raise HTTPException(status_code=404, detail="Seña (Sign) no encontrada con ese ID")
    return pair