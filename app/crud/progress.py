from sqlalchemy.orm import Session
from sqlalchemy import func
from .. import models, schemas
from datetime import datetime

def upsert_user_progress(db: Session, user_id: str, progress_in: schemas.UserModuleProgressCreate):
    """
    Crea o actualiza (UPSERT) el progreso de un usuario en un módulo.
    La BBDD se encarga de actualizar 'last_activity' automáticamente.
    """
    
    # Preparamos el objeto con los datos
    db_progress = models.UserModuleProgress(
        user_id=user_id,
        module_id=progress_in.module_id,
        percent=progress_in.percent,
        last_activity=datetime.now() # Aseguramos que se actualice al insertar/actualizar
    )
    
    # merge() hace la magia:
    # Si la Primary Key (user_id, module_id) existe, la actualiza.
    # Si no existe, la inserta.
    merged_progress = db.merge(db_progress)
    db.commit()
    
    return merged_progress


def get_user_progress(db: Session, user_id: str):
    """Obtiene todo el progreso (por módulo) del usuario actual."""
    return db.query(models.UserModuleProgress).filter(models.UserModuleProgress.user_id == user_id).all()


def get_user_stats_summary(db: Session, user_id: str) -> schemas.StatsSummary:
    """
    Calcula las estadísticas resumidas para el dashboard del usuario.
    """
    
    # 1. Calcular precisión global y tiempo total de quizzes
    quiz_stats = db.query(
        func.sum(models.QuizAttempt.score).label("total_score"),
        func.sum(models.QuizAttempt.total).label("total_questions"),
        func.sum(models.QuizAttempt.duration_ms).label("total_quiz_time")
    ).filter(models.QuizAttempt.user_id == user_id).first()

    # 2. Calcular tiempo total de memorama
    memory_time = db.query(
        func.sum(models.MemoryRun.duration_ms).label("total_memory_time")
    ).filter(models.MemoryRun.user_id == user_id).scalar() or 0

    # 3. Calcular precisión
    total_score = quiz_stats.total_score or 0
    total_questions = quiz_stats.total_questions
    
    precision_global = 0.0
    if total_questions and total_questions > 0:
        precision_global = (total_score / total_questions) * 100
    
    # 4. Calcular tiempo total
    total_duration_ms = (quiz_stats.total_quiz_time or 0) + memory_time

    # 5. Calcular señas dominadas (ej: módulos completados al 100%)
    senas_dominadas = db.query(models.UserModuleProgress).filter(
        models.UserModuleProgress.user_id == user_id,
        models.UserModuleProgress.percent == 100
    ).count()

    # (Lógica de "racha" es más compleja, requiere tracking diario. 
    # La dejamos en 0 por ahora como placeholder).
    
    return schemas.StatsSummary(
        precision_global=round(precision_global, 2),
        tiempo_total_ms=total_duration_ms,
        racha_actual=0, # Placeholder
        senas_dominadas=senas_dominadas
    )