from sqlalchemy.orm import Session
from .. import models, schemas
import math


def calculate_xp_for_level(level: int) -> int:
    """
    Calcula el XP total necesario para alcanzar un nivel específico.
    Fórmula exponencial: 100 * (nivel - 1)^1.5
    """
    if level == 1:
        return 0
    return int(100 * math.pow(level - 1, 1.5))


def calculate_level_from_xp(total_xp: int) -> int:
    """
    Calcula el nivel actual basado en el XP total.
    Niveles del 1 al 50.
    """
    level = 1
    for i in range(1, 51):
        xp_for_next_level = calculate_xp_for_level(i + 1)
        if total_xp < xp_for_next_level:
            level = i
            break
        if i == 50:
            level = 50
    return level


def get_level_title(level: int) -> str:
    """Devuelve el título basado en el nivel."""
    if level < 5:
        return "Aprendiz"
    elif level < 10:
        return "Estudiante"
    elif level < 20:
        return "Practicante"
    elif level < 30:
        return "Comunicador"
    elif level < 40:
        return "Experto"
    elif level < 50:
        return "Maestro"
    else:
        return "Leyenda LSM"


def get_user_level_info(db: Session, user_id: str) -> schemas.UserLevelInfo:
    """
    Obtiene la información completa del nivel del usuario.
    """
    user = db.query(models.User).filter(models.User.uid == user_id).first()
    
    if not user:
        raise ValueError(f"User {user_id} not found")
    
    total_xp = user.total_xp or 0
    current_level = calculate_level_from_xp(total_xp)
    
    # XP necesario para el nivel actual y el siguiente
    xp_for_current_level = calculate_xp_for_level(current_level)
    xp_for_next_level = calculate_xp_for_level(current_level + 1)
    
    # XP que tiene en el nivel actual
    current_level_xp = total_xp - xp_for_current_level
    required_xp = xp_for_next_level - xp_for_current_level
    
    # Progreso en el nivel actual (0.0 - 1.0)
    progress = 0.0
    if current_level < 50 and required_xp > 0:
        progress = min(1.0, current_level_xp / required_xp)
    elif current_level == 50:
        progress = 1.0
    
    return schemas.UserLevelInfo(
        total_xp=total_xp,
        current_level=current_level,
        level_title=get_level_title(current_level),
        xp_for_current_level=xp_for_current_level,
        xp_for_next_level=xp_for_next_level,
        current_level_xp=current_level_xp,
        required_xp=required_xp,
        progress=round(progress, 3)
    )


def award_xp(
    db: Session, 
    user_id: str, 
    amount: int,
    source: str,
    source_id: int = None,
    description: str = None
) -> schemas.XPAwardResponse:
    """
    Otorga XP al usuario y actualiza su nivel.
    Registra la transacción en XPTransaction.
    
    Args:
        user_id: ID del usuario
        amount: Cantidad de XP a otorgar
        source: Fuente del XP ('quiz', 'memory_game', 'lesson', 'streak_bonus', 'achievement')
        source_id: ID opcional de la fuente (quiz_id, etc.)
        description: Descripción opcional
    
    Returns:
        XPAwardResponse con información de nivel y si subió de nivel
    """
    # Obtener usuario
    user = db.query(models.User).filter(models.User.uid == user_id).first()
    
    if not user:
        raise ValueError(f"User {user_id} not found")
    
    # Nivel anterior
    previous_level = calculate_level_from_xp(user.total_xp or 0)
    
    # Actualizar XP total
    user.total_xp = (user.total_xp or 0) + amount
    
    # Calcular nuevo nivel
    new_level = calculate_level_from_xp(user.total_xp)
    user.current_level = new_level
    
    # Registrar transacción
    transaction = models.XPTransaction(
        user_id=user_id,
        amount=amount,
        source=source,
        source_id=source_id,
        description=description
    )
    db.add(transaction)
    
    # Commit cambios
    db.commit()
    db.refresh(user)
    
    # Obtener información de nivel actualizada
    level_info = get_user_level_info(db, user_id)
    
    return schemas.XPAwardResponse(
        xp_awarded=amount,
        total_xp=user.total_xp,
        previous_level=previous_level,
        current_level=new_level,
        level_up=(new_level > previous_level),
        level_info=level_info
    )


def get_xp_transactions(
    db: Session,
    user_id: str,
    skip: int = 0,
    limit: int = 50
) -> list[schemas.XPTransaction]:
    """
    Obtiene el historial de transacciones de XP del usuario.
    """
    transactions = db.query(models.XPTransaction).filter(
        models.XPTransaction.user_id == user_id
    ).order_by(
        models.XPTransaction.created_at.desc()
    ).offset(skip).limit(limit).all()
    
    return transactions


def calculate_xp_for_quiz(score: int, total: int, duration_ms: int = None) -> int:
    """
    Calcula XP por completar un quiz.
    
    Base: 10 XP por pregunta correcta
    Bonus de precisión: +50% si 100% correcto
    Bonus de velocidad: +25% si completa en menos de 30 segundos
    """
    base_xp = score * 10
    
    # Bonus de precisión perfecta
    precision_bonus = 0
    if score == total and total > 0:
        precision_bonus = int(base_xp * 0.5)
    
    # Bonus de velocidad (menos de 30 segundos)
    speed_bonus = 0
    if duration_ms and duration_ms < 30000:
        speed_bonus = int(base_xp * 0.25)
    
    return base_xp + precision_bonus + speed_bonus


def calculate_xp_for_memory_game(matches: int, attempts: int) -> int:
    """
    Calcula XP por completar un memory game.
    
    Base: matches * 15 XP
    Bonus de eficiencia: +30% si attempts <= matches * 1.5
    """
    base_xp = matches * 15
    
    # Bonus por buena eficiencia
    efficiency_bonus = 0
    if attempts <= matches * 1.5:
        efficiency_bonus = int(base_xp * 0.3)
    
    return base_xp + efficiency_bonus


def calculate_xp_for_lesson() -> int:
    """
    XP fijo por completar una lección.
    """
    return 25


def calculate_streak_bonus(streak_days: int) -> int:
    """
    Calcula bonus de XP por mantener racha.
    
    - 3 días: +10 XP
    - 7 días: +25 XP
    - 14 días: +50 XP
    - 30 días: +100 XP
    - 50+ días: +200 XP
    """
    if streak_days >= 50:
        return 200
    elif streak_days >= 30:
        return 100
    elif streak_days >= 14:
        return 50
    elif streak_days >= 7:
        return 25
    elif streak_days >= 3:
        return 10
    return 0
