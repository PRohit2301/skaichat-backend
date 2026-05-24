# Skai Chat — Backend

> Production-grade REST API for a 1-to-1 real-time messaging application. Built with Java 21, Spring Boot 3, PostgreSQL, and MongoDB Atlas. Designed to handle 500–800 concurrent users on a zero-cost infrastructure.

---

## Live Demo

| Service | URL |
|---------|-----|
| Backend API | `https://your-app.onrender.com` |
| Frontend App | `https://skai-chat.vercel.app` |

---

## Tech Stack

| Layer | Technology |
|-------|-----------|
| Language | Java 21 |
| Framework | Spring Boot 3.2.5 |
| Security | Spring Security + JWT (HS256) |
| ORM | Spring Data JPA + Hibernate |
| ODM | Spring Data MongoDB |
| Database 1 | PostgreSQL — Supabase free tier |
| Database 2 | MongoDB Atlas — M0 free tier |
| Connection Pool | HikariCP (max 10 connections) |
| Password Hashing | BCrypt (10 rounds) |
| Build Tool | Maven |
| Hosting | Render free tier |

---

## Architecture

```
Client (React)
     │
     │ HTTPS + JWT
     ▼
Spring Boot (Render)
     │
     ├──── Spring Security (JwtAuthFilter)
     ├──── 20 REST API Endpoints
     │
     ├──── PostgreSQL (Supabase)
     │     ├── users
     │     ├── conversations
     │     └── friend_requests
     │
     └──── MongoDB Atlas
           └── messages
```

**Why two databases?**
- PostgreSQL handles structured, relational data (users, conversations) where consistency and foreign key constraints matter.
- MongoDB handles high-volume message writes where flexibility and speed are critical.
- This pattern is called **Polyglot Persistence** — using the right database for the right job.

---

## Project Structure

```
src/main/java/com/skaichat/
│
├── SkaichatBackendApplication.java
├── config/
│   ├── SecurityConfig.java       ← JWT filter chain, stateless session
│   ├── JwtConfig.java            ← secret key, 7-day expiry
│   ├── CorsConfig.java           ← Vercel origin whitelist
│   └── MongoConfig.java          ← MongoDB repository scan
│
├── auth/                         ← Signup, Login, JWT generation
├── user/                         ← Profile, Search
├── contact/                      ← Friend requests, Contacts
├── conversation/                 ← Conversation management
├── message/                      ← Send, Fetch, Read receipts
├── settings/                     ← Password, Phone, Theme, Delete
├── exception/                    ← Global error handling
└── util/                         ← JWT + Validation helpers
```

---

## API Reference

### Auth
| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| POST | `/api/auth/signup` | Public | Register → returns JWT |
| POST | `/api/auth/login` | Public | Login → returns JWT |

### Users
| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| GET | `/api/users/me` | JWT | Get current user profile |
| PUT | `/api/users/me` | JWT | Update username + about |
| GET | `/api/users/search?q=` | JWT | Search by name or phone |

### Contacts
| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| POST | `/api/contacts/request` | JWT | Send friend request |
| PUT | `/api/contacts/accept/:id` | JWT | Accept request → creates conversation |
| DELETE | `/api/contacts/decline/:id` | JWT | Decline or cancel request |
| DELETE | `/api/contacts/:id` | JWT | Remove contact |
| GET | `/api/contacts` | JWT | Get accepted contacts |
| GET | `/api/contacts/requests` | JWT | Get pending requests |

### Messages
| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| POST | `/api/messages/send` | JWT | Send message |
| GET | `/api/messages/:convId` | JWT | Fetch all messages |
| PUT | `/api/messages/read/:convId` | JWT | Mark as read (✓✓ blue) |

### Conversations
| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| GET | `/api/conversations` | JWT | Get all conversations |
| POST | `/api/conversations/create` | JWT | Create conversation |

### Settings
| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| PUT | `/api/settings/password` | JWT | Change password |
| PUT | `/api/settings/phone` | JWT | Change phone number |
| PUT | `/api/settings/theme` | JWT | Save theme preference |
| DELETE | `/api/settings/account` | JWT | Hard delete account |

---

## Database Schema

### PostgreSQL — users
| Column | Type | Notes |
|--------|------|-------|
| id | BIGSERIAL PK | Auto increment |
| username | VARCHAR(50) | Display name |
| phone_number | VARCHAR(15) UNIQUE | Login identifier |
| password_hash | VARCHAR(255) | BCrypt 10 rounds |
| about | VARCHAR(150) | Profile bio |
| theme_preference | VARCHAR(20) | light / dark / custom |
| accent_color | VARCHAR(7) | Hex color e.g. #007AFF |
| created_at | TIMESTAMP | Member since |
| is_active | BOOLEAN | false = deleted |

### PostgreSQL — conversations
| Column | Type | Notes |
|--------|------|-------|
| id | BIGSERIAL PK | Auto increment |
| participant_one_id | BIGINT FK | First user |
| participant_two_id | BIGINT FK | Second user |
| last_message | TEXT | Chat list preview |
| last_message_at | TIMESTAMP | Sort order |

### PostgreSQL — friend_requests
| Column | Type | Notes |
|--------|------|-------|
| id | BIGSERIAL PK | Auto increment |
| sender_id | BIGINT FK | Who sent request |
| receiver_id | BIGINT FK | Who receives it |
| status | VARCHAR(20) | pending / accepted |

### MongoDB — messages
| Field | Type | Notes |
|-------|------|-------|
| _id | ObjectId | Auto |
| conversation_id | Long | References PostgreSQL |
| sender_id | Long | References PostgreSQL |
| receiver_id | Long | References PostgreSQL |
| content | String | Max 2000 chars |
| is_delivered | Boolean | ✓✓ grey |
| is_read | Boolean | ✓✓ blue |
| created_at | Date | Timestamp |

---

## Security

| Layer | Implementation |
|-------|---------------|
| JWT | HS256, 7-day expiry, stored in memory (not localStorage) |
| BCrypt | 10 rounds, one-way hash, never returned in API response |
| CORS | Only Vercel URL whitelisted — no wildcard |
| Input Validation | @Valid on all DTOs, parameterized JPA queries |
| Authorization | userId extracted from JWT only — never from request body |
| HTTPS | Automatic on Render — all traffic encrypted |

---

## Getting Started

### Prerequisites
- Java 21
- Maven
- PostgreSQL (or Supabase account)
- MongoDB Atlas account

### Local Setup

**1. Clone the repository**
```bash
git clone https://github.com/yourusername/skaichat-backend.git
cd skaichat-backend
```

**2. Configure environment**

Create `src/main/resources/application.properties`:
```properties
spring.application.name=skaichat-backend
server.port=8080

# PostgreSQL
spring.datasource.url=jdbc:postgresql://YOUR_SUPABASE_HOST:5432/postgres?sslmode=require
spring.datasource.username=YOUR_USERNAME
spring.datasource.password=YOUR_PASSWORD
spring.datasource.driver-class-name=org.postgresql.Driver
spring.jpa.hibernate.ddl-auto=update

# MongoDB
spring.data.mongodb.uri=mongodb+srv://YOUR_USER:YOUR_PASSWORD@YOUR_CLUSTER.mongodb.net/skaichat
spring.data.mongodb.database=skaichat

# JWT
jwt.secret=your-256-bit-secret-key
jwt.expiration=604800000

# CORS
frontend.url=http://localhost:5173
```

**3. Run the application**
```bash
mvn spring-boot:run
```

Server starts at `http://localhost:8080`

---

## Key Design Decisions

**HikariCP Connection Pooling**
Supabase free tier allows only 2 database connections. HikariCP pools and queues all 800 concurrent user requests across those 2 connections efficiently — no crashes, no timeouts.

**Hard Delete Order**
Account deletion follows strict child-before-parent order to avoid FK violations:
1. Delete messages from MongoDB
2. Delete friend_requests from PostgreSQL
3. Delete conversations from PostgreSQL
4. Delete user from PostgreSQL

**Read Receipts**
- ✓ grey = sent to server
- ✓✓ grey = delivered to receiver
- ✓✓ blue = receiver opened and read

---

## Deployment

Deployed on **Render** free tier.

To prevent cold starts (Render sleeps after 15 min inactivity), use [UptimeRobot](https://uptimerobot.com) to ping the server every 10 minutes — free forever.

---

## License

MIT License — feel free to use, modify, and distribute.

---

*Skai Chat Backend · Built with ❤️ using Java + Spring Boot*
