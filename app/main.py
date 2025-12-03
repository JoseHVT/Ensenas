from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware #  CORS para que no se bloquee la app al hacer peticiones

# ---------------------------------------------
# modulos de la base de datos
from . import models
from .database import engine

#routers

from .routers import users, modules, dictionary, quizzes, memory, progress, media, missions, lessons

#----------------------------------------------

#logica de inicio

#  instancia de la app de FastAPI
app = FastAPI(title="EnSeñas API", version="1.0.0")

# Configuracion de CORS (Cross-Origin Resource Sharing)
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],  # Permitir todas las fuentes (en produccion, especificar dominios)
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)
#osea, cualu=quiere origen '*' se puede conectar a la api
# -------------------------------------------
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