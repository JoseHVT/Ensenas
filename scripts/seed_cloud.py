import requests
import json
import os

# CONFIGURACION
# aqui URL de Render (sin la barra al final)
API_URL = "https://ensenas-api.onrender.com" 
DATA_FILE = "data/initial_content.json"

def load_data():
    # 1. Leer el archivo JSON
    print(f" Leyendo datos de {DATA_FILE}...")
    try:
        with open(DATA_FILE, "r", encoding="utf-8") as f:
            data = json.load(f)
    except FileNotFoundError:
        print(" Error: No se encuentra el archivo de datos.")
        return

    # 2. Cargar Modulos
    print("\n---  Cargando MModulos ---")
    for module in data["modules"]:
        try:
            response = requests.post(f"{API_URL}/modules/", json=module)
            if response.status_code == 201:
                print(f" Modulo creado: {module['title']}")
            else:
                print(f" Error al crear modulo {module['title']}: {response.text}")
        except Exception as e:
            print(f" Error de conexion: {e}")

    # 3. Cargar Diccionario
    print("\n---  Cargando Diccionario ---")
    for sign in data["signs"]:
        try:
            response = requests.post(f"{API_URL}/dictionary/", json=sign)
            if response.status_code == 201:
                print(f" Sena creada: {sign['word']}")
            else:
                print(f" Error al crear sena {sign['word']}: {response.text}")
        except Exception as e:
            print(f" Error de conexion: {e}")
    print("\n ¡Carga masiva terminada! ")

if __name__ == "__main__":
    load_data()