from sqlalchemy.orm import Session
from sqlalchemy.sql.expression import func
from .. import models, schemas

def get_daily_missions(db: Session, user_id: str, limit: int = 3):
    """
    Genera 3 misiones diarias basadas en el nivel del usuario
     usa Quizzes aleatorios de modulos desbloqueados
    """
    
    # 1. Encontrar el nivel del usuario 
    max_module = db.query(func.max(models.UserModuleProgress.module_id))\
        .filter(models.UserModuleProgress.user_id == user_id)\
        .scalar()
    
    max_unlocked_id = max_module if max_module else 1
    
    # 2. Seleccionar retos
    daily_quizzes = db.query(models.Quiz)\
        .filter(models.Quiz.module_id <= max_unlocked_id)\
        .order_by(func.random())\
        .limit(limit)\
        .all()
        
    return daily_quizzes