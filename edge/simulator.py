import os
import time
import json
import random
import paho.mqtt.client as mqtt
from datetime import datetime

# Load configuration from Environment Variables (Cloud/Docker Native)
broker = os.getenv("MQTT_BROKER_HOST", "localhost")
port = int(os.getenv("MQTT_PORT", 1883))
topic = os.getenv("MQTT_TOPIC", "sensor/vibration")
username = os.getenv("MQTT_USER")
password = os.getenv("MQTT_PASS")

def on_connect(client, userdata, flags, rc):
    if rc == 0:
        print("✅ Connected to MQTT Broker!")
    else:
        print(f"❌ Connection failed. Code: {rc}")

client = mqtt.Client()
client.on_connect = on_connect

if username and password:
    client.username_pw_set(username, password)

try:
    client.connect(broker, port, 60)
except Exception as e:
    print(f"🚫 Broker connection error: {e}")

client.loop_start()

print(f"🚀 Simulation started. Target topic: {topic}")

try:
    while True:
        # Generate base vibration data
        vibration = round(random.uniform(0.5, 3.0), 2)
        
        # Anomaly Injection Logic:
        # Simulate a mechanical fault with a 5% probability
        if random.random() > 0.95:
            print("⚠️ ANOMALY INJECTED!")
            vibration = round(random.uniform(5.0, 10.0), 2)

        # Standardized Payload Contract
        payload = {
            "machine_id": "cnc-machine-01",
            "vibration": vibration,
            "timestamp": datetime.utcnow().isoformat() + "Z"
        }

        # Send data to the messaging layer
        client.publish(topic, json.dumps(payload))
        print(f"📤 Sent Telemetry: {payload}")
            
        time.sleep(2)
except KeyboardInterrupt:
    print("Stopping simulator...")
    client.loop_stop()
    client.disconnect()