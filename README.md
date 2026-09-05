# 🛡️ ISO Progress Tracker

[![Java](https://img.shields.io/badge/Java-17_LTS-ED8B00?logo=openjdk&logoColor=white)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.2.5-6DB33F?logo=springboot&logoColor=white)](https://spring.io/projects/spring-boot)
[![Spring Security](https://img.shields.io/badge/Spring_Security-6.2.4-6DB33F?logo=springsecurity&logoColor=white)](https://spring.io/projects/spring-security)
[![Thymeleaf](https://img.shields.io/badge/Thymeleaf-3.1.2-005F0F?logo=thymeleaf&logoColor=white)](https://www.thymeleaf.org/)
[![Bootstrap](https://img.shields.io/badge/Bootstrap-5.3-7952B3?logo=bootstrap&logoColor=white)](https://getbootstrap.com/)
[![Database](https://img.shields.io/badge/H2-Persistent_File_Mode-003B57?logo=sqlite&logoColor=white)](https://www.h2database.com/)

A centralized enterprise web application designed to track, coordinate, and audit compliance workflows for international standards:
* **ISO 9001:2015** (Quality Management Systems)
* **ISO/IEC 27001:2022** (Information Security Management Systems)

Engineered using the **2TUP (Two Tracks Unified Process)** methodology with a 3-tier monolithic Model-View-Controller (MVC) architecture.

---

## 📖 Project Overview

Continuous audit readiness is critical for modern organizations, yet compliance management often suffers from fragmented spreadsheets, scattered email threads, and untracked file attachments.

**ISO Progress Tracker** addresses these operational risks by providing a unified, secure workspace that centralizes process ownership, corrective action tracking, and audit artifact verification. Developed for **Certifia**, the platform streamlines audit preparation, automates metric computation, and guarantees end-to-end accountability across organizational departments.

---

## 🌟 Key Functional Modules

* **Normative Process Portfolio:** Complete lifecycle management (CRUD) for ISO 9001 and ISO 27001 processes. Features multi-criteria keyword search, standard-specific filtering, and dynamic multi-column sorting (by Name, Responsible Pilot, Target Date, and Progress).
* **Corrective Action Plans (CAPA):** Contextual mitigation tracking linked directly to parent processes with cascading integrity. Tracks operational deadlines and strict execution statuses (`EN COURS`, `TERMINÉ`).
* **Centralized Audit Evidence Vault:** Secure document repository managing file uploads, duplicate title prevention, metadata indexing (file size, MIME type, upload timestamp), and direct byte-stream downloading.
* **In-Memory Analytics Dashboard (`/stats`):** Real-time executive metrics computed via the **Java Streams API**, including overall compliance percentages, pilot workload rankings, standard distributions, and process health indicators without heavy SQL aggregate overhead.
* **Role-Based Access Control (RBAC):** Tiered operational permissions (`ROLE_ADMIN` vs. `ROLE_USER`) secured with **Spring Security 6**, BCrypt password hashing, and active CSRF mitigation.
* **Security Interceptors & Workflow Governance:** 
  * Custom `ForcePasswordChangeInterceptor` blocking platform access until temporary or reset credentials are changed via `force-change-password.html`.
  * Self-registration gate holding unapproved accounts in a pending state (`approved = false`) until validated by an administrator.
  * User management portal for administrators to review accounts, process password recovery tickets, and edit permissions.

---

## 🏛️ System Architecture

The application adopts an enterprise 3-tier monolithic architecture ensuring strict separation of presentation, security, business computation, and data persistence:

```text
┌────────────────────────────────────────────────────────────────────────┐
│               PRESENTATION LAYER (Client Browser & UI)                 │
│  - Thymeleaf 3 Template Engine + Spring Security Dialect               │
│  - Responsive Bootstrap 5 Layouts (Dark/Light Contextual Elements)     │
│  - Contextual Views (processes/, admin-users, profile, auth portals)   │
└───────────────────────────────────┬────────────────────────────────────┘
                                    │ HTTP(S) Form / Multi-part Requests
                                    ▼
┌────────────────────────────────────────────────────────────────────────┐
│             WEB CONTROLLERS & APPLICATION SECURITY LAYER               │
│  - Spring MVC Web Controllers (@Controller)                            │
│  - Spring Security 6 Filter Chain (BCrypt, CSRF prevention, Sessions)  │
│  - Custom HandlerInterceptor (ForcePasswordChangeInterceptor)          │
│  - Role-Based Route Authorization (ROLE_ADMIN vs. ROLE_USER)           │
└───────────────────────────────────┬────────────────────────────────────┘
                                    │ Service Invocations
                                    ▼
┌────────────────────────────────────────────────────────────────────────┐
│                 BUSINESS LOGIC & COMPUTATION LAYER                     │
│  - Spring @Service Domain Implementations                              │
│  - Java Streams API Pipeline (In-Memory KPIs, Rankings, Aggregations)  │
│  - Multi-Criteria Filtering & Bidirectional Sorting Algorithms         │
│  - Local Filesystem Storage Synchronization (Audit Evidence Files)     │
└───────────────────────────────────┬────────────────────────────────────┘
                                    │ Spring Data Repositories
                                    ▼
┌────────────────────────────────────────────────────────────────────────┐
│             PERSISTENCE & DATA STORAGE LAYER                           │
│  - Spring Data JPA & Hibernate ORM 6                                   │
│  - Relational Mapping (@Entity, CascadeType.ALL, OrphanRemoval)        │
│  - Embedded H2 Database (Persistent File Mode + AUTO_SERVER=TRUE)      │
│  - File System Evidence Vault (uploads/iso_docs/)                      │
└────────────────────────────────────────────────────────────────────────┘
