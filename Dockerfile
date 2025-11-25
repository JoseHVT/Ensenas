# 1. Usamos una imagen oficial de Python ligera
FROM python:3.10-slim

# 2. Establecemos el directorio de trabajo dentro del contenedor
WORKDIR /app

# 3. Copiamos el archivo de requerimientos primero por los beneficios de cache de docker
COPY requirements.txt .

# 4. Instalamos las lib necesarias
# --no-cache-dir mantiene la imagen ligera
RUN pip install --no-cache-dir -r requirements.txt

# 5. Copiamos el resto del code al cont
COPY . .

# 6. Exponemos el puerto 8080 por estandar
EXPOSE 8080

# 7. El comando para arrancar la app cuando el contenedor se encienda
# Nota: Usamos "0.0.0.0" para que sea accesible desde fuera del contenedor
CMD ["uvicorn", "app.main:app", "--host", "0.0.0.0", "--port", "8080"]