from fastapi import APIRouter, Depends, status
from sqlalchemy.orm import Session
from typing import List

from .. import crud, schemas # Importamos el crud y los esquemas
from ..dependencies import get_db # Importamos el conector a la BD

router = APIRouter(
    prefix="/modules",  # Todos los endpoints aqui empezaran con /modules
    tags=["Modules"]    # Se agruparán como "Modules" en /docs
)

@router.get("/", response_model=List[schemas.Module])
def read_modules(
    db: Session = Depends(get_db), 
    skip: int = 0, 
    limit: int = 100
):
    """
    Obtiene una lista de todos los mmdulos.
    """
    modules = crud.get_modules(db, skip=skip, limit=limit)
    return modules

@router.post("/", 
             response_model=schemas.Module, 
             status_code=status.HTTP_201_CREATED
)
def create_new_module(
    module: schemas.ModuleCreate, 
    db: Session = Depends(get_db)
):
    """
    Crea un nuevo módulo.
    (OJO: Por ahora no esta protegido, ¡es solo para probar!)
    """
    return crud.create_module(db=db, module=module)