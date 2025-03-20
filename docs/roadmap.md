# 🧠 BrainAlpha - Development Roadmap

## Overview
This document outlines the **development roadmap** for BrainAlpha, covering the different **sprints** and their respective goals.

---

## 🟦 📅 Sprint 0: Project Setup & Planning (March 2025)
✅ **Project planning and documentation**
- Define **project objectives** and **scope**.
- Set up **GitLab repository** and **issue tracking**.
- Document **use cases**, **functional requirements**, and **non-functional requirements**.
- Design **wireframes** and initial **UI mockups**.

✅ **Infrastructure & Initial Codebase**
- Configure **Supabase database**.
- Define **database schema** and create SQL script.
- Set up **CI/CD pipeline** in GitLab.
- Deploy initial **“Hello World” app** (Android & Desktop).

✅ **Documentation**
- **README.md**, **LICENSE.md**, **USE_CASES.md**, **TEST_CASES.md**.
- Diagrams: **UML, ERD, Class Diagrams**.

---

## 🟦 📅 Sprint 1: Core Development - MVP (April 2025)
🎯 **Goal:** Build the **Minimum Viable Product (MVP)** with basic features.

 **Core Features**
- **User Authentication System** (Register, Login, Logout) via **Supabase Auth**.
- **Game System Implementation**:
    - Develop **first cognitive training games** (e.g., Memory, Concentration).
    - Basic game mechanics (start, play, end, scoring).
- **User Progress Tracking**:
    - Store **game sessions** in the database.
    - Display **progress statistics** for logged-in users.
- **Offline Mode**:
    - Allow playing without login (no progress tracking).

🛠 **Technical Tasks**
- Implement **Supabase queries** for fetching and saving data.
- Develop **initial UI** for login, game selection, and leaderboard.
- Unit tests for **authentication and game sessions**.

**Sprint Deliverables**
- Fully working authentication system.
- At least **two playable games**.
- User progress tracking working in the database.

---

## 🟦 📅 Sprint 2: Feature Expansion (May 2025)
🎯 **Goal:** Enhance gameplay experience and add advanced features.

 **Feature Enhancements**
- **More Games & Game Categories**:
    - Implement **Sudoku, Quick Reaction, Stroop Test, Word Search**.
    - Improve UI and animations.
- **Leaderboard System**:
    - Store and display **rankings per game**.
- **Achievements System**:
    - Unlockable achievements based on user performance.
- **User Settings**:
    - Implement **dark mode**, language selection, and difficulty levels.

🛠 **Technical Tasks**
- Improve **game performance** (reduce load times, optimize UI).
- Implement **Supabase Realtime** for live leaderboard updates.
- Write **integration tests** for leaderboard and game scoring.

**Sprint Deliverables**
- Fully implemented leaderboard.
- At least **four additional games**.
- Working **achievement system**.

---

## 🟦 📅 Sprint 3: Optimization & Final Release (June 2025)
🎯 **Goal:** Polish, optimize, and prepare for public release.

**Final Enhancements**
- **UI/UX Improvements**:
    - Improve game animations and responsiveness.
    - Optimize UI for **tablet devices**.
- **Bug Fixes & Testing**:
    - Perform **extensive user testing**.
    - Fix critical issues and **performance bottlenecks**.
- **Final App Release**:
    - Build and package **official APK and desktop version**.
    - Upload to **Google Play Store**.

 **Sprint Deliverables**
- Fully stable and optimized version of BrainAlpha.
- Public release of the **final version**.

---

## Summary
BrainAlpha follows a **three-sprint roadmap**, focusing on:

1️⃣ **Sprint 0:** Planning & Initial Setup  
2️⃣ **Sprint 1:** Core MVP Development  
3️⃣ **Sprint 2:** Feature Expansion & Enhancements  
4️⃣ **Sprint 3:** Optimization & Final Release

