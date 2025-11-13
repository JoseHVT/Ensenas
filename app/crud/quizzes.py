from sqlalchemy.orm import Session
from .. import models, schemas
from datetime import datetime

def get_quiz_by_module(db: Session, module_id: int):
    """Obtiene todos los quizzes de un modulo"""
    return db.query(models.Quiz).filter(models.Quiz.module_id == module_id).all()

def get_quiz(db: Session, quiz_id: int):
    """Obtiene un quiz especifico por su ID"""
    return db.query(models.Quiz).filter(models.Quiz.id == quiz_id).first()

def create_quiz_attempt(db: Session, attempt: schemas.QuizAttemptCreate, user_id: str):
    """
    Registra un intento de quiz
    Calcula la calificación comparando las respuestas del usuario con las correctas
    """
    # 1. Obtenemos el quiz original con sus preguntas
    quiz = get_quiz(db, quiz_id=attempt.quiz_id)
    if not quiz:
        return None

    # 2. Calculamos la calificacion
    score = 0
    total_questions = len(quiz.questions)
    
    # Convertimos las respuestas del usuario a un diccionario para facil acceso
    # Suponemos que 'attempt.answers' es un dict: { "question_id": "respuesta_usuario" }
    user_answers = attempt.answers 
    
    for question in quiz.questions:
        # Buscamos si el usuario respondio esta pregunta
        user_answer = user_answers.get(str(question.id))
        
        if user_answer and user_answer.lower() == question.answer.lower():
             score += 1

    # 3. Creamos el registro del intento
    db_attempt = models.QuizAttempt(
        user_id=user_id,
        quiz_id=attempt.quiz_id,
        score=score,
        total=total_questions,
        duration_ms=attempt.duration_ms,
        created_at=datetime.now()
    )
    
    # 4. Guardamos en la BD
    db.add(db_attempt)
    db.commit()
    db.refresh(db_attempt)
    
    return db_attempt

def get_user_attempts(db: Session, user_id: str, skip: int = 0, limit: int = 50):
    """Obtiene el historial de intentos de un usuario."""
    return db.query(models.QuizAttempt)\
             .filter(models.QuizAttempt.user_id == user_id)\
             .order_by(models.QuizAttempt.created_at.desc())\
             .offset(skip).limit(limit).all()