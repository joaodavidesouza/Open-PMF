# Open-PMF (Open Predictive Maintenance Framework) — v1.0

Open-PMF is an **open, cloud-agnostic framework for industrial predictive maintenance**, designed to provide a realistic, end-to-end pipeline from edge data acquisition to backend processing and analytics.

This project is **not a ready-made product**, but a **solid architectural foundation** that can be adapted to different industrial contexts, machines, and business needs.

---

## 🚧 Project Status: Stable Monitoring Foundation

Open-PMF has reached its **first major milestone**: a stable, scalable, and fully portable monitoring system.  

- The **full pipeline** — from multi-machine simulation to dynamic dashboards — is fully operational.  
- All core components are containerized with Docker for **reproducible deployments**.  
- The system provides a **baseline for industrial telemetry**, ready for advanced predictive modeling in future iterations.

---

## 🏗️ Architecture Overview

```mermaid
graph TD
    subgraph "Edge Layer"
        A[Python Fleet Simulator]
    end
    
    subgraph "Messaging & Ingestion"
        A -- "MQTT" --> B(Mosquitto Broker)
        B -- "TCP" --> C{Spring Boot Backend}
    end

    subgraph "Storage & Visualization"
        C -- "JDBC" --> D[(TimescaleDB)]
        E[Grafana] -- "SQL Queries" --> D
    end

    subgraph "Future Extensions"
        C -.-> F(Alerting Services)
        D -.-> G(ML Training Services)
    end
````

**Layer Breakdown:**

1. **Edge Layer**

   * Python-based simulator modeling a **fleet of machines**, each with unique operational profiles and anomalies.

2. **Messaging Layer**

   * Secure **MQTT broker** (Eclipse Mosquitto) with authentication and decoupled producer-consumer design.

3. **Backend Layer**

   * Modular **Spring Boot application** responsible for ingestion, validation, and persistence.
   * Clean separation between transport, domain logic, and storage.

4. **Storage Layer**

   * **TimescaleDB / PostgreSQL** with hypertables for efficient high-frequency time-series data storage.

5. **Visualization Layer**

   * **Grafana dashboards** automatically provisioned, with dynamic panels for any number of machines.
   * Supports live anomaly highlighting and fleet-wide monitoring.

---

## 🚀 Quick Start

**Run the full stack locally in a few commands:**

1. **Clone the repository:**

```bash
git clone https://github.com/joaodavidesouza/Open-PMF.git
cd Open-PMF
```

2. **Configure environment variables:**

```bash
cp .env.example .env
# Edit if you need to change credentials or ports
```

3. **Launch the stack:**

```bash
docker-compose up -d
```

4. **Access the Grafana dashboards:**

* Open your browser: [http://localhost:3000](http://localhost:3000)
* Default login: `admin / admin`
* Navigate to **Dashboards → Industrial → Fleet Overview** to see live vibration data.

5. **Verify database ingestion (optional):**

```bash
docker exec -it timescaledb \
  psql -U admin -d openpmf \
  -c "SELECT * FROM sensor_measurements ORDER BY timestamp DESC LIMIT 10;"
```

---

## 📦 Key Features (v1.0)

* **Dynamic Multi-Machine Simulation:** Fleet of machines with individual vibration and anomaly profiles.
* **Anomaly Injection:** Random high-vibration events to test monitoring.
* **Secure MQTT Ingestion:** Authentication and decoupled architecture for realistic security posture.
* **Optimized Time-Series Storage:** TimescaleDB hypertables handle high-frequency data efficiently.
* **Fully Dockerized Environment:** Single-command deployment ensures reproducibility.
* **Automated Grafana Provisioning:** Datasource and dynamic fleet dashboard deployed automatically.
* **Scalable Visualization:** Panels repeat per machine, supporting any fleet size.
* **Real-Time Visual Alerting:** Dashboard panels highlight anomalies dynamically.

---

## 🚧 Current Limitations

* **Backend Alerting:** Dashboard visualizes anomalies but backend does not yet record them as discrete events.
* **Machine Learning:** Ready for RUL prediction or advanced anomaly detection, but no ML models included yet.
* **Data Variety:** Currently focused on vibration; extending to multi-sensor datasets is future work.

---

## 🛡️ Security Notes

* Credentials are managed via `.env` and excluded from version control.
* Sensitive volumes (Mosquitto logs/data) are ignored via `.gitignore`.
* Intended for **local development / PoC**. Production deployments require additional hardening.

---

## 📌 Design Philosophy

* Modular, cloud-agnostic, and extensible architecture.
* Focused on providing a **robust industrial baseline**, not a final product.
* Emphasizes reproducibility, portability, and maintainability across environments.

---

## 🔮 Next Steps / Roadmap

* **Automated backend alerting**
* **Integration of ML models for RUL and predictive maintenance**
* **Support for hybrid sensor data**
* **Advanced visualization and fleet analytics**

---
