from pydantic import BaseModel, EmailStr, conint
from datetime import datetime
from typing import Optional, List, Any

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
        

#esquema d e modulos y lecciones
class LessonBase(BaseModel):
    title: str
    sort_order: Optional[int] = 0

class LessonCreate(LessonBase):
    pass

class Lesson(LessonBase):
    id: int
    module_id: int
    class Config:
        from_attributes = True

class ModuleBase(BaseModel):
    title: str
    description: Optional[str] = None
    code: Optional[str] = None
    sort_order: Optional[int] = 0

class ModuleCreate(ModuleBase):
    pass

class Module(ModuleBase):
    id: int
    # Esto le dice a Pydantic que anide la lista de lecciones
    # dentro de la respuesta del modulo
    lessons: List[Lesson] = [] 
    class Config:
        from_attributes = True

# --- Esquemas de Diccionario ---
class SignBase(BaseModel):
    word: str
    category: Optional[str] = None
    video_path: str
    thumb_path: Optional[str] = None
    tags: Optional[List[str]] = None

class SignCreate(SignBase):
    pass

class Sign(SignBase):
    id: int
    class Config:
        from_attributes = True

# --- Esquemas de Quizzes ---

class QuizQuestionBase(BaseModel):
    prompt: str
    options: Optional[dict] = None # Para JSON
    answer: Optional[str] = None

class QuizQuestionCreate(QuizQuestionBase):
    pass

class QuizQuestion(QuizQuestionBase):
    id: int
    quiz_id: int
    class Config:
        from_attributes = True

class QuizBase(BaseModel):
    title: str
    type: str # 'multiple_choice', 'complete', 'pair'

class QuizCreate(QuizBase):
    pass

class Quiz(QuizBase):
    id: int
    module_id: int
    questions: List[QuizQuestion] = []
    class Config:
        from_attributes = True

#esquema de progreso y stats
class UserModuleProgressBase(BaseModel):
    # conint = integer con restricciones (0 a 100)
    percent: conint(ge=0, le=100) 

class UserModuleProgressCreate(UserModuleProgressBase):
    module_id: int

class UserModuleProgress(UserModuleProgressBase):
    user_id: str
    module_id: int
    last_activity: datetime
    class Config:
        from_attributes = True

class QuizAttemptBase(BaseModel):
    score: int
    total: int
    duration_ms: Optional[int] = None

class QuizAttemptCreate(QuizAttemptBase):
    quiz_id: int
    answers: Any # Recibimos las respuestas en cualquier formato

class QuizAttempt(QuizAttemptBase):
    id: int
    user_id: str
    quiz_id: int
    created_at: datetime
    class Config:
        from_attributes = True

# --- Esquemas de SignPair (Memory Match) ---
class SignPairBase(BaseModel):
    word: str
    sign_id: int

class SignPairCreate(SignPairBase):
    pass

class SignPair(SignPairBase):
    id: int
    # metemos el objeto sign para que la app tenga el video o imagen como url.
    sign: Sign

    class Config:
        from_attributes = True

class MemoryRunBase(BaseModel):
    matches: int
    attempts: int
    streak: Optional[int] = None
    duration_ms: int
    module_id: Optional[int] = None
    streak: Optional[int] = None

class MemoryRunCreate(MemoryRunBase):
    pass

class MemoryRun(MemoryRunBase):
    id: int
    user_id: str
    created_at: datetime
    
    class Config:
        from_attributes = True

#--estats

class StatsSummary(BaseModel):
    precision_global: float = 0.0
    tiempo_total_ms: int = 0
    racha_actual: int = 0
    senas_dominadas: int = 0

# --- Esquemas de Racha Diaria ---

class DailyActivityBase(BaseModel):
    quizzes_completed: int = 0
    lessons_completed: int = 0
    memory_games_completed: int = 0
    xp_earned: int = 0

class DailyActivityCreate(DailyActivityBase):
    pass

class DailyActivity(DailyActivityBase):
    id: int
    user_id: str
    activity_date: datetime
    created_at: datetime
    
    class Config:
        from_attributes = True

class StreakInfo(BaseModel):
    """Información completa de la racha del usuario"""
    current_streak: int
    longest_streak: int
    last_activity_date: Optional[datetime] = None
    weekly_calendar: List[bool]  # 7 días, True si hubo actividad
    total_active_days: int

class StreakUpdate(BaseModel):
    """Payload para actualizar la actividad del día"""
    activity_type: str  # 'quiz', 'lesson', 'memory_game'
    xp_earned: int = 0

# --- Esquemas de XP y Niveles ---

class XPTransactionBase(BaseModel):
    amount: int
    source: str  # 'quiz', 'memory_game', 'lesson', 'streak_bonus', 'achievement'
    source_id: Optional[int] = None
    description: Optional[str] = None

class XPTransactionCreate(XPTransactionBase):
    pass

class XPTransaction(XPTransactionBase):
    id: int
    user_id: str
    created_at: datetime
    
    class Config:
        from_attributes = True

class UserLevelInfo(BaseModel):
    """Información de nivel del usuario"""
    total_xp: int
    current_level: int
    level_title: str
    xp_for_current_level: int  # XP necesario que ya pasó
    xp_for_next_level: int     # XP necesario para siguiente nivel
    current_level_xp: int      # XP actual en el nivel
    required_xp: int           # XP requerido para subir
    progress: float            # Progreso 0.0 - 1.0

class XPAwardRequest(BaseModel):
    """Request para otorgar XP al usuario"""
    amount: int
    source: str
    source_id: Optional[int] = None
    description: Optional[str] = None

class XPAwardResponse(BaseModel):
    """Response al otorgar XP"""
    xp_awarded: int
    total_xp: int
    previous_level: int
    current_level: int
    level_up: bool  # True si subió de nivel
    level_info: UserLevelInfo