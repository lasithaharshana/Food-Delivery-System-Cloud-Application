```
# Food Delivery System with Microservices using Spring Boot

## Overview

This project implements a Food Delivery System composed of three microservices:

- **Customer Service:** Manages customer profiles and authentication
- **Inventory Service:** Manages food items and stock availability
- **Order Service:** Handles order placement, tracking, and status updates

Core technologies used:  
- Spring Boot for microservices development  
- MySQL as separate databases for each service  
- Apache Kafka for asynchronous event communication  
- Debezium for Change Data Capture (CDC) from MySQL to Kafka  
- Saga pattern for orchestrating distributed transactions  
- Docker for containerization  
- Kubernetes for container orchestration and deployment  

---

## Technology Stack

- **Backend:** Spring Boot 3.x, Spring Cloud Stream (Kafka integration)  
- **Database:** MySQL (one per microservice)  
- **Messaging:** Apache Kafka  
- **CDC:** Debezium (MySQL connector)  
- **Transaction Management:** Saga pattern (event choreography)  
- **Containerization:** Docker  
- **Orchestration:** Kubernetes  
- **Build Tool:** Maven or Gradle  

---

## Architecture Overview

```
+---------------------+          +---------------------+          +---------------------+
|  Customer Service    |    |  Order Service      |    | Inventory Service    |
|  (MySQL + Kafka)     | EVENTS   | (MySQL + Kafka)     | EVENTS   | (MySQL + Kafka)      |
+---------------------+          +---------------------+          +---------------------+
           |                              |                               |
           +------------------------------+-------------------------------+
                                         |
                                   Kafka Cluster
                                         |
                                  Debezium Connect 
                                  (CDC from MySQL)
```

- Each microservice owns its database  
- Services communicate asynchronously using Kafka topics  
- Debezium captures MySQL binlog changes and streams to Kafka  
- Saga pattern ensures consistency across distributed operations  

---

## Step-by-Step Implementation Guide

### 1. Setup Project Repositories and Structure

Create three Spring Boot projects:

```
/customer-service
/inventory-service
/order-service
```

Each project contains:  
- Java source code and resources (`src/main/java`, `src/main/resources`)  
- `application.yml` or `application.properties` for configuration  
- `Dockerfile` for containerization  

---

### 2. Setup MySQL Databases

Create three separate MySQL databases:  
- `customer_db`  
- `inventory_db`  
- `order_db`  

Example tables:

**Customer Service:**  
| Table     | Columns                  |  
|-----------|--------------------------|  
| customers | id, name, email, phone   |  

**Inventory Service:**  
| Table | Columns                      |  
|-------|------------------------------|  
| foods | id, name, description, quantity |  

**Order Service:**  
| Table       | Columns                          |  
|-------------|---------------------------------|  
| orders      | id, customer_id, order_status   |  
| order_items | id, order_id, food_id, quantity |  

---

### 3. Implement Customer Service

- REST APIs for customer registration, login, and retrieval  
- Store data in `customer_db`  
- Publish `CustomerCreated` events to Kafka after successful registration  

---

### 4. Implement Inventory Service

- APIs to add, update, and query food items with stock quantities  
- Consume order-related events to adjust stock levels  
- Publish `InventoryUpdated` events when stock changes  

---

### 5. Implement Order Service

- APIs to place orders and track order status  
- Publish `OrderCreated` events when orders are placed  
- Listen to Customer and Inventory events to verify customer and reserve stock  
- Manage order lifecycle with Saga pattern events  

---

### 6. Setup Apache Kafka

- Create Kafka topics:  
  - `customer-events`  
  - `order-events`  
  - `inventory-events`  

- Configure each microservice with Spring Cloud Stream Kafka bindings for producing and consuming events  

---

### 7. Implement Saga Pattern (Event Choreography)

Example flow:  
1. Order Service publishes `OrderCreated`  
2. Inventory Service listens and tries to reserve stock  
3. Inventory Service publishes `InventoryReserved` (success) or `InventoryFailed` (failure)  
4. Order Service listens, confirms or cancels order accordingly  
5. Customer Service notified about final order status  

---

### 8. Setup Debezium for Change Data Capture

- Deploy Debezium MySQL connector (Kafka Connect)  
- Configure connectors to monitor `customer_db`, `inventory_db`, `order_db`  
- Debezium streams database changes as Kafka topics to keep data in sync across services  

---

### 9. Dockerize Microservices

Create a `Dockerfile` in each service:

```
FROM openjdk:17-jdk-slim
VOLUME /tmp
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
```

Build Docker images and tag appropriately.

---

### 10. Kubernetes Deployment

Prepare Kubernetes manifests for:

- MySQL StatefulSets and Services for each microservice  
- Kafka cluster deployment (or use managed service)  
- Debezium connector deployment  
- Deployments and Services for Customer, Inventory, and Order microservices  

Use ConfigMaps and Secrets for environment configuration and credentials.

---

### 11. Run & Test

- Deploy MySQL, Kafka, Debezium, and microservices onto the Kubernetes cluster  
- Use tools like Postman to test API endpoints:  
  - Register customers  
  - Add inventory items  
  - Place and track orders  
- Monitor Kafka topics and logs for event flow and Saga coordination  

#### Quickstart (Docker Compose)

- Build and start: `docker compose up --build -d`
- Services:
  - customerservice: `http://localhost:9092`
  - inventoryservice: `http://localhost:9093`
  - orderservice: `http://localhost:9091`

Sample flow:

1. Create customer:
   POST `http://localhost:9092/api/customers` with body:
   `{ "name":"Alice", "email":"alice@example.com", "phone":"123" }`
2. Create item:
   POST `http://localhost:9093/api/inventory` with body:
   `{ "name":"Pizza", "description":"Cheese", "quantity": 10 }`
3. Create order:
   POST `http://localhost:9091/api/orders` with body like:
   `{ "customerId": 1, "items": [ { "foodId": 1, "quantity": 1 } ] }`

---

## Helpful Tips

- Use Spring Cloud Stream for seamless Kafka integration  
- Use Flyway or Liquibase for database migrations  
- Implement robust logging and monitoring (e.g., ELK stack)  
- For local dev testing, use Docker Compose with Kafka and MySQL containers  
- Use testcontainers for integration tests with Kafka and MySQL  
- Optionally add an API Gateway (Spring Cloud Gateway)  

---

## Summary

Your Food Delivery System microservices architecture provides:

- Clear separation of concerns with service-specific databases  
- Event-driven asynchronous communication using Kafka  
- Reliable distributed transaction management with Saga pattern  
- Real-time data sync with Debezium CDC  
- Containerized services deployed and managed via Kubernetes  

---

## Contribution & Support

Feel free to fork this repo, raise issues, submit pull requests, or reach out for questions.

---

## License

[Specify your license here, e.g., MIT License]

---

## Contact

Project Maintainer: [Your Name / Contact Info]

```

***