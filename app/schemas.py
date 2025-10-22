from pydantic import BaseModel, EmailStr
from datetime import datetime
from typing import Optional

# esquema base para los usuarios
# campos comunes, por el momento
class UserBase(BaseModel):
    email: EmailStr  # pydanctic valida el email
    name: Optional[str] = None # optional == none

# Este esquema se usa cuando creamos un usuario (aunque Firebase lo haga).
# Hereda de UserBase y añade el uid.
class UserCreate(UserBase):
    uid: str

# Este es el esquema que usaremos para MOSTRAR un usuario desde la API.
# Hereda de UserBase y añade los campos que son seguros de mostrar.
class User(UserBase):
    uid: str
    created_at: datetime

    #le decimos a pydantic que lea los datos aun si son de un orm
    class Config:
        from_attributes = True
        