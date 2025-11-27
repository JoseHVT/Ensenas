from sqlalchemy.orm import Session
from sqlalchemy import func, and_, cast, Date
from .. import models, schemas
from datetime import datetime, timedelta
from typing import List

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

    # 6. Calcular racha actual
    racha_actual = calculate_current_streak(db, user_id)
    
    return schemas.StatsSummary(
        precision_global=round(precision_global, 2),
        tiempo_total_ms=total_duration_ms,
        racha_actual=racha_actual,
        senas_dominadas=senas_dominadas
    )


# === FUNCIONES PARA SISTEMA DE RACHA ===

def update_daily_activity(
    db: Session, 
    user_id: str, 
    activity_type: str,
    xp_earned: int = 0
):
    """
    Actualiza la actividad diaria del usuario.
    Crea un registro si no existe para hoy, o actualiza el existente.
    """
    today = datetime.now().replace(hour=0, minute=0, second=0, microsecond=0)
    
    # Buscar si ya existe un registro para hoy
    activity = db.query(models.DailyActivity).filter(
        and_(
            models.DailyActivity.user_id == user_id,
            cast(models.DailyActivity.activity_date, Date) == today.date()
        )
    ).first()
    
    if activity:
        # Actualizar el registro existente
        if activity_type == 'quiz':
            activity.quizzes_completed += 1
        elif activity_type == 'lesson':
            activity.lessons_completed += 1
        elif activity_type == 'memory_game':
            activity.memory_games_completed += 1
        activity.xp_earned += xp_earned
    else:
        # Crear nuevo registro para hoy
        activity = models.DailyActivity(
            user_id=user_id,
            activity_date=today,
            quizzes_completed=1 if activity_type == 'quiz' else 0,
            lessons_completed=1 if activity_type == 'lesson' else 0,
            memory_games_completed=1 if activity_type == 'memory_game' else 0,
            xp_earned=xp_earned
        )
        db.add(activity)
    
    db.commit()
    db.refresh(activity)
    return activity


def calculate_current_streak(db: Session, user_id: str) -> int:
    """
    Calcula la racha actual del usuario (días consecutivos con actividad).
    """
    today = datetime.now().replace(hour=0, minute=0, second=0, microsecond=0)
    
    # Obtener todas las actividades ordenadas por fecha descendente
    activities = db.query(models.DailyActivity).filter(
        models.DailyActivity.user_id == user_id
    ).order_by(models.DailyActivity.activity_date.desc()).all()
    
    if not activities:
        return 0
    
    streak = 0
    current_date = today
    
    for activity in activities:
        activity_date = activity.activity_date.replace(hour=0, minute=0, second=0, microsecond=0)
        
        # Si la actividad es del día actual o del día anterior consecutivo
        if activity_date == current_date:
            streak += 1
            current_date -= timedelta(days=1)
        elif activity_date == current_date + timedelta(days=1):
            # Si saltamos al siguiente día en la iteración, continuamos
            continue
        else:
            # Si hay un gap, terminamos el conteo
            break
    
    return streak


def get_weekly_calendar(db: Session, user_id: str) -> List[bool]:
    """
    Devuelve un array de 7 booleanos representando los últimos 7 días.
    True si hubo actividad ese día, False si no.
    """
    today = datetime.now().replace(hour=0, minute=0, second=0, microsecond=0)
    calendar = []
    
    for i in range(6, -1, -1):  # De 6 a 0 (últimos 7 días)
        date = today - timedelta(days=i)
        activity = db.query(models.DailyActivity).filter(
            and_(
                models.DailyActivity.user_id == user_id,
                cast(models.DailyActivity.activity_date, Date) == date.date()
            )
        ).first()
        calendar.append(activity is not None)
    
    return calendar


def get_streak_info(db: Session, user_id: str) -> schemas.StreakInfo:
    """
    Obtiene información completa de la racha del usuario.
    """
    current_streak = calculate_current_streak(db, user_id)
    weekly_calendar = get_weekly_calendar(db, user_id)
    
    # Calcular racha más larga
    all_activities = db.query(models.DailyActivity).filter(
        models.DailyActivity.user_id == user_id
    ).order_by(models.DailyActivity.activity_date.desc()).all()
    
    longest_streak = 0
    temp_streak = 0
    last_date = None
    
    for activity in sorted(all_activities, key=lambda x: x.activity_date):
        activity_date = activity.activity_date.replace(hour=0, minute=0, second=0, microsecond=0)
        
        if last_date is None:
            temp_streak = 1
        elif activity_date == last_date + timedelta(days=1):
            temp_streak += 1
        else:
            longest_streak = max(longest_streak, temp_streak)
            temp_streak = 1
        
        last_date = activity_date
    
    longest_streak = max(longest_streak, temp_streak)
    
    # Total de días activos
    total_active_days = len(all_activities)
    
    # Última actividad
    last_activity_date = all_activities[0].activity_date if all_activities else None
    
    return schemas.StreakInfo(
        current_streak=current_streak,
        longest_streak=longest_streak,
        last_activity_date=last_activity_date,
        weekly_calendar=weekly_calendar,
        total_active_days=total_active_days
    )