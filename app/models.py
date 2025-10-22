from sqlalchemy import Column, String, TIMESTAMP
from sqlalchemy.sql import func
from .database import Base  # heredamos la clase que definimos en database.py

class User(Base):
    # El nombre de la tabla en la base de datos 
    __tablename__ = "users"

    # Definimos las columnas en base en el SRS

    # El uid que viene de Firebase 
    uid = Column(String(128), primary_key=True) 

    # El email del usuario 
    email = Column(String(255), unique=True, nullable=False, index=True)

    # El nombre 
    name = Column(String(120), nullable=True)

    # La fecha en que se creeo, manejada por la base de datos 
    created_at = Column(TIMESTAMP(timezone=True), 
                        server_default=func.now())
    