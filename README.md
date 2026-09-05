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
🛠️ Technology StackLayer / ConcernTechnologyVersionRationaleRuntimeJava SE (JDK)17 LTSModern enterprise LTS baseline with Records and Streams API.FrameworkSpring Boot3.2.5Auto-configuration, IoC container, and embedded Tomcat 10.SecuritySpring Security6.2.4Route protection, BCrypt cryptography, and custom interceptors.ORM / Data AccessSpring Data JPA / Hibernate6.4.4Declarative repositories and relational mapping.DatabaseH2 Database Engine2.2.224Zero-dependency persistent file-mode storage with AUTO_SERVER=TRUE.Template EngineThymeleaf3.1.2Natural server-side HTML rendering integrated with Spring Security tags.Frontend UIBootstrap5.3Responsive grid layout and accessible UI components.Build AutomationApache Maven3.8+Standard dependency resolution and application packaging.📂 Project StructurePlaintextcom.iso_progress_tracker/
├── config/             # SecurityFilterChain, WebMvcConfig, ForcePasswordChangeInterceptor, DataInitializer
├── controllers/        # Spring MVC Controllers (Process, Action, Document, Stats, Admin, Auth, Profile)
├── dto/                # Data Transfer Objects (PasswordChangeForm)
├── entities/           # JPA Entities (Process, Action, Document, User, Role)
├── repositories/       # Spring Data JPA Repositories
├── services/           # Business Logic, File Management, and Streams Analytics
└── resources/
    ├── templates/      # Thymeleaf UI Templates (processes/, admin, security, auth)
    └── application.properties
🚀 Getting StartedPrerequisitesJDK 17 (or higher LTS version)Apache Maven 3.8+Installation & RunClone the repository:Bashgit clone [https://github.com/Anasfdch5/iso-progress-tracker.git](https://github.com/Anasfdch5/iso-progress-tracker.git)
cd iso-progress-tracker
Compile and build:Bashmvn clean install
Launch the application:Bashmvn spring-boot:run
Access points:Web Application: http://localhost:8080/loginH2 Database Console: http://localhost:8080/h2-consoleJDBC URL: jdbc:h2:file:./data/isotrackerdb;AUTO_SERVER=TRUEUsername: saPassword: (blank)👤 AuthorAnas Fdaouch — Software Engineering Student at ENSA Agadir
