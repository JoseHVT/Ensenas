from sqlalchemy.orm import Session
from .. import models, schemas

def get_signs(
    db: Session, 
    skip: int = 0, 
    limit: int = 20, 
    query: str = None, 
    category: str = None
):
    db_query = db.query(models.Sign)
    if query:
        search = f"{query}%"
        db_query = db_query.filter(models.Sign.word.ilike(search))
    if category:
        db_query = db_query.filter(models.Sign.category == category)
    return db_query.offset(skip).limit(limit).all()

def create_sign(db: Session, sign: schemas.SignCreate):
    db_sign = models.Sign(**sign.model_dump())
    db.add(db_sign)
    db.commit()
    db.refresh(db_sign)
    return db_sign