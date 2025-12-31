import os
import json
import time
import random
import paho.mqtt.client as mqtt

# Lemos as configurações de variáveis de ambiente (ou usamos um padrão)
BROKER = os.getenv("MQTT_BROKER", "localhost") 
PORT = int(os.getenv("MQTT_PORT", 1883))
TOPIC = os.getenv("MQTT_TOPIC", "industria/textil/maquina1")

client = mqtt.Client(mqtt.CallbackAPIVersion.VERSION2)

# Função para tentar conectar até conseguir
def connect_to_broker():
    while True:
        try:
            client.connect(BROKER, PORT, 60)
            print(f"✅ Conectado ao Broker: {BROKER}")
            break
        except Exception as e:
            print(f"⏳ Aguardando Broker... ({e})")
            time.sleep(5)

connect_to_broker()

try:
    while True:
        dados = {
            "machine_id": "tear-01-mock",
            "vibration": round(random.uniform(0.1, 0.8), 2),
            "timestamp": time.time()
        }
        client.publish(TOPIC, json.dumps(dados))
        print(f"📡 Enviado para {BROKER}: {dados}")
        time.sleep(2)
except KeyboardInterrupt:
    client.disconnect()