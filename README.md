# SwineCore — Multi-Farm Pig Management System

A full-stack Spring Boot application for managing multi-farm pig operations, staff workflows, inventory, finance, and an integrated e-commerce marketplace.

---

## 🚀 Quick Start

### Prerequisites
- **JDK 21**
- **Maven 3.9+** (or use the included `mvnw` wrapper)
- **MySQL 8.x** running locally (or update `application.properties` for your DB)

### Steps

1. **Create the database**
   ```sql
   CREATE DATABASE swinecore CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
   ```

2. **Configure database credentials**  
   Edit `src/main/resources/application.properties`:
   ```properties
   spring.datasource.username=root
   spring.datasource.password=your_password
   ```

3. **Build and run**
   ```bash
   ./mvnw spring-boot:run
   ```
   Or on Windows:
   ```cmd
   mvnw.cmd spring-boot:run
   ```

4. **Open in browser**  
   [http://localhost:8080](http://localhost:8080)

5. **Default Admin Account**  
   | Field    | Value                  |
   |----------|------------------------|
   | Email    | `admin@swinecore.com`  |
   | Password | `Admin@1234`           |
   > ⚠️ Change the password immediately after first login!

---

## 🏗️ Architecture

| Layer       | Technology |
|-------------|------------|
| Framework   | Spring Boot 3.3, Spring MVC |
| Security    | Spring Security 6 (RBAC, CSRF) |
| Persistence | Spring Data JPA + Hibernate |
| Database    | MySQL 8 |
| Templates   | Thymeleaf 3 + Bootstrap 5 |
| QR Codes    | ZXing (Google) |
| Scheduling  | Spring `@Scheduled` |
| Email       | Spring Mail (JavaMail) |

---

## 👥 User Roles

| Role       | Capabilities |
|------------|-------------|
| **Admin**  | Full CRUD across all farms, users, genetics. Safe-delete with name confirmation. Analytics |
| **Manager**| Manage own farm: buildings, rules, pigs, inventory, reports, analytics |
| **HR**     | Create/manage Supervisor & Staff accounts. Farm advertisements |
| **Supervisor** | Review/approve staff tasks, log feed shipments, list pigs for sale, clock in/out |
| **Staff**  | Clock in, complete daily tasks, submit birth records (round-robin rotation) |
| **Customer** | Register/login, browse marketplace, buy pigs/semen, QR checkout |

---

## 🐷 Key Features

### Pig Management
- **Structured Serial Code**: `[FarmCode][BuildingCode][GeneticCode][MotherSuffix2][RecordDate][SeqNum]`
- **Auto-classification**: Pigs auto-upgrade status at 150 days (Breeding Sow/Boar)
- **Litter Recording**: Staff records births → codes auto-generated → Supervisor confirms

### Task System
- Standard "Maintain & Care" tasks for all attended staff daily
- **Rule-triggered specialized tasks**: Vaccines, medications, feeds based on pig age
- **Fair Round-Robin rotation** for non-standard tasks among attended staff

### Birth Form Rotation
- Advanced sequential access: only ONE staff member holds the form at a time
- Absent staff → form passes to next attended → reverts when original returns
- Form stays with staff until submission is recorded

### Inventory & Finance
- Daily feed deduction via scheduler (11:30 PM)
- Low-stock alerts (2-day supply threshold)
- Feed shipment verification: auto-payment on match, manager confirmation on mismatch
- Finance module auto-records pig/semen sales as income

### E-Commerce Marketplace
- Public homepage with farm ads
- Customer registration/login
- Pig & semen marketplace with cart
- QR code checkout with optional PNG download
- Auto-income logging on payment

---

## 📁 Project Structure

```
src/main/
├── java/com/swinecore/
│   ├── SwineCoreApplication.java
│   ├── config/          — Security, App configuration
│   ├── entity/          — JPA entities (User, Farm, Pig, etc.)
│   │   └── enums/       — Role, PigStatus, OrderStatus, ...
│   ├── repository/      — Spring Data JPA repositories
│   ├── service/         — Business logic layer
│   ├── controller/      — MVC controllers by role
│   ├── scheduler/       — Pig auto-classification, feed deduction
│   └── util/            — PigCodeGenerator, QrCodeUtil, FileUploadUtil
└── resources/
    ├── application.properties
    ├── db/init.sql      — Optional manual schema notes
    ├── templates/       — Thymeleaf HTML templates
    │   ├── layout/      — Shared sidebar layout
    │   ├── auth/        — Login, forgot/reset password
    │   ├── admin/       — Admin pages
    │   ├── manager/     — Manager pages
    │   ├── hr/          — HR pages
    │   ├── supervisor/  — Supervisor pages
    │   ├── staff/       — Staff pages
    │   ├── customer/    — Customer marketplace pages
    │   ├── shared/      — Profile page
    │   └── error/       — 403, 404 pages
    └── static/
        ├── css/main.css
        ├── js/main.js
        └── images/      — users/, pigs/, customers/, farms/
```

---

## ⚙️ Configuration Reference

| Property | Default | Notes |
|----------|---------|-------|
| `server.port` | 8080 | HTTP port |
| `spring.datasource.url` | `localhost:3306/swinecore` | MySQL connection |
| `app.upload.dir` | `~/swinecore-uploads` | File storage |
| `app.base-url` | `http://localhost:8080` | Used in QR/email |
| `spring.mail.*` | Gmail SMTP | Configure for password reset |

---

## 📧 Password Reset Email

Configure Gmail (or any SMTP) in `application.properties`:
```properties
spring.mail.username=your-email@gmail.com
spring.mail.password=your-app-password   # Gmail App Password (not your login password)
```
Obtain a Gmail App Password at: https://myaccount.google.com/apppasswords

---

## 📜 License
MIT — Internal farm management use.
