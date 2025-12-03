import requests
import json
import os

# CONFIGURACIÓN
API_URL = "https://ensenas-api.onrender.com"

# Solo necesitamos el archivo de lecciones
LESSONS_FILE = "data/lessons.json"

def load_lessons_only():
    if not os.path.exists(LESSONS_FILE):
        print(f"❌ Error: No encuentro el archivo {LESSONS_FILE}")
        return

    print("🔄 Paso 1: Descargando mapa de módulos existentes...")
    modules_map = {}
    
    try:
        # Pedimos TODOS los módulos al servidor para saber sus IDs reales
        resp = requests.get(f"{API_URL}/modules/?limit=100")
        if resp.status_code == 200:
            modules_list = resp.json()
            for m in modules_list:
                # Mapeamos el código (MOD-01) al ID real (5)
                if m.get("code"):
                    modules_map[m["code"]] = m["id"]
            print(f"✅ Mapa construido: {len(modules_map)} módulos encontrados en la BD.")
        else:
            print(f"❌ Error al descargar módulos: {resp.status_code} - {resp.text}")
            return
    except Exception as e:
        print(f"❌ Error crítico de conexión: {e}")
        return

    if not modules_map:
        print("⚠️ Alerta: No se encontraron módulos en la base de datos. Las lecciones no se pueden cargar.")
        return

    print("\n🚀 Paso 2: Cargando Lecciones...")
    try:
        with open(LESSONS_FILE, "r", encoding="utf-8") as f:
            lessons_data = json.load(f)
        
        count_ok = 0
        count_err = 0
        
        for lesson in lessons_data:
            # Obtenemos el código (ej. MOD-01) pero NO lo borramos del dict original
            # por si necesitamos reusar el objeto, aunque pop es seguro aquí.
            mod_code = lesson.get("module_code")
            
            if mod_code and mod_code in modules_map:
                mod_id = modules_map[mod_code]
                
                # Preparamos el objeto limpio para enviar (sin module_code)
                payload = {
                    "title": lesson["title"],
                    "sort_order": lesson["sort_order"]
                }

                try:
                    # POST /lessons/?module_id=X
                    resp = requests.post(f"{API_URL}/lessons/?module_id={mod_id}", json=payload)
                    
                    if resp.status_code == 201:
                        print(f"   ✅ [{mod_code}] Lección creada: {lesson['title']}")
                        count_ok += 1
                    else:
                        print(f"   ⚠️ Error: {resp.text}")
                        count_err += 1
                except Exception as e:
                     print(f"   ❌ Error red: {e}")
            else:
                print(f"   ⏩ Saltada: El módulo '{mod_code}' no existe en el mapa.")
        
        print(f"\n✨ Resumen: {count_ok} creadas, {count_err} fallidas.")

    except Exception as e:
        print(f"❌ Error leyendo el archivo JSON: {e}")

if __name__ == "__main__":
    load_lessons_only()