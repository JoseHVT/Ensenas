import requests
import json
import os

# CONFIGURACION
# aqui URL de Render (sin la barra al final)
API_URL = "https://ensenas-api.onrender.com" 
DATA_FILE = "data/initial_content.json"
QUIZZES_FILE = "data/quizzes.json"
LESSONS_FILE = "data/lessons.json"

def load_data():

    # 1. Leer el archivo JSON

    print(f" Leyendo datos de {DATA_FILE}...")
    try:
        with open(DATA_FILE, "r", encoding="utf-8") as f:
            data = json.load(f)
    except FileNotFoundError:
        print(" Error: No se encuentra el archivo de datos.")
        return
    
    # Mapa para guardar "MOD-01" -> ID 5 (Lo usaremos para los quizzes)
    modules_map = {}

    # 2. Cargar Modulos
    print("\n---  Cargando MModulos ---")
    for module in data["modules"]:
        try:
            response = requests.post(f"{API_URL}/modules/", json=module)

            if response.status_code in [200, 201]:
                mod_data = response.json()
                mod_id = mod_data["id"]
                mod_code = mod_data.get("code")
                print(f"mdulo: {module['title']} (ID: {mod_id})")
                
                if mod_code:
                    modules_map[mod_code] = mod_id
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

    #4. cargar lecciones
    if os.path.exists(LESSONS_FILE):
        print("\n--- Cargando Lecciones ---")
        try:
            with open(LESSONS_FILE, "r", encoding="utf-8") as f:
                lessons_data = json.load(f)
            
            for lesson in lessons_data:
                mod_code = lesson.pop("module_code", None)
                
                if mod_code and mod_code in modules_map:
                    mod_id = modules_map[mod_code]
                    resp = requests.post(f"{API_URL}/modules/{mod_id}/lessons", json=lesson)
                    if resp.status_code == 201:
                        print(f"Leccion creada: {lesson['title']}")
                    else:
                        print(f" Error leccion '{lesson['title']}': {resp.text}")
                else:
                    print(f" Saltada: mood not found {mod_code}")
        except Exception as e:
            print(f"Error cargando lecciones: {e}")
    else:
        print(f"no ncontrado {LESSONS_FILE}, saltando lecciones.")

    # 5. Cargar Quizzes
    if os.path.exists(QUIZZES_FILE):
        print("\n---Cargando Quizzes ---")
        try:
            with open(QUIZZES_FILE, "r", encoding="utf-8") as f:
                quizzes_data = json.load(f)
            
            for quiz in quizzes_data:
                mod_code = quiz.pop("module_code", None)
                
                if mod_code and mod_code in modules_map:
                    mod_id = modules_map[mod_code]
                    # Usamos el endpoint create-full
                    resp = requests.post(f"{API_URL}/quizzes/?module_id={mod_id}", json=quiz)
                    
                    if resp.status_code == 201:
                        print(f"Quiz creado: {quiz['title']}")
                    else:
                        print(f"Error quiz: {resp.text}")
                else:
                    print(f"Saltado quiz '{quiz['title']}': Sin mod {mod_code}")
        except Exception as e:
            print(f"Error cargando quizzes: {e}")

    print("\ncarga lista")
if __name__ == "__main__":
    load_data()
