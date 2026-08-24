# 🏢 Recruitment Management Platform

**A comprehensive recruitment platform built with Spring Boot** that handles job creation, candidate sourcing, interview workflow, and hiring decisions.

---

## 📋 Table of Contents
1. [Project Overview](#-project-overview)
2. [Features](#-features)
3. [Technology Stack](#-technology-stack)
4. [Architecture](#-architecture)
5. [Setup & Installation](#-setup--installation)
6. [Database Schema](#-database-schema)
7. [API Documentation (Swagger)](#-api-documentation-swagger)
8. [Testing](#-testing)
9. [Contributors](#-contributors)

---

## 🎯 Project Overview

The **Recruitment Management Platform** is a RESTful web application that provides a complete HR solution for managing the entire hiring lifecycle. It supports multiple user roles (Admin, HR, Interviewer, Candidate) with JWT-based authentication, LDAP integration, CV parsing, and real-time application tracking.

### Key Capabilities:
- **Job Management**: Create, update, delete, and browse job listings.
- **Candidate Management**: Upload CVs (single or bulk), parse candidate data, search and tag candidates.
- **Application Tracking**: Move candidates through hiring stages (Applied → Interview → Offer/Disqualified) with full audit logs.
- **Interview Workflow**: Assign recruiters and interviewers, submit and review feedback with scoring.

---

## ✨ Features

### 🔐 User & Authentication Service
- **Register/Login** for `ADMIN`, `HR`, `INTERVIEWER`, and `CANDIDATE` roles.
- **Role-Based Access Control (RBAC)** with Spring Security.
- **JWT Authentication** (token generation & validation).
- **LDAP Integration** for enterprise authentication (embedded LDAP server).

### 👤 Candidate Management Service
- **Single & Bulk CV Upload** (supports PDF, DOC, DOCX).
- **CV Parsing** with automatic extraction of:
  - Full Name, Email, Phone, Location
  - Skills & Technologies (using keyword matching)
- **Tagging & Searching** candidates by name, email, or tags.

### 📋 Application Tracking Service
- **Move candidates** across stages: `APPLIED → INTERVIEW → OFFER/DISQUALIFIED`.
- **Audit Logs**: Every status change is recorded with timestamp and user.
- **Assign Recruiters & Interviewers** to applications.
- **Interview Feedback**: Interviewers can submit scores (0–10) and comments.

---

## 🛠️ Technology Stack

| Technology | Version |
|------------|---------|
| **Java** | 21 (LTS) |
| **Spring Boot** | 3.4.3 |
| **Spring Security** | 6.x |
| **Spring Data JPA** | 3.x |
| **MySQL** | 8.x |
| **JWT (JJWT)** | 0.12.6 |
| **LDAP (Embedded)** | UnboundID SDK |
| **CV Parsing** | Apache PDFBox, Apache POI |
| **Testing** | JUnit 5, Mockito |
| **API Documentation** | Swagger/OpenAPI 3 |
| **Frontend** | Thymeleaf, Bootstrap 5 |

---

## 🏗️ Architecture

The application follows a **layered architecture**:


┌─────────────────────────────────────────────────────┐
│ Web/API Layer │
│ (Controllers: Auth, Candidate, Job, Application) │
├─────────────────────────────────────────────────────┤
│ Service Layer │
│ (Business Logic: User, Candidate, Application) │
├─────────────────────────────────────────────────────┤
│ Repository Layer │
│ (JPA Repositories for Database Access) │
├─────────────────────────────────────────────────────┤
│ Database (MySQL) │
└─────────────────────────────────────────────────────┘



### Security Flow: User → Login → Authentication Manager (LDAP + MySQL) → JWT Token → Subsequent Requests (Bearer Token)



------------

## ⚙️ Setup & Installation

### Prerequisites
- **Java 21** (or newer)
- **MySQL 8.x** (running locally)
- **Maven** (or use the included Maven wrapper)

### Step 1: Clone the Repository  ```bash
git clone https://github.com/MOhamedMahrous2004/recruitment-platform.git 
cd recruitment-platform

-------------
Step 2: Configure Database
Create a MySQL database: CREATE DATABASE recruitment_db;
Update src/main/resources/application.yaml with your database credentials:
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/recruitment_db
    username: root
    password: your_password

------------
Step 3: Build and Run
# Using Maven wrapper (recommended)
./mvnw clean install
./mvnw spring-boot:run

# Or using system Maven
mvn clean install
mvn spring-boot:run


-------------
Step 4: Access the Application
Web UI: http://localhost:8081

Swagger API Docs: http://localhost:8081/swagger-ui/index.html

Default Login Credentials
Role	        Email/Username	            Password	               Notes
ADMIN	        admin@recruitment.com	      Admin@123	               Full system control
HR (LDAP)	    ldapuser	                  Ldap@123	               Authenticated via Embedded LDAP
HR (MySQL)	  hr@recruitment.com	        12345                    You can create additional HR users
CANDIDATE	    Mohamed2004@gmail.com	       12345	                 Public registration
INTERVIEWER   Roaa@recruitment.com  	     12345                   Created by ADMIN

------------
📚 API Documentation (Swagger)
Once the application is running, access Swagger at:
http://localhost:8081/swagger-ui/index.html

Major Endpoints:


Method	      Endpoint	                                      Description	                Roles
POST	        /api/auth/login	                                Login & JWT generation	    Public
GET	          /api/admin/test	                                Admin test endpoint	        ADMIN
GET	          /api/hr/candidates	                            Search candidates	          HR, ADMIN
POST	        /api/candidate/profile/cv	                      Upload single CV	          CANDIDATE
POST	        /api/hr/candidates/bulk-cv	                    Bulk CV upload	            HR, ADMIN
GET	          /api/candidate/applications	                    View my applications	      CANDIDATE
POST	        /api/candidate/applications/apply               Apply to job	              CANDIDATE
PUT	          /api/hr/applications/{id}/status	              Update application status	  HR, ADMIN
GET	          /api/hr/applications/{id}/history	              View audit log	            HR, ADMIN
POST	        /api/interviewer/applications/{id}/feedback	    Submit interview feedback	  INTERVIEWER

See Swagger for detailed request/response schemas.

-----------------
✅ Testing
The project includes unit and integration tests using JUnit 5 and Mockito.

# Run all tests
./mvnw test

# Run specific test class
./mvnw test -Dtest=ApplicationServiceTest

Test Coverage:

✅ User registration and authentication.

✅ JWT token generation and validation.

✅ Candidate CV upload and parsing.

✅ Application lifecycle (apply → status change → history).

✅ Interview feedback submission.

-------------------
👥 Contributors
Mohamed Mahrous – Developer & Maintainer
(BE Internship Project)

© 2026 Recruitment Platform – Built with ❤️ using Spring Boot
