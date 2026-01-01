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
# --- Define our fleet of machines ---
machines = [
    {"id": "cnc-machine-01", "normal_vib": (0.5, 2.0), "anomaly_vib": (5.0, 8.0)},
    {"id": "cnc-machine-02", "normal_vib": (0.8, 2.5), "anomaly_vib": (6.0, 9.0)},
    {"id": "stamping-press-01", "normal_vib": (2.0, 5.0), "anomaly_vib": (10.0, 15.0)}
]

print(f"🚀 Fleet simulation started for {len(machines)} machines.")

try:
    while True:
        # Loop through each machine and send its telemetry
        for machine in machines:
            vibration = round(random.uniform(*machine["normal_vib"]), 2)
            
            # Each machine has its own chance of having an anomaly
            if random.random() > 0.98:
                print(f"⚠️ ANOMALY INJECTED for {machine['id']}!")
                vibration = round(random.uniform(*machine["anomaly_vib"]), 2)

            payload = {
                "machine_id": machine["id"],
                "vibration": vibration,
                "timestamp": datetime.utcnow().isoformat() + "Z"
            }
            
            # Publish to the broker
            client.publish(topic, json.dumps(payload))
            print(f"📤 Sent Telemetry: {payload}")
        
        # Wait before the next fleet-wide reading
        time.sleep(3)

except KeyboardInterrupt:
    print("Stopping simulator...")
    client.loop_stop()
    client.disconnect()