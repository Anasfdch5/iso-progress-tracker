# 🛡️ ISO Progress Tracker

[![Java](https://img.shields.io/badge/Java-17_LTS-ED8B00?logo=openjdk&logoColor=white)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.2.5-6DB33F?logo=springboot&logoColor=white)](https://spring.io/projects/spring-boot)
[![Spring Security](https://img.shields.io/badge/Spring_Security-6-6DB33F?logo=springsecurity&logoColor=white)](https://spring.io/projects/spring-security)
[![Thymeleaf](https://img.shields.io/badge/Thymeleaf-3-005F0F?logo=thymeleaf&logoColor=white)](https://www.thymeleaf.org/)
[![Bootstrap](https://img.shields.io/badge/Bootstrap-5-7952B3?logo=bootstrap&logoColor=white)](https://getbootstrap.com/)
[![Database](https://img.shields.io/badge/H2-Persistent_Mode-003B57?logo=sqlite&logoColor=white)](https://www.h2database.com/)

A centralized web application designed to streamline, automate, and audit compliance monitoring for international standards:
- **ISO 9001:2015** (Quality Management Systems)
- **ISO/IEC 27001:2022** (Information Security Management Systems)

Built with a 3-tier monolithic MVC architecture following the **2TUP (Two Tracks Unified Process)** methodology.

---

## ✨ Key Features

- **Process Portfolio & Mapping:** Complete lifecycle management (CRUD) for ISO 9001 and ISO 27001 processes with dynamic progress calculation, search, and multi-criteria sorting.
- **Corrective Action Plans:** Dedicated task tracking linked directly to parent processes with operational states (`EN COURS`, `TERMINÉ`) and deadlines.
- **Audit Evidence Register:** Centralized file repository supporting uploads, metadata indexing, duplicate detection, and secure downloads.
- **In-Memory Analytics Dashboard:** Real-time compliance scoring, progress ranking, and workload distribution per stakeholder computed dynamically via the **Java Streams API**.
- **Role-Based Access Control (RBAC):** Tiered permissions (`ROLE_ADMIN` vs `ROLE_USER`) powered by **Spring Security 6**.
- **Security Interceptor:** Custom `ForcePasswordChangeInterceptor` requiring users with temporary passwords to reset credentials before accessing system modules.
- **User Governance:** Administrative directory to approve/reject pending registrations and manually process account recovery tickets.

---

## 🏛️ System Architecture

```text
├── Presentation Layer: Thymeleaf 3, Bootstrap 5, HTML5/CSS3, JavaScript
├── Business & Security Layer: Spring MVC, Spring Security 6, Custom Interceptors, Java Streams API
└── Persistence & Data Layer: Spring Data JPA, Hibernate ORM, Embedded H2 Database (File Mode)
