import json
import time
import random
import paho.mqtt.client as mqtt

# Configurações de Conexão
BROKER = "localhost" 
PORT = 1883
TOPIC = "industria/textil/maquina1"

def simular_dados_sensor():
    """Gera um payload JSON simulando sensores industriais"""
    return {
        "machine_id": "tear-01-mock",
        "timestamp": time.time(),
        "vibration": round(random.uniform(0.1, 0.8), 2),
        "temperature": random.randint(40, 55),
        "status": "active"
    }

# Configuração do Cliente MQTT
client = mqtt.Client(mqtt.CallbackAPIVersion.VERSION2)

try:
    client.connect(BROKER, PORT, 60)
    print(f" Conectado ao Broker em {BROKER}:{PORT}")
except Exception as e:
    print(f" Erro ao conectar no Broker: {e}")
    exit(1)

print(f" Iniciando simulação. Enviando dados para o tópico: {TOPIC}")

try:
    while True:
        dados = simular_dados_sensor()
        payload = json.dumps(dados)
        
        # Publica a mensagem no Broker
        client.publish(TOPIC, payload)
        
        print(f"📡 Enviado: {payload}")
        time.sleep(2) # Envia a cada 2 segundos
except KeyboardInterrupt:
    print("\n Simulação encerrada pelo usuário.")
    client.disconnect()