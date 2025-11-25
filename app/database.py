import os
from sqlalchemy import create_engine
from sqlalchemy.ext.declarative import declarative_base
from sqlalchemy.orm import sessionmaker
# from dotenv import load_dotenv para local

# Carga las variables del archivo .env (como nuestro DATABASE_URL)
# load_dotenv() para local

# Lee la URL de la base de datos desde la variable de entorno
# Si no existe, usa una base de datos SQLite (local)
DATABASE_URL = os.getenv("DATABASE_URL", "sqlite:///./enseñas.db")

#validamos que el driver sea compatible con el motor
if DATABASE_URL.startswith("mysql://"):
    DATABASE_URL = DATABASE_URL.replace("mysql://", "mysql+pymysql://")

# Crea motor de de db

#configuramos  para mysql y sqlite

if "sqlite" in DATABASE_URL:
    engine = create_engine(
      DATABASE_URL, connect_args={"check_same_thread": False} 
    ) # El argumento check_same_thread=False es necesario solo para SQLite

else:
    #mysql
    #pool_recycle para evitar errores de conexion por timeout
    engine = create_engine(
      DATABASE_URL, pool_recycle=3600
    )

#sesion oara comunicarse con db
SessionLocal = sessionmaker(autocommit=False, autoflush=False, bind=engine)

# clase de todos nuestro modelos de datos

Base = declarative_base()
