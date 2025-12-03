from fastapi import APIRouter, Depends, status
from sqlalchemy.orm import Session
from typing import List

from .. import schemas
from ..crud import lessons as crud_lessons
from ..dependencies import get_db

router = APIRouter(
    prefix="/lessons",
    tags=["Lessons"]
)

@router.get("/", response_model=List[schemas.Lesson])
def read_lessons(
    module_id: int,
    db: Session = Depends(get_db)
):
    """
    Obtiene las lecciones de un modulo especifico.
    Ejemplo: GET /lessons/?module_id=1
    """
    return crud_lessons.get_lessons_by_module(db, module_id=module_id)

@router.post("/", response_model=schemas.Lesson, status_code=status.HTTP_201_CREATED)
def create_lesson(
    module_id: int,
    lesson: schemas.LessonCreate,
    db: Session = Depends(get_db)
):
    """
    Crea una leccion para un modulo.
    """
    return crud_lessons.create_lesson(db=db, lesson=lesson, module_id=module_id)