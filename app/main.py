from fastapi import FastAPI

# modulos de la base de datos
from . import models
from .database import engine

#routers

from .routers import users, modules, dictionary, quizzes, memory, progress, media

#----------------------------------------------

#logica de inicio

#  instancia de la app de FastAPI
app = FastAPI(title="EnSeñas API", version="1.0.0")

#para todos los modelos que heredan de Base, crea las tablas en la db si no existen
models.Base.metadata.create_all(bind=engine)

# ---------------------------------------------------

#  Le decimos a la app principal que incluya todas las rutas
app.include_router(users.router)
app.include_router(modules.router)
app.include_router(dictionary.router)
app.include_router(quizzes.router)
app.include_router(memory.router)
app.include_router(progress.router)
app.include_router(media.router)


#-----------------------------------------------------------

#endpoints
@app.get("/")
def read_root():
    return {"message": "¡Bienvenido al backend de EnSeñas!"}