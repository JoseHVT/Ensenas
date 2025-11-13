from sqlalchemy.orm import Session
from sqlalchemy.sql.expression import func # Para aleatorio (RANDOM)
from .. import models, schemas
from datetime import datetime

def create_sign_pair(db: Session, sign_id: int, word: str):
    """Crea un par de palabra-seña para el juego."""
    # Primero, verifica que la seña exista
    sign = db.query(models.Sign).filter(models.Sign.id == sign_id).first()
    if not sign:
        return None
        
    db_pair = models.SignPair(word=word, sign_id=sign_id)
    db.add(db_pair)
    db.commit()
    db.refresh(db_pair)
    return db_pair

def get_memory_deck(db: Session, size: int = 8):
    """
    Obtiene un mazo aleatorio de pares para el juego.
    'size' es el numero de PARES (ej. 8 pares = 16 cartas).
    """
    # .order_by(func.random()) es aleatorio (funciona en SQLite/PostgreSQL)
    # .limit(size) toma el numero de pares que pedimos
    return db.query(models.SignPair).order_by(func.random()).limit(size).all()

def create_memory_run(db: Session, run: schemas.MemoryRunCreate, user_id: str):
    """Guarda el resultado de una partida de memorama."""
    db_run = models.MemoryRun(
        **run.model_dump(), # Pasa todos los campos (matches, attempts, etc.)
        user_id=user_id,
        created_at=datetime.now()
    )
    db.add(db_run)
    db.commit()
    db.refresh(db_run)
    return db_run