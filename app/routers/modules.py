from fastapi import APIRouter, Depends, status
from sqlalchemy.orm import Session
from typing import List

from .. import schemas # Importamos el crud y los esquemas
from ..dependencies import get_db # Importamos el conector a la BD
from ..crud import modules as crud_modules


#router
router = APIRouter(
    prefix="/modules",  # Todos los endpoints aqui empezaran con /modules
    tags=["Modules"]    # Se agruparan como "Modules" en /docs
)

@router.get("/", response_model=List[schemas.Module])
def read_modules(
    db: Session = Depends(get_db), 
    skip: int = 0, 
    limit: int = 100
):
    """
    Obtiene una lista de todos los mmdulos
    """
    return crud_modules.get_modules(db, skip=skip, limit=limit)

@router.post("/", 
             response_model=schemas.Module, 
             status_code=status.HTTP_201_CREATED
)
def create_new_module(
    module: schemas.ModuleCreate, 
    db: Session = Depends(get_db)

    
):
    """
    Crea un nuevo modulo.
    prb
    """
    return crud_modules.create_module(db=db, module=module)