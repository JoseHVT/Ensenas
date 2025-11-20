"""
Script para iniciar el servidor FastAPI fácilmente
Ejecuta con: python start_server.py
"""

import os
import sys

# Configurar variable de entorno
os.environ["DATABASE_URL"] = "sqlite:///./ensenas.db"

# Asegurar que el directorio actual está en el path
sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))

# Iniciar Uvicorn
if __name__ == "__main__":
    import uvicorn
    uvicorn.run(
        "app.main:app",
        host="0.0.0.0",
        port=8000,
        reload=False  # Desactivado para evitar problemas en Windows
    )
