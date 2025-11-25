from .. import models

from fastapi import APIRouter, Depends, HTTPException, status
from sqlalchemy.orm import Session
from typing import List

from ..crud import quizzes as crud_quizzes
from .. import schemas
from ..dependencies import get_db, get_current_user

router = APIRouter(
    prefix="/quizzes",
    tags=["Quizzes"]
)

@router.get("/", response_model=List[schemas.Quiz])
def get_quizzes_for_module(
    module_id: int,
    db: Session = Depends(get_db)
):
    """
    Obtiene la lista de quizzes disponibles para un modulo especifico.
    """
    quizzes = crud_quizzes.get_quiz_by_module(db, module_id=module_id)
    return quizzes

@router.get("/{quiz_id}", response_model=schemas.Quiz)
def get_quiz_details(
    quiz_id: int,
    db: Session = Depends(get_db)
):
    """
    Obtiene los detalles de un quiz especifico, incluyendo sus preguntas.
    """
    quiz = crud_quizzes.get_quiz(db, quiz_id=quiz_id)
    if not quiz:
        raise HTTPException(status_code=404, detail="Quiz no encontrado")
    return quiz

@router.post("/attempt", response_model=schemas.QuizAttempt)
def submit_quiz_attempt(
    attempt: schemas.QuizAttemptCreate,
    # prot Solo usuarios autenticados pueden enviar intentos
    current_user: dict = Depends(get_current_user),
    db: Session = Depends(get_db)
):
    """
    Recibe las respuestas de un usuario, calcula su calificacion y guarda el intento.
    Requiere autenticacion.
    """
    user_id = current_user["uid"]
    result = crud_quizzes.create_quiz_attempt(db, attempt=attempt, user_id=user_id)
    
    if not result:
        raise HTTPException(status_code=404, detail="Quiz no encontrado")
        
    return result

@router.get("/my-attempts", response_model=List[schemas.QuizAttempt])
def get_my_attempts(
    skip: int = 0,
    limit: int = 50,
    current_user: dict = Depends(get_current_user),
    db: Session = Depends(get_db)
):
    """
    Obtiene el historial de intentos del usuario actual.
    Requiere autenticacion.
    """
    user_id = current_user["uid"]
    return crud_quizzes.get_user_attempts(db, user_id=user_id, skip=skip, limit=limit)




# --- Endpoint Temporal de Desarrollo (seed) ---

@router.post("/seed-test-quiz", tags=["_TEMP_Dev_Helpers"])
def seed_test_quiz(db: Session = Depends(get_db)):
    """
    [USO TEMPORAL] Crea un quiz de prueba con 2 preguntas,
    asociado al modulo 1.

    """

    # 1. Busca el Modulo 1 
    test_module = db.query(models.Module).filter(models.Module.id == 1).first()

    if not test_module:
        raise HTTPException(
            status_code=404, 
            detail="modulo 1 no encontrado. Por favor, crea un modulo con POST /modules primero."
        )

    # 2. Revisa si el quiz de prueba ya existe
    existing_quiz = db.query(models.Quiz).filter(models.Quiz.title == "Quiz de Saludos (Prueba)").first()
    if existing_quiz:
        return {"message": "El quiz de prueba ya existe.", "quiz_id": existing_quiz.id}

    # 3. Crea el Quiz y sus Preguntas 
    new_quiz = models.Quiz(
        title="Quiz de Saludos (Prueba)",
        type="multiple_choice",
        module_id=test_module.id, # Lo asociamos al mo4dulo 1
        questions=[  #  crear las preguntas "hijo" directamente
            models.QuizQuestion(
                prompt="¿Que seña significa 'Hola'?",
                options={"a": "video_a.mp4", "b": "video_hola.mp4", "c": "video_c.mp4"},
                answer="b"
            ),
            models.QuizQuestion(
                prompt="¿Que seña significa 'Adios'?",
                options={"a": "video_adios.mp4", "b": "video_y.mp4", "c": "video_z.mp4"},
                answer="a"
            )
        ]
    )

    # 4. Añadimos el quizz y SQLAlchemy se encarga de las pregunts
    db.add(new_quiz)
    db.commit()
    db.refresh(new_quiz)

    return {
        "message": "Quiz de prueba y 2 preguntas creados exitosamente.", 
        "quiz_id": new_quiz.id
    }