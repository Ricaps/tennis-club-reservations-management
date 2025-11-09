# 🎾 Tennis Club Reservation System

---

## 🌟 Features

- 🏟️ Manage multiple **tennis courts** with various **surface types** and individual **minute-based pricing**.
- 🧱 Maintain **surface types** dynamically through a user-managed **dictionary (lookup table)**.
- 📅 Create **court reservations** with flexible time intervals and play modes (**singles** or **doubles**, doubles
  priced ×1.5).
- ☎️ Automatically manage **customers** by phone number — new users are created when a number doesn’t yet exist.
- 🔐 Secure the system with **JWT-based authentication**, including:
  - Registration at [`/api/v1/auth/register`](http://localhost:8080/api/v1/auth/register)
  - Login at [`/api/v1/auth/login`](http://localhost:8080/api/v1/auth/login)
- 📘 Explore and test all REST endpoints through an interactive **Swagger UI** available at  
  [`http://localhost:8080/api/swagger-ui/index.html`](http://localhost:8080/api/swagger-ui/index.html).
- 🗑️ Implement **soft delete** for all entities, with optional **data initialization** (2 surfaces and 4 courts) enabled
  via external configuration.

---

## 🧱 Tech Stack

| Layer                 | Technology                     |
|-----------------------|--------------------------------|
| **Backend Framework** | Spring Boot (v3.x)             |
| **Database**          | In-Memory H2                   |
| **Security**          | Spring Security + JWT          |
| **ORM**               | Hibernate / JPA                |
| **Build Tool**        | Maven                          |
| **Testing**           | JUnit 5, Mockito               |
| **API Docs**          | SpringDoc OpenAPI / Swagger UI |

---

## ⚙️ Installation & Setup

### Prerequisites

- Java 21+
- Maven 3.9+

```bash
git clone https://github.com/Ricaps/tennis-club-reservations-management.git
cd tennis-club-reservations-management
./mvnw spring-boot:run -Dspring-boot.run.profiles=prod
```

### ⚙️ Production Configuration

When running the application with the `prod` profile, configuration properties can be managed inside:

📁 **`config/application-prod.properties`**

This external configuration file allows you to override sensitive or environment-specific settings.

### 👑 Database Seed

When **data initialization** is enabled (`application.database-seed=true`), the system automatically creates:

- 🏓 **2 surface types**
- 🏟️ **4 tennis courts**
- 👤 **1 default admin user**

**Default Admin Credentials:**

- 📱 **Phone:** `+420111111112`
- 🔑 **Password:** `12345`

This user can be used to log in via the authentication endpoint (Bearer token can be found in response header):  
[`POST /api/v1/auth/login`](http://localhost:8080/api/v1/auth/login)
---

## 🗂️ Diagrams

To understand the system design and structure:

📘 **[Entity Relationship Diagram (ERD)](./docs/erd_diagram.png)**  
🧩 **[UML Class Diagram (Generated)](./docs/uml_class_diagram_generated.png)**

These diagrams illustrate the relationships between entities and the system’s overall architecture.

---

## 🏗️ Architecture Overview

The system follows a clean **three-tier architecture**