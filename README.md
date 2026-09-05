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

## 🌟 Key Features

* **Normative Process Portfolio:** Full lifecycle management (CRUD) for ISO 9001 and ISO 27001 processes with dynamic progress calculation, search, and multi-criteria sorting.
* **Corrective Action Plans (CAPA):** Task tracking linked directly to parent processes with operational statuses (`EN COURS`, `TERMINÉ`) and deadlines.
* **Audit Evidence Vault:** Centralized document register supporting file uploads, metadata indexing, duplicate detection, and secure byte-stream downloads.
* **In-Memory Analytics Dashboard (`/stats`):** Real-time compliance scoring, progress ranking, and pilot workload distribution computed dynamically via the **Java Streams API**.
* **Role-Based Access Control (RBAC):** Tiered permissions (`ROLE_ADMIN` vs. `ROLE_USER`) powered by **Spring Security 6** with BCrypt password hashing and CSRF protection.
* **Administrative Governance:** Pending approval queue for self-registered accounts, manual password overrides, and a custom `ForcePasswordChangeInterceptor` requiring users with temporary credentials to update their password before browsing.

---

## 🏛️ System Architecture

```text
├── Presentation Layer: Thymeleaf 3, Bootstrap 5, HTML5/CSS3, JavaScript
├── Security & Interceptor Layer: Spring Security 6, ForcePasswordChangeInterceptor
├── Business & Analytics Layer: Spring MVC Services, Java Streams API
└── Persistence & Storage Layer: Spring Data JPA, Hibernate ORM, Embedded H2 (Persistent File Mode)
