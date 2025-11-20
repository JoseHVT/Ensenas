"""
Script independiente para poblar la base de datos SQLite con datos de prueba
NO depende de app.database para evitar problemas con variables de entorno
"""

from sqlalchemy import create_engine, Column, String, TIMESTAMP, TEXT, INT, JSON
from sqlalchemy.ext.declarative import declarative_base
from sqlalchemy.orm import Session
from sqlalchemy.sql import func

# Crear Base declarativa
Base = declarative_base()

# Definir modelos mínimos (copias de app/models.py)
class Module(Base):
    __tablename__ = "modules"
    id = Column(INT, primary_key=True, autoincrement=True)
    code = Column(String(64), unique=True)
    title = Column(String(120), nullable=False)
    description = Column(TEXT, nullable=True)
    sort_order = Column(INT, default=0)
    created_at = Column(TIMESTAMP(timezone=True), server_default=func.now())

class Sign(Base):
    __tablename__ = "signs"
    id = Column(INT, primary_key=True, autoincrement=True)
    word = Column(String(120), nullable=False)
    category = Column(String(80), nullable=True)
    video_path = Column(String(512), nullable=False)
    thumb_path = Column(String(512), nullable=True)
    tags = Column(JSON, nullable=True)

# Crear engine
SQLITE_URL = "sqlite:///./ensenas.db"
engine = create_engine(SQLITE_URL, connect_args={"check_same_thread": False})

# Crear tablas
Base.metadata.create_all(bind=engine)

# Crear sesión
session = Session(engine)

try:
    print("🚀 Iniciando población de base de datos...")
    
    # 1. INSERTAR MÓDULOS
    modules_data = [
        Module(id=1, code='MOD_ABC', title='Abecedario', description='Aprende el abecedario en LSM letra por letra', sort_order=1),
        Module(id=2, code='MOD_NUM', title='Números', description='Aprende a contar del 0 al 100 en señas', sort_order=2),
        Module(id=3, code='MOD_COL', title='Colores', description='Aprende los colores básicos en LSM', sort_order=3),
        Module(id=4, code='MOD_ANI', title='Animales', description='Conoce las señas de animales comunes', sort_order=4),
        Module(id=5, code='MOD_SAL', title='Saludos y Cortesías', description='Expresiones básicas de cortesía', sort_order=5),
        Module(id=6, code='MOD_FAM', title='Familia', description='Miembros de la familia', sort_order=6),
        Module(id=7, code='MOD_COM', title='Comida', description='Alimentos y bebidas', sort_order=7),
        Module(id=8, code='MOD_HOG', title='Hogar', description='Objetos del hogar', sort_order=8)
    ]
    
    for module in modules_data:
        existing = session.query(Module).filter(Module.id == module.id).first()
        if not existing:
            session.add(module)
    
    session.commit()
    print(f"✅ {len(modules_data)} módulos insertados")
    
    # 2. INSERTAR SEÑAS DEL DICCIONARIO
    signs_data = [
        # Abecedario
        Sign(id=1, word='A', category='Abecedario', video_path='a', thumb_path='a', tags='["letra", "abecedario"]'),
        Sign(id=2, word='B', category='Abecedario', video_path='b', thumb_path='b', tags='["letra", "abecedario"]'),
        Sign(id=3, word='C', category='Abecedario', video_path='c', thumb_path='c', tags='["letra", "abecedario"]'),
        Sign(id=4, word='D', category='Abecedario', video_path='d', thumb_path='d', tags='["letra", "abecedario"]'),
        Sign(id=5, word='E', category='Abecedario', video_path='e', thumb_path='e', tags='["letra", "abecedario"]'),
        
        # Números
        Sign(id=6, word='0', category='Números', video_path='numero_0', thumb_path='numero_0', tags='["número", "básico"]'),
        Sign(id=7, word='1', category='Números', video_path='numero_1', thumb_path='numero_1', tags='["número", "básico"]'),
        Sign(id=8, word='2', category='Números', video_path='numero_2', thumb_path='numero_2', tags='["número", "básico"]'),
        Sign(id=9, word='3', category='Números', video_path='numero_3', thumb_path='numero_3', tags='["número", "básico"]'),
        Sign(id=10, word='4', category='Números', video_path='numero_4', thumb_path='numero_4', tags='["número", "básico"]'),
        
        # Colores
        Sign(id=11, word='Amarillo', category='Colores', video_path='amarillo_web', thumb_path='amarillo_web', tags='["color", "básico"]'),
        Sign(id=12, word='Azul', category='Colores', video_path='azul_web', thumb_path='azul_web', tags='["color", "básico"]'),
        Sign(id=13, word='Blanco', category='Colores', video_path='blanco_web', thumb_path='blanco_web', tags='["color", "básico"]'),
        Sign(id=14, word='Café', category='Colores', video_path='cafe_web', thumb_path='cafe_web', tags='["color", "básico"]'),
        Sign(id=15, word='Gris', category='Colores', video_path='gris_web', thumb_path='gris_web', tags='["color", "básico"]'),
        Sign(id=16, word='Morado', category='Colores', video_path='morado_web', thumb_path='morado_web', tags='["color", "básico"]'),
        Sign(id=17, word='Naranja', category='Colores', video_path='naranja_web', thumb_path='naranja_web', tags='["color", "básico"]'),
        Sign(id=18, word='Negro', category='Colores', video_path='negro_web', thumb_path='negro_web', tags='["color", "básico"]'),
        Sign(id=19, word='Oro', category='Colores', video_path='oro_web', thumb_path='oro_web', tags='["color", "intermedio"]'),
        Sign(id=20, word='Plata', category='Colores', video_path='plata_web', thumb_path='plata_web', tags='["color", "intermedio"]'),
        Sign(id=21, word='Rojo', category='Colores', video_path='rojo_web', thumb_path='rojo_web', tags='["color", "básico"]'),
        Sign(id=22, word='Rosa', category='Colores', video_path='rosa_web', thumb_path='rosa_web', tags='["color", "básico"]'),
        Sign(id=23, word='Verde', category='Colores', video_path='verde_web', thumb_path='verde_web', tags='["color", "básico"]'),
        
        # Animales
        Sign(id=24, word='Abeja', category='Animales', video_path='abeja_web', thumb_path='abeja_web', tags='["animal", "insecto"]'),
        Sign(id=25, word='Águila', category='Animales', video_path='aguila_web', thumb_path='aguila_web', tags='["animal", "ave"]'),
        Sign(id=26, word='Araña', category='Animales', video_path='arana_web', thumb_path='arana_web', tags='["animal", "insecto"]'),
        Sign(id=27, word='Ardilla', category='Animales', video_path='ardilla_web', thumb_path='ardilla_web', tags='["animal", "mamífero"]'),
        Sign(id=28, word='Burro', category='Animales', video_path='burro_web', thumb_path='burro_web', tags='["animal", "mamífero"]'),
        Sign(id=29, word='Caballo', category='Animales', video_path='caballo_web', thumb_path='caballo_web', tags='["animal", "mamífero"]'),
        Sign(id=30, word='Cerdo', category='Animales', video_path='cerdo_web', thumb_path='cerdo_web', tags='["animal", "granja"]'),
        Sign(id=31, word='Conejo', category='Animales', video_path='conejo_web', thumb_path='conejo_web', tags='["animal", "mamífero"]'),
        Sign(id=32, word='Gato', category='Animales', video_path='gato_web', thumb_path='gato_web', tags='["animal", "mascota"]'),
        Sign(id=33, word='Gorila', category='Animales', video_path='gorila_web', thumb_path='gorila_web', tags='["animal", "primate"]'),
        Sign(id=34, word='León', category='Animales', video_path='leon_web', thumb_path='leon_web', tags='["animal", "felino"]'),
        Sign(id=35, word='Oso', category='Animales', video_path='oso_web', thumb_path='oso_web', tags='["animal", "mamífero"]'),
        Sign(id=36, word='Pájaro', category='Animales', video_path='pajaro_web', thumb_path='pajaro_web', tags='["animal", "ave"]'),
        Sign(id=37, word='Pato', category='Animales', video_path='pato_web', thumb_path='pato_web', tags='["animal", "ave"]'),
        Sign(id=38, word='Perro', category='Animales', video_path='perro_web', thumb_path='perro_web', tags='["animal", "mascota"]'),
        Sign(id=39, word='Tigre', category='Animales', video_path='tigre_web', thumb_path='tigre_web', tags='["animal", "felino"]'),
        Sign(id=40, word='Toro', category='Animales', video_path='toro_web', thumb_path='toro_web', tags='["animal", "granja"]'),
        Sign(id=41, word='Tortuga', category='Animales', video_path='tortuga_web', thumb_path='tortuga_web', tags='["animal", "reptil"]'),
        Sign(id=42, word='Vaca', category='Animales', video_path='vaca_web', thumb_path='vaca_web', tags='["animal", "granja"]'),
        Sign(id=43, word='Víbora', category='Animales', video_path='vibora_web', thumb_path='vibora_web', tags='["animal", "reptil"]'),
    ]
    
    for sign in signs_data:
        existing = session.query(Sign).filter(Sign.id == sign.id).first()
        if not existing:
            session.add(sign)
    
    session.commit()
    print(f"✅ {len(signs_data)} señas insertadas en el diccionario")
    
    print("\n🎉 Base de datos poblada exitosamente!")
    print(f"📊 Total: {len(modules_data)} módulos, {len(signs_data)} señas")
    print(f"📁 Archivo creado: ensenas.db")

except Exception as e:
    print(f"❌ Error al poblar la base de datos: {e}")
    import traceback
    traceback.print_exc()
    session.rollback()

finally:
    session.close()
