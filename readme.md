# PURPLE DOG - Valuable Items Trading Platform

A platform for trading valuable items between individual sellers and professional buyers.

---

## Project Structure

```
hackathon/
├── pom.xml                    # Parent Maven POM
├── README.md                  # This file (English)
├── readme.md                  # Database documentation
└── mvp/                       # Main application module
    ├── pom.xml                # Module POM
    ├── src/
    │   ├── main/
    │   │   ├── java/
    │   │   │   └── com/purple_dog/mvp/
    │   │   │       ├── MvpApplication.java
    │   │   │       └── entities/     # 28 JPA entities + 15 enums
    │   │   └── resources/
    │   │       └── application.properties
    │   └── test/
    └── target/
```

---

## Quick Start with docker

# Prérequis

Docker

Docker Compose

Un fichier .env à la racine du projet

# Variables d’environnement

Créer un fichier .env à la racine avec les variables nécessaires:

```
# Database Configuration
POSTGRES_DB=purple_dog_db
POSTGRES_USER=postgres
POSTGRES_PASSWORD=postgres

# Backend Configuration
SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/purple_dog_db

# Frontend Configuration (when ready)
VITE_API_URL=http://localhost:8080/api

# Backend Frontend URL (for CORS)
FRONTEND_URL=http://localhost

# Dev URLs
APP_URL=http://localhost:3000

STRIPE_SECRET_KEY=sk_test_51ScpWHJj3NngK59lcowA058kF4yBU9azJcR1Z2ncmGyGwyOHrkrk0vLezbvUPnLcp2PlUXIaCdKJ4aaXxUlvjaoG00o6HPEzo9
STRIPE_PUBLISHABLE_KEY=pk_test_51ScpWHJj3NngK59l7wCvqgVhuVTpITCXhKdrhOmyqTWCPz3MC5xPlwQHjR8JdKZD6RU1oZxfsI2s78vPx8NIfVVw00HeyjcLOj
STRIPE_WEBHOOK_SECRET=
SHIPPO_API_KEY=shippo_test_f39a8c01f20e356314157c1df37de8948883acb2

GMAIL_APP_PASSWORD=xylnuzfgtzecqwlu
GMAIL_APP_USERNAME=pdog83341@gmail.com
GMAIL_USERNAME=pdog83341@gmail.com

```
# Lancement du projet 

A la racine du projet lancer :

```
docker compose build
docker compose up -d
```
## Quick Start (without docker)

### Open in IntelliJ IDEA

1. **Open IntelliJ IDEA**
2. Click **File → Open**
4. Select the **root folder** and click **Open**
5. IntelliJ will detect Maven project automatically
6. Wait for dependencies to download

### Run the Application

**Option 1: From IntelliJ**
- Navigate to `mvp/src/main/java/com/purple_dog/mvp/MvpApplication.java`
- Right-click → **Run 'MvpApplication'**

**Option 2: From Terminal**
```bash
cd /Users/elwardi/Desktop/hackathon

```

**Option 3: Maven Command**
```bash
cd mvp
./mvnw spring-boot:run
```

---

## 🛠️ Prerequisites

- **Java 21** (JDK 21)
- **Maven 3.8+**
- **PostgreSQL 15+**
- **IntelliJ IDEA**

---

## 🗄️ Database Setup

```bash
# Install PostgreSQL
brew install postgresql@15
brew services start postgresql@15

# Create database
psql postgres
CREATE DATABASE purple_dog_db;
\q
```

Configure in `mvp/src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/purple_dog_db
spring.datasource.username=postgres
spring.datasource.password=postgres
```

---

## Entities Created

 **27 JPA Entities + 15 Enums**

**Users:** Person (abstract), Individual, Professional, Admin  
**Products:** Product, Photo, Document, Category, Address  
**Sales:** Auction, Bid, QuickSale, Offer, Favorite, Alert  
**Orders:** Order, Payment, Delivery, Carrier, Invoice  
**Subscriptions:** Plan, Feature  
**Communication:** Conversation, Message, SupportTicket, TicketMessage, Notification

---

## Maven Commands

```bash
# Compile
mvn clean compile

# Package
mvn clean package

# Run tests
mvn test

# Run application
mvn -pl mvp spring-boot:run

# Update dependencies
mvn clean install -U
```

---

## Access Application

Once started: **http://localhost:8080/api**

---

## Documentation

- **Quick Start (FR)**: See `DEMARRAGE.md`
- **Database Schema**: See `readme.md`
- **Complete SQL**: Full schema with 31 tables in `readme.md`

---

## Troubleshooting

### IntelliJ doesn't recognize project
1. Close IntelliJ
2. Delete `.idea` folder
3. Reopen and import again

### Maven dependencies not downloading
```bash
mvn clean install -U
```

### Port 8080 already in use
Change in `application.properties`:
```properties
server.port=8081
```

---

## Team

**Project:** Purple Dog - Hackathon Project  
**Date:** December 2025  
**Tech Stack:** Java 21, Spring Boot 3.5.8, PostgreSQL, JPA/Hibernate, Lombok

---

## Next Steps

1. Entities created
2. Create Repositories
3. Create Services
4. Create Controllers (REST API)
5. Configure Security (Spring Security + JWT)
6. Add Swagger documentation

---


