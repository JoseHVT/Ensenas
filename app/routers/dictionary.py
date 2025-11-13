from fastapi import APIRouter, Depends, Query, status
from sqlalchemy.orm import Session
from typing import List, Optional

# Importamos el módulo CRUD específico para el diccionario
from ..crud import dictionary as crud_dictionary
from .. import schemas
from ..dependencies import get_db

router = APIRouter(
    prefix="/dictionary",
    tags=["Dictionary"]
)

@router.get("/", response_model=List[schemas.Sign])
def search_dictionary(
    db: Session = Depends(get_db),
    skip: int = 0,
    limit: int = 20,
    # 'Query' nos permite añadir documentación y validación a los parámetros de la URL
    query: Optional[str] = Query(None, min_length=1, description="Texto a buscar por prefijo"),
    category: Optional[str] = Query(None, description="Filtrar por categoría exacta")
):
    """
    Busca señas en el diccionario.
    Permite filtrar por texto (prefijo) y categoría, con paginación.
    """
    return crud_dictionary.get_signs(
        db=db, 
        skip=skip, 
        limit=limit, 
        query=query, 
        category=category
    )

@router.post("/", 
             response_model=schemas.Sign, 
             status_code=status.HTTP_201_CREATED
)
def add_sign_to_dictionary(
    sign: schemas.SignCreate,
    db: Session = Depends(get_db)
):
    """
    Agrega una nueva seña al diccionario.
    """
    return crud_dictionary.create_sign(db=db, sign=sign)