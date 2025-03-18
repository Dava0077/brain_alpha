# 🟦 BrainAlpha - Use Case Diagram

##  Overview
This document presents the UML **Use Case Diagram** for the MVP version of BrainAlpha.

---

## 🟦 Use Case Diagram
Below is a graphical representation of the main interactions between the user and the system.

 **[Insert UML diagram here]**

---

## 🟦 Explanation
The **actors** and **interactions** represented in the diagram:
- **User** interacts with:
  - **Authentication System** (via Supabase Auth)
  - **Game System** (select and play cognitive games)
  - **Progress Tracking System** (view statistics)
- **The system records:**
  - Game sessions (`game_sessions` table)
  - User progress (`user_progress` table)

 **For full details on each use case, see [`use_cases.md`](use_cases.md).**