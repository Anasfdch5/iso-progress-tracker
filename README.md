# 🛡️ ISO Progress Tracker

A centralized web application designed to track and audit compliance workflows for **ISO 9001:2015** (Quality Management) and **ISO/IEC 27001:2022** (Information Security Management).

---

## 📌 Features

- **Process Portfolio:** Full lifecycle management (CRUD) for ISO processes with real-time progress calculation, search, and multi-criteria sorting[cite: 1].
- **Action Plans:** Task tracking linked to processes with strict operational states (`EN COURS`, `TERMINÉ`) and deadlines[cite: 1].
- **Audit Evidence Vault:** Centralized document register supporting file uploads, metadata indexing, and secure downloads[cite: 1].
- **Analytics Dashboard:** Dynamic compliance scores, pilot rankings, and progress metrics calculated using the **Java Streams API**[cite: 1].
- **Security & Access Control:** Role-based permissions (`ROLE_ADMIN` vs. `ROLE_USER`) powered by **Spring Security 6**, including admin approval for new accounts and forced renewal of temporary passwords[cite: 1].

---

## 🛠️ Tech Stack

- **Backend:** Java 17 LTS, Spring Boot 3, Spring Security 6, Spring Data JPA / Hibernate[cite: 1]
- **Frontend:** Thymeleaf 3, Bootstrap 5, HTML5/CSS3, JavaScript[cite: 1]
- **Database:** H2 Database Engine (Persistent File Mode)[cite: 1]
- **Methodology:** 2TUP, UML (PlantUML)[cite: 1]

---

## 🚀 Getting Started

### Prerequisites
- JDK 17+[cite: 1]
- Apache Maven 3.8+[cite: 1]

### Installation & Run

1. Clone the repository:
   ```bash
   git clone [https://github.com/Anasfdch5/iso-progress-tracker.git](https://github.com/Anasfdch5/iso-progress-tracker.git)
   cd iso-progress-tracker
