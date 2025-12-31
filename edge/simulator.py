import os
import json
import time
import random
import paho.mqtt.client as mqtt

# Environment variables or defaults
BROKER = os.getenv("MQTT_BROKER", "localhost") 
PORT = int(os.getenv("MQTT_PORT", 1883))
TOPIC = os.getenv("MQTT_TOPIC", "industry/textile/machine1")

client = mqtt.Client(mqtt.CallbackAPIVersion.VERSION2)

def connect_to_broker():
    while True:
        try:
            client.connect(BROKER, PORT, 60)
            print(f"Connected to Broker: {BROKER}")
            break
        except Exception as e:
            print(f"Waiting for Broker... ({e})")
            time.sleep(5)

connect_to_broker()

try:
    while True:
        data = {
            "machine_id": "loom-01-mock",
            "vibration": round(random.uniform(0.1, 0.8), 2),
            "timestamp": time.time()
        }
        client.publish(TOPIC, json.dumps(data))
        print(f"Sent to {BROKER}: {data}")
        time.sleep(2)
except KeyboardInterrupt:
    client.disconnect()
    print("Disconnected.")