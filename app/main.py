from fastapi import FastAPI

#  instancia de la app de FastAPI
app = FastAPI(title="EnSeñas API", version="1.0.0")

# modulos de la base de datos
from . import models
from .database import engine

#para todos los modelos que heredan de Base, crea las tablas en la db si no existen
models.Base.metadata.create_all(bind=engine)
# -------------------------------

#  Le decimos a la app principal que incluya todas las rutas
#  que definimos en el archivo users.py
app.include_router(users.router)

@app.get("/")
def read_root():
    return {"message": "¡Bienvenido al backend de EnSeñas!"}