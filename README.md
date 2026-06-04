# 🚗 Uber Clone - Microservices Backend

Sistema de corridas estilo Uber desenvolvido com Spring Boot, Kafka, Redis e RabbitMQ.

## 🏗️ Arquitetura

- **User Service** (Port 8081) - Gerenciamento de usuários e autenticação
- **Ride Service** (Port 8082) - Gerenciamento do ciclo de vida das corridas
- **Matching Service** (Port 8083) - Algoritmo de pareamento passageiro-motorista
- **Location Service** (Port 8084) - Rastreamento de localização em tempo real

## 🛠️ Tecnologias

- Java 17
- Spring Boot 4.6.0
- PostgreSQL 15
- Redis 7
- RabbitMQ 3
- Apache Kafka 7.6 (KRaft mode)
- Docker & Docker Compose

## 🚀 Como Executar

### 1. Subir infraestrutura (Docker)

```bash
docker compose up -d