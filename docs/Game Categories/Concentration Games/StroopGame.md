# 🧠 Stroop Test – Game Documentation

## 🎯 Game Objective

The Stroop Test is a mental agility and focus exercise.  
The player's goal is to **select the color in which the word is written**, not the meaning of the word.

---

## 🛠️ Mechanics

- A **word** representing a color (e.g., `"Blue"`) is shown in a **different font color** (e.g., **red**).
- The player must click the button that matches the **actual display color**, not the word's text.
- Each correct answer earns 1 point.
- The game runs for a total of **30 seconds**.
- When time runs out, a summary screen shows the final score.

---

## 📲 User Interface

- Initial screen with **clear instructions** and a `Start Game` button.
- Main word (the challenge) is styled with **shadow** for better readability.
- Response buttons show color names in uppercase and high contrast.
- Visual feedback for answers: `"Correct!"` or `"Incorrect"` (auto-dismissed after 0.6s).
- Live score is displayed during gameplay.

---

## ✅ Completed MVP Features

| Feature                          | Status   |
|----------------------------------|----------|
| Instructions before game start   | ✅        |
| Time limit (30s)                 | ✅        |
| Word with real font color        | ✅        |
| Buttons with color options       | ✅        |
| Score based on correct answers   | ✅        |
| End screen with summary          | ✅        |
| Feedback on correct/wrong answer | ✅        |

---

## 📌 Future Features

- Feedback sounds (`Correct` / `Incorrect`)
- Language setting support
- Score history & analytics
- High score tracking
- Global settings and navigation migration
