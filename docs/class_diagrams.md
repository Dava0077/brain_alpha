# 🟦 BrainAlpha - Class Diagram

## Overview
This document provides the **Class Diagram** for the MVP version of BrainAlpha.

---

## 🟦 Class Diagram
The following diagram represents the **main classes and relationships** in the system:

![Class Diagram](class_diagram.png)

---

## 🟦 Class Definitions

### **User (User Management)**
- **Attributes:**
  - `id: UUID`
  - `email: String`
  - `name: String`
  - `createdAt: Date`
- **Methods:**
  - `signUp()`
  - `signIn()`
  - `signOut()`
  - `loadUserProfile()`
  - `ensureUserExists()`
  - `deleteUserFromAuth()`
  - `sendPasswordReset()`
  - `resendVerification()`

---

### **Game (Game Information)**
- **Attributes:**
  - `id: UUID`
  - `name: String`
  - `description: String`
  - `category: String`
- **Methods:**
  - `getGameIdByName()`
  - `getGameById()`

---

### **UserProgress (Tracking User Progress)**
- **Attributes:**
  - `id: UUID`
  - `userId: UUID`
  - `gameId: UUID`
  - `score: Int`
  - `lastPlayed: Date`
- **Methods:**
  - `upsertUserProgress()`
  - `getAllTotalScores()`

---

### **GameSession (Game Play Sessions)**
- **Attributes:**
  - `id: UUID`
  - `userId: UUID`
  - `gameId: UUID`
  - `score: Int`
  - `difficulty: String`
  - `timeAvg: Double?`
  - `timeLeft: Double?`
  - `playedAt: Date`
- **Methods:**
  - `registerGameSession()`
  - `getLeaderboard()`
  - `getLeaderboardGroupedByDifficulty()`
  - `getGlobalLeaderboardByGameId()`
  - `getTotalScore()`

---