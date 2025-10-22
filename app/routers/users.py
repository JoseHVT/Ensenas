from fastapi import APIRouter, Depends
from ..dependencies import get_current_user # .. para subir un nivel y encontrar dependencies.py
from ..schemas import User # .. para subir un nivel y encontrar schemas.py

# 1. Creamos un router, es como una mini-aplicación
router = APIRouter(
    prefix="/users",  # 2. Todos los endpoints aqui empezaran con /users
    tags=["Users"]    # 3. Los agrupara en la documentacion /docs
)

@router.get("/me")
async def read_users_me(
    current_user_uid: str = Depends(get_current_user)
):
    # Si el codigo llega aqua, el token es válido.
    return current_user_uid

# --- futuros endpoints ---
