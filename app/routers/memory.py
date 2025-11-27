from fastapi import APIRouter, Depends, Query, HTTPException, status
from sqlalchemy.orm import Session
from typing import List

from ..crud import memory as crud_memory
from ..crud import xp as crud_xp
from ..crud import progress as crud_progress
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
    Guarda el resultado (intentos, duración, etc.) de una partida.
    Requiere autenticacin.
    
    Automáticamente otorga XP y actualiza la racha del usuario.
    """
    user_id = current_user["uid"]
    result = crud_memory.create_memory_run(db, run=run_data, user_id=user_id)
    
    # Calcular y otorgar XP
    xp_earned = crud_xp.calculate_xp_for_memory_game(
        matches=result.matches,
        attempts=result.attempts
    )
    
    if xp_earned > 0:
        try:
            crud_xp.award_xp(
                db,
                user_id=user_id,
                amount=xp_earned,
                source="memory_game",
                source_id=result.id,
                description=f"Memory Game: {result.matches} pares en {result.attempts} intentos"
            )
            
            # Actualizar actividad diaria para racha
            crud_progress.update_daily_activity(
                db,
                user_id=user_id,
                activity_type="memory_game",
                xp_earned=xp_earned
            )
        except Exception as e:
            # Log error pero no fallar el memory game
            print(f"Error awarding XP: {e}")
    
    return result


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
    Asegrate de haber creado una Seña (con POST /dictionary) primero.
    """
    pair = crud_memory.create_sign_pair(db, sign_id=sign_id, word=word)
    if not pair:
        raise HTTPException(status_code=404, detail="Seña (Sign) no encontrada con ese ID")
    return pair