import os
import time
import json
import random
import paho.mqtt.client as mqtt
from datetime import datetime

# 1. Load configuration from environment variables
broker = os.getenv("MQTT_BROKER_HOST", "mosquitto_broker")
port = int(os.getenv("MQTT_PORT", 1883))
topic = os.getenv("MQTT_TOPIC", "sensor/vibration")
username = os.getenv("MQTT_USER")
password = os.getenv("MQTT_PASS")

def on_connect(client, userdata, flags, rc):
    if rc == 0:
        print("✅ Connected to MQTT Broker!")
    else:
        print(f"❌ Connection failed with result code {rc}")

client = mqtt.Client()
client.on_connect = on_connect

# 2. IMPORTANT: Set the username and password for authentication
if username and password:
    client.username_pw_set(username, password)

try:
    client.connect(broker, port, 60)
except Exception as e:
    print(f"🚫 Could not connect to broker: {e}")

client.loop_start()

print(f"🚀 Simulator started. Sending to topic: {topic}")

try:
    while True:
        # Generate dummy vibration data
        vibration = round(random.uniform(0.5, 3.0), 2)
        
        # Randomly simulate an anomaly
        if random.random() > 0.95:
            print("⚠️ Simulating ANOMALY spike!")
            vibration = round(random.uniform(5.0, 10.0), 2)

        payload = {
            "machineId": "cnc-machine-01",
            "vibration": vibration,
            "timestamp": datetime.utcnow().isoformat() + "Z"
        }

        # 3. Publish to the broker
        result = client.publish(topic, json.dumps(payload))
        
        # Check if publish was successful
        if result.rc == mqtt.MQTT_ERR_SUCCESS:
            print(f"📤 Sent: {payload}")
        else:
            print(f"🛑 Failed to send message (Error code: {result.rc})")
            
        time.sleep(2)
except KeyboardInterrupt:
    print("Stopping simulator...")
    client.loop_stop()
    client.disconnect()