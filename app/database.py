import os
from sqlalchemy import create_engine
from sqlalchemy.ext.declarative import declarative_base
from sqlalchemy.orm import sessionmaker
from dotenv import load_dotenv

# Carga las variables del archivo .env (como nuestro DATABASE_URL)
load_dotenv()

# Lee la URL de la base de datos desde la variable de entorno
SQLALCHEMY_DATABASE_URL = os.getenv("DATABASE_URL")

# Crea motor de de db
# El argumento check_same_thread=False es necesario solo para SQLite
engine = create_engine(
    SQLALCHEMY_DATABASE_URL, connect_args={"check_same_thread": False}
)

#sesion oara comunicarse con db
SessionLocal = sessionmaker(autocommit=False, autoflush=False, bind=engine)

# clase de todos nuestro modelos de datos

Base = declarative_base()
