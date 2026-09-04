# 🛡️ ISO Progress Tracker — Enterprise Compliance & Audit Management Platform

[![Java](https://img.shields.io/badge/Java-17_LTS-ED8B00?logo=openjdk&logoColor=white)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.2.5-6DB33F?logo=springboot&logoColor=white)](https://spring.io/projects/spring-boot)
[![Spring Security](https://img.shields.io/badge/Spring_Security-6.2.4-6DB33F?logo=springsecurity&logoColor=white)](https://spring.io/projects/spring-security)
[![Thymeleaf](https://img.shields.io/badge/Thymeleaf-3.1.2-005F0F?logo=thymeleaf&logoColor=white)](https://www.thymeleaf.org/)
[![Bootstrap](https://img.shields.io/badge/Bootstrap-5-7952B3?logo=bootstrap&logoColor=white)](https://getbootstrap.com/)
[![Database](https://img.shields.io/badge/H2-Persistent_File_Mode-003B57?logo=sqlite&logoColor=white)](https://www.h2database.com/)
[![Architecture](https://img.shields.io/badge/Architecture-3--Tier_MVC-blue)](#-system-architecture)
[![Methodology](https://img.shields.io/badge/Methodology-2TUP-orange)](#-engineering-methodology)

---

## 📖 Executive Summary

**ISO Progress Tracker** is an enterprise-grade web application engineered to centralize, orchestrate, and audit compliance initiatives for organizations undergoing certification under international standards:
* **ISO 9001:2015** — Quality Management Systems (QMS)
* **ISO/IEC 27001:2022** — Information Security Management Systems (ISMS)

Historically, certification and surveillance audits rely heavily on fragmented spreadsheets, isolated desktop drives, and unmonitored email correspondence. This manual approach generates significant operational risks: document versioning conflicts, absence of ownership traceability, unmonitored corrective actions, and an inability for leadership to evaluate real-time audit readiness.

Developed during an engineering residency at **Certifia**, **ISO Progress Tracker** replaces ad-hoc tracking with a unified, role-governed platform. It couples a structured 3-tier Spring Boot architecture with an in-memory Java Streams computation engine, an integrated audit evidence vault, and automated security enforcement.

---

## 🏛️ System Architecture

The application implements an enterprise **3-Tier Monolithic Model-View-Controller (MVC)** design pattern, enforcing strict separation of concerns across presentation, business logic, data access, and storage:

```text
┌────────────────────────────────────────────────────────────────────────┐
│               PRESENTATION LAYER (Client Browser & UI)                 │
│  - Thymeleaf 3 Server-Side Template Engine                             │
│  - Responsive Bootstrap 5 UI Framework                                │
│  - Dynamic Contextual Views (Processes, Actions, Documents, Analytics) │
└───────────────────────────────────┬────────────────────────────────────┘
                                    │ HTTP(S) / Form Submissions
                                    ▼
┌────────────────────────────────────────────────────────────────────────┐
│             WEB CONTROLLERS & APPLICATION SECURITY LAYER               │
│  - Spring MVC Controllers (@Controller)                                │
│  - Spring Security 6 Filter Chain (BCrypt, CSRF Protection, Sessions)  │
│  - Custom HandlerInterceptor (ForcePasswordChangeInterceptor)          │
│  - Role-Based Access Control (ROLE_ADMIN vs. ROLE_USER)                │
└───────────────────────────────────┬────────────────────────────────────┘
                                    │ Service Invocations
                                    ▼
┌────────────────────────────────────────────────────────────────────────┐
│                 BUSINESS LOGIC & COMPUTATION LAYER                     │
│  - Spring @Service Domain Implementations                              │
│  - Java Streams API Engine (KPI Aggregations, Ranks, Progress Metrics) │
│  - Multi-Criteria Filtering & Bidirectional Sorting Algorithms         │
│  - File System Storage Management (Physical Audit Evidence Handling)   │
└───────────────────────────────────┬────────────────────────────────────┘
                                    │ Managed ORM Operations
                                    ▼
┌────────────────────────────────────────────────────────────────────────┐
│             PERSISTENCE & DATA STORAGE LAYER                           │
│  - Spring Data JPA & Hibernate ORM 6                                   │
│  - Relational Mapping (@Entity, CascadeType.ALL, Orphan Removal)       │
│  - Embedded H2 Database Engine (Persistent File Mode + AUTO_SERVER)    │
│  - Physical File System Vault (uploads/iso_docs/)                      │
└────────────────────────────────────────────────────────────────────────┘
