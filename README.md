# Open-PMF (Open Predictive Maintenance Framework)

Open-PMF is a containerized, end-to-end IoT pipeline for monitoring industrial machine health.  
It simulates high-frequency vibration data at the edge, ingests it through a secure MQTT broker, processes it in a Spring Boot backend, and stores it in a time-series database for further analysis and anomaly detection.

The project is designed to be realistic, reproducible, and extensible — closer to an industrial PoC than a demo script.

---

## 🏗️ Architecture Overview

- **Edge Simulator**  
  Python-based vibration data generator with configurable anomaly injection.

- **Message Broker**  
  Eclipse Mosquitto (MQTT) with password-file authentication.

- **Backend**  
  Spring Boot 3.3.6 (Java 21), using Spring Integration MQTT for ingestion and persistence.

- **Database**  
  TimescaleDB (PostgreSQL + time-series extension) optimized for high-ingest workloads.

- **Infrastructure**  
  Fully Dockerized stack with internal networking and SELinux-aware volume mapping.

---

## 🛠️ Status & Roadmap

### ✅ Phase 1 — Infrastructure & Security (Completed)
- Secure MQTT broker with hashed authentication
- Automatic time-series schema generation via Hibernate/JPA
- Docker Compose orchestration with isolated services
- SELinux compatibility for Fedora-based systems (`:Z` volume flags)
- Verified end-to-end data flow (Simulator → Broker → Backend → Database)

### ⏳ Phase 2 — Visualization & Intelligence (Planned)
- Grafana dashboards for real-time vibration data
- Rule-based anomaly detection and alerting
- REST API for external consumers
- Machine learning models for Remaining Useful Life (RUL) estimation

---

## 🚀 Getting Started

### 1. Clone the repository
```bash
git clone <your-repo-url>
cd Open-PMF
````

### 2. Configure environment variables

Copy the example environment file and adjust credentials as needed:

```bash
cp .env.example .env
```

### 3. Launch the stack

```bash
sudo docker-compose up -d --build
```

### 4. Verify data ingestion

```bash
sudo docker exec -it timescaledb \
  psql -U admin -d openpmf \
  -c "SELECT * FROM sensor_measurements ORDER BY timestamp DESC LIMIT 10;"
```

If data is returned, the full pipeline is operational.

---

## 🛡️ Security Notes

* Credentials are managed via `.env` and excluded from version control.
* Mosquitto data and log volumes are ignored via `.gitignore`.
* The setup is intended for local development and PoC use; additional hardening is required for production deployments.

---

## 📌 Notes

This project is intentionally modular. Each component (simulator, broker, backend, database) can be replaced or extended independently to support real hardware, different protocols, or advanced analytics.
