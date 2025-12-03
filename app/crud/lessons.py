from sqlalchemy.orm import Session
from .. import models, schemas

def get_lessons_by_module(db: Session, module_id: int):
    """Obtiene todas las lecciones de un dulo específico"""
    return db.query(models.Lesson).filter(models.Lesson.module_id == module_id).all()

def create_lesson(db: Session, lesson: schemas.LessonCreate, module_id: int):
    """Crea una nueva leccion vinculada a un moqdulo"""
    db_lesson = models.Lesson(
        **lesson.model_dump(),
        module_id=module_id
    )
    db.add(db_lesson)
    db.commit()
    db.refresh(db_lesson)
    return db_lesson