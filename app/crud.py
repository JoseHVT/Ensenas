from sqlalchemy.orm import Session
from . import models, schemas # Importamos modelos y esquemas

# --- CRUD para modulos ---

def get_module(db: Session, module_id: int):
    """Obtiene un módulo específico por su ID"""
    # .filter() es el "WHERE" de SQL
    # .first() obtiene el primer resultado o None
    return db.query(models.Module).filter(models.Module.id == module_id).first()

def get_modules(db: Session, skip: int = 0, limit: int = 100):
    """Obtiene una lista de módulos, con paginación"""
    # .offset() es el "saltar" (skip)
    # .limit() es el "limite"
    # .all() obtiene todos los resultados
    return db.query(models.Module).offset(skip).limit(limit).all()

def create_module(db: Session, module: schemas.ModuleCreate):
    """Crea un nuevo módulo en la base de datos"""
    # 1. Convierte el esquema de Pydantic a un modelo de SQLAlchemy
    db_module = models.Module(**module.model_dump())
    # 2. anade el objeto a la sesion
    db.add(db_module)
    # 3. Confirma (guarda) los cambios en la BD
    db.commit()
    # 4. Refresca el objeto para obtener el ID generado por la BD
    db.refresh(db_module)
    return db_module

# --- CRUD para Lecciones

def get_lessons_by_module(db: Session, module_id: int):
    """Obtiene todas las lecciones de un modulo especifico"""
    return db.query(models.Lesson).filter(models.Lesson.module_id == module_id).all()