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
    
    # merge() se encarga del UPSERT:
    # Si la Primary Key (user_id, module_id) existe, la actualiza.
    # Si no existe, la inserta.
    merged_progress = db.merge(db_progress)
    db.commit()
    
    return merged_progress


def get_user_progress(db: Session, user_id: str):
    """Obtiene todo el progreso (por modulo) del usuario actual."""
    return db.query(models.UserModuleProgress).filter(models.UserModuleProgress.user_id == user_id).all()


def get_user_stats_summary(db: Session, user_id: str) -> schemas.StatsSummary:
    """
    Calcula las estadisticas resumidas para el dashboard del usuario.
    """
    
    # 1. Calcular precision global y tiempo total de quizzes
    quiz_stats = db.query(
        func.sum(models.QuizAttempt.score).label("total_score"),
        func.sum(models.QuizAttempt.total).label("total_questions"),
        func.sum(models.QuizAttempt.duration_ms).label("total_quiz_time")
    ).filter(models.QuizAttempt.user_id == user_id).first()

    # 2. Calcular tiempo total de memorama
    memory_time = db.query(
        func.sum(models.MemoryRun.duration_ms).label("total_memory_time")
    ).filter(models.MemoryRun.user_id == user_id).scalar() or 0

    # 3. Calcular precision
    total_score = quiz_stats.total_score or 0
    total_questions = quiz_stats.total_questions
    
    precision_global = 0.0
    if total_questions and total_questions > 0:
        precision_global = (total_score / total_questions) * 100
    
    # 4. Calcular tiempo total
    total_duration_ms = (quiz_stats.total_quiz_time or 0) + memory_time

    # 5. Calcular senas dominadas (ej: modulos completados al 100%)
    senas_dominadas = db.query(models.UserModuleProgress).filter(
        models.UserModuleProgress.user_id == user_id,
        models.UserModuleProgress.percent == 100
    ).count()

    # logica de racha 
    # exp de ho
    today = datetime.now().date()

    #score de quizz de hoy
    daily_quiz_score = db.query(func.sum(models.QuizAttempt.score))\
        .filter(models.QuizAttempt.user_id == user_id)\
        .filter(cast(models.QuizAttempt.created_at, Date) == today)\
        .scalar() or 0
    
    #score de memorama de hoy
    daily_memory_matches = db.query(func.sum(models.MemoryRun.matches))\
        .filter(models.MemoryRun.user_id == user_id)\
        .filter(cast(models.MemoryRun.created_at, Date) == today)\
        .scalar() or 0
    
    # xp total de hoy
    xp_today = (daily_quiz_score * 10) + (daily_memory_matches * 5)


    return schemas.StatsSummary(
        precision_global=round(precision_global, 2),
        tiempo_total_ms=total_duration_ms,
        racha_actual=0, # Placeholder
        senas_dominadas=senas_dominadas,
        daily_xp=xp_today # 
    )
