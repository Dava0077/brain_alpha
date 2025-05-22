# 🏆 Leaderboard Screen – User Guide

## 🎯 Purpose

The **Leaderboard** screen allows you to browse the **top global scores** across all games in Brain Alpha.

You can explore rankings based on:

- 🧠 Specific game
- 📊 Difficulty level (Easy, Medium, Hard)
- 🕒 Average time or score
- 🔼 Ascending or descending order

---

## 📊 What does the leaderboard show?

Each leaderboard entry includes:

- 👤 Username
- 🧠 Score
- ⏱ Average time (or difficulty)
- 📅 Date played

Entries are grouped by game category:  
For example: **Memory**, **Logic**, **Concentration**, **Mental Agility**.

---

## 📂 Categories & Games

Games are grouped into the following categories:

| Category         | Included Games                             |
|------------------|---------------------------------------------|
| 🧠 Memory         | Pairs Game, Simon Game                      |
| 🔍 Logic          | Sudoku, Word Search                         |
| 🎯 Concentration  | Stroop Test, Odd Emoji                      |
| ⚡ Mental Agility | Mental Math, Click Number                   |

---

## 🔽 Filters & Options

You can customize how results are displayed:

- **Sort by**:
  - 🧠 Score (`score`)
  - ⏱ Average time (`avg_time`)
- **Direction**:
  - 🔼 Ascending
  - 🔽 Descending

Each category and game can be expanded to view specific rankings.

---

## 🎮 Score Table

When a game is expanded, you will see either:

- 🟢 Easy
- 🔵 Medium
- 🔴 Hard

Or a **merged view** by username (combined scores from all difficulties).

Each row includes:

| Rank | User   | Score | Time or Difficulty | Date |
|------|--------|-------|---------------------|------|

---

## 📦 Data Source

Data is retrieved from **Supabase** with filters applied for:

- 🆔 Game ID
- 🎚️ Difficulty
- 🧠 Sort criteria (score or time)
- 🔼 Order (asc/desc)

Each expandable block triggers a query and caches the results in memory to avoid unnecessary reloads.

---

## 🧭 Navigation

From this screen you can:

- 🔙 Return to the home menu (via the “Back” button)
- 👁️ View rankings per game and difficulty
- 🔄 Change sorting and direction with a single tap

```kotlin
fun goHome() {
    navigateTo("home")
}
