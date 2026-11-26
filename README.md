
# 🧠 BrainAlpha - Mental Training

<img src="logo.PNG" width="400">

## 🟦 Project Overview

**BrainAlpha** is an open-source application designed to enhance **memory, concentration, logic, and mental agility** through interactive games.  
The app is completely **free**, with **no ads or in-app purchases**, and is inclusive for people with **cognitive difficulties**.

BrainAlpha offers its users features such as:
-  **Challenges divided into categories and levels.**
-  **Measure your progress with statistics.**
-  **Customize the app (favorite categories, interface, difficulty levels, etc.).**
-  **Cloud-based user progress tracking.**
-  **Achievements system to encourage continuous improvement.**

All project documentation is available in the ▶ [`docs/`](docs/) folder.

---

## 🟦 Project Status

- **Version:** `v0.1`
- **Last update:** `March 2025`
- **Stage:** `MVP – first iteration completed`
- **Context:** Final course project (DAM), developed by a team of 3.
- **Availability:** The Android app is no longer published on Google Play. The source code and builds are available in this repository.

---


## 🟦 Project Objectives

- Develop a **cross-platform** application with cognitive training games.
- Enable users to **track their progress** with detailed statistics.
- Provide **personalized training** based on user performance.
- Maintain an **open-source** model under the **AGPL-3.0 license**.

---

## 🟦 Business Goals / Use Cases

- Provide **effective and accessible mental training** for all users.
- Encourage **motivation and consistency** through achievements.
- Offer **progress tracking and insights** to users.
- Ensure **cloud synchronization** across multiple devices.
- Maintain a **completely free** development model with no ads or payments.

Full use cases can be found in ▶ [Use Cases](docs/use_cases.md).

---

## 🟦 Data Sources

BrainAlpha stores and manages data using **Supabase**, an open-source database compatible with PostgreSQL.

- **User data** (authentication and progress tracking).
- **Game data** (statistics, levels, difficulty, session history).
- **Leaderboard data** (user rankings per game).
- **Achievements tracking** (unlocked achievements).(Pending)

For details on data structure, check ▶ [Data Model](docs/data_model.md).

---

## 🟦 Applied Technologies

| **Area**        | **Technologies Used** |
|----------------|----------------------|
| **Frontend**   | Kotlin + Jetpack Compose (Android) / JavaFX (Desktop) |
| **Backend**    | Supabase (Firebase optional) |
| **Database**   | Supabase (PostgreSQL) |
| **Infrastructure** | GitLab CI/CD for continuous integration |
| **Authentication** | Supabase Auth (Firebase optional) | 
| **Notifications** | WorkManager | (Pending)

---

## 🟦 Development Roadmap

BrainAlpha is continuously evolving. Here’s what’s planned for future updates:

- **Sprint 1:** Database setup, authentication system, first game prototype.
- **Sprint 2:** Improved UI/UX, more cognitive training games, statistics system.
- **Sprint 3:** Achievements, leaderboard, and advanced customization.

For a full roadmap, check ▶ [Development Roadmap](docs/roadmap.md).

---

## 🟦 Wireframes

The UI mockups were designed to **illustrate the core experience of BrainAlpha**, including:
1. **Authentication Screens** (Sign Up, Login).
2. **Main Categories Selection**.
3. **Gameplay Screens** (Memory, Logic, Concentration, and Mental Agility).
4. **Leaderboard and Achievements(Pending)**.

View the full set of designs here ▶ [Mockups & UI](docs/mockups.md).

---

## 🟦 Non-Functional Requirements

To ensure **high-quality standards**, BrainAlpha meets the following non-functional requirements:

- **Availability:** 99.9% uptime through cloud infrastructure.
- **Performance:** Optimized for mid and low-range devices.
- **Security:** Data protection with encryption and secure authentication.
- **Accessibility:** Interface adapted for people with cognitive challenges.
- **Cross-Platform Support:** Works on both **Android** and **Desktop**.

Full list of non-functional requirements in ▶ [Non-Functional Requirements](docs/non_functional_requirements.md).

---

## 🟦 Open Source License

BrainAlpha is an **open-source project** under the **GPL license**, allowing:

-  Free usage of the application.
-  Code modification to fit different needs.
-  Contributions to improve and expand the project.

More details ▶ [License](LICENSE).

---

## 🟦 Installation Guide

### **Prerequisites**
- Java JDK 11+
- Android SDK (for mobile development)
- Supabase API Key (for database access)

### **Clone the Repository**
```bash
git clone https://gitlab.com/provencat/dam/brain-alpha.git

cd brain-alpha
```

### **Run on Android**
```bash
./gradlew assembleDebug
```

### **Run on Desktop**
```bash
java -jar BrainAlpha.jar
```

## 🟦 How to contribute

1. Fork the repository on GitLab.
2. Clone your forked repository:
```bash
git clone https://gitlab.com/yourusername/brain-alpha.git
```
3. Create new branch:
```bash
git checkout -b feature/new_feature
```
4. Make changes and push your code:
```bash
git add .
git commit -m "Added new feature"
git push origin feature/new_feature
```
5. Open and Merge Request on GitLab.


------------------------------
