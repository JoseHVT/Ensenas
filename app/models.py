from sqlalchemy import Column, String, TIMESTAMP, TEXT, INT, INT, ForeignKey, JSON, Enum
from sqlalchemy.dialects.mysql import TINYINT
from sqlalchemy.sql import func
from .database import Base  # heredamos la clase que definimos en database.py
from sqlalchemy.orm import relationship # Importante para las relaciones

class User(Base):
    # El nombre de la tabla en la base de datos 
    __tablename__ = "users"

    # Definimos las columnas en base en el SRS

    # El uid que viene de Firebase 
    uid = Column(String(128), primary_key=True) 

    # El email del usuario 
    email = Column(String(255), unique=True, nullable=False, index=True)

    # El nombre 
    name = Column(String(120), nullable=True)

    # Sistema de XP y Niveles
    total_xp = Column(INT, default=0, nullable=False)
    current_level = Column(INT, default=1, nullable=False)

    # La fecha en que se creeo, manejada por la base de datos 
    created_at = Column(TIMESTAMP(timezone=True), 
                        server_default=func.now())
    
    #relaciones. Un usuario puede tener muchos progresos, intentos de quiz y corridas de memoria
    progress = relationship("UserModuleProgress", back_populates="user")
    quiz_attempts = relationship("QuizAttempt", back_populates="user")
    memory_runs = relationship("MemoryRun", back_populates="user")


    #catalogo

class Module(Base):
    __tablename__ = "modules"
    id = Column(INT, primary_key=True, autoincrement=True)
    code = Column(String(64), unique=True)
    title = Column(String(120), nullable=False)
    description = Column(TEXT, nullable=True)
    sort_order = Column(INT, default=0)
    created_at = Column(TIMESTAMP(timezone=True), server_default=func.now())
    
    #rel
    lessons = relationship("Lesson", back_populates="module")
    quizzes = relationship("Quiz", back_populates="module")
    progress = relationship("UserModuleProgress", back_populates="module")
    memory_runs = relationship("MemoryRun", back_populates="module")

class Lesson(Base):
    __tablename__ = "lessons"
    id = Column(INT, primary_key=True, autoincrement=True)
    module_id = Column(INT, ForeignKey("modules.id"), nullable=False)
    title = Column(String(120), nullable=False)
    sort_order = Column(INT, default=0)
    
    #rel
    module = relationship("Module", back_populates="lessons")

# --- Diccionario ---

class Sign(Base):
    __tablename__ = "signs"
    id = Column(INT, primary_key=True, autoincrement=True)
    word = Column(String(120), nullable=False, index=True) # [cite: 192, 200]
    category = Column(String(80), nullable=True) # [cite: 193]
    video_path = Column(String(512), nullable=False) # [cite: 195]
    thumb_path = Column(String(512), nullable=True) # [cite: 195]
    tags = Column(JSON, nullable=True) # [cite: 198]
    
    #rel
    sign_pairs = relationship("SignPair", back_populates="sign")

# --- Quizzes ---

class Quiz(Base):
    __tablename__ = "quizzes"
    id = Column(INT, primary_key=True, autoincrement=True)
    module_id = Column(INT, ForeignKey("modules.id"), nullable=False)
    type = Column(Enum('multiple_choice', 'complete', 'pair'), nullable=False) # [cite: 211]
    title = Column(String(160))

    module = relationship("Module", back_populates="quizzes")
    questions = relationship("QuizQuestion", back_populates="quiz")
    attempts = relationship("QuizAttempt", back_populates="quiz")

class QuizQuestion(Base):
    __tablename__ = "quiz_questions"
    id = Column(INT, primary_key=True, autoincrement=True)
    quiz_id = Column(INT, ForeignKey("quizzes.id"), nullable=False)
    prompt = Column(TEXT, nullable=False) # [cite: 218]
    options = Column(JSON, nullable=True) # [cite: 220]
    answer = Column(String(255), nullable=True) # [cite: 222]

    quiz = relationship("Quiz", back_populates="questions")

# --- Memory Match ---

class SignPair(Base):
    __tablename__ = "sign_pairs"
    id = Column(INT, primary_key=True, autoincrement=True)
    word = Column(String(120), nullable=False) # [cite: 230]
    sign_id = Column(INT, ForeignKey("signs.id"), nullable=False) # [cite: 231]

    sign = relationship("Sign", back_populates="sign_pairs")

# --- Progreso y estats ---

class UserModuleProgress(Base):
    __tablename__ = "user_module_progress"
    user_id = Column(String(128), ForeignKey("users.uid"), primary_key=True) # [cite: 238]
    module_id = Column(INT, ForeignKey("modules.id"), primary_key=True) # [cite: 240]
    percent = Column(INT, nullable=False, default=0) # [cite: 242]
    last_activity = Column(TIMESTAMP(timezone=True), server_default=func.now(), onupdate=func.now()) # [cite: 244]

    user = relationship("User", back_populates="progress")
    module = relationship("Module", back_populates="progress")

class QuizAttempt(Base):
    __tablename__ = "quiz_attempts"
    id = Column(INT, primary_key=True, autoincrement=True)
    user_id = Column(String(128), ForeignKey("users.uid"), nullable=False, index=True) # [cite: 253]
    quiz_id = Column(INT, ForeignKey("quizzes.id"), nullable=False) # [cite: 255]
    score = Column(INT, nullable=False) # [cite: 257]
    total = Column(INT, nullable=False) # [cite: 259]
    duration_ms = Column(INT, nullable=True) # [cite: 261]
    created_at = Column(TIMESTAMP(timezone=True), server_default=func.now()) # [cite: 262]

    user = relationship("User", back_populates="quiz_attempts")
    quiz = relationship("Quiz", back_populates="attempts")

class MemoryRun(Base):
    __tablename__ = "memory_runs"
    id = Column(INT, primary_key=True, autoincrement=True)
    user_id = Column(String(128), ForeignKey("users.uid"), nullable=False) # [cite: 269]
    module_id = Column(INT, ForeignKey("modules.id"), nullable=True) # [cite: 271]
    matches = Column(INT) # [cite: 273]
    attempts = Column(INT) # [cite: 275]
    streak = Column(INT) # [cite: 277]
    duration_ms = Column(INT) # [cite: 280]
    created_at = Column(TIMESTAMP(timezone=True), server_default=func.now()) # [cite: 281]

    user = relationship("User", back_populates="memory_runs")
    module = relationship("Module", back_populates="memory_runs")

class DailyActivity(Base):
    """
    Registra la actividad diaria del usuario para calcular rachas.
    Se crea un registro cada día que el usuario complete alguna actividad.
    """
    __tablename__ = "daily_activities"
    id = Column(INT, primary_key=True, autoincrement=True)
    user_id = Column(String(128), ForeignKey("users.uid"), nullable=False, index=True)
    activity_date = Column(TIMESTAMP(timezone=True), nullable=False, index=True)
    # Contador de actividades completadas en el día
    quizzes_completed = Column(INT, default=0)
    lessons_completed = Column(INT, default=0)
    memory_games_completed = Column(INT, default=0)
    xp_earned = Column(INT, default=0)
    created_at = Column(TIMESTAMP(timezone=True), server_default=func.now())
    
    user = relationship("User")

class XPTransaction(Base):
    """
    Registra cada ganancia de XP del usuario para auditoría y análisis.
    """
    __tablename__ = "xp_transactions"
    id = Column(INT, primary_key=True, autoincrement=True)
    user_id = Column(String(128), ForeignKey("users.uid"), nullable=False, index=True)
    amount = Column(INT, nullable=False)  # Cantidad de XP ganado
    source = Column(String(50), nullable=False)  # 'quiz', 'memory_game', 'lesson', 'streak_bonus', 'achievement'
    source_id = Column(INT, nullable=True)  # ID del quiz, memory game, etc.
    description = Column(String(255), nullable=True)  # Descripción adicional
    created_at = Column(TIMESTAMP(timezone=True), server_default=func.now(), index=True)
    
    user = relationship("User")