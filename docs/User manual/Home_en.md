# 🏠 Home Screen – User Guide

## 🎯 Purpose

The **Home Screen** is the central hub after a successful login. From here, users can:

- Access their profile.
- Navigate to game categories like Memory, Logic, Concentration, and Mental Agility.
- View global leaderboards.
- Open settings.
- Safely log out of their session.

---

## 🖼️ Interface Overview

### 🧑‍🎓 Top Bar

- 📷 **User avatar** (navigates to profile screen)
- ⚙️ **Settings icon**
- 🚪 **Logout icon**
- 👋 **Greeting** with the user’s display name

### 🏆 Leaderboard Button

- Direct access to the **global leaderboard**
- Positioned below the top bar

### 🧠 Game Categories

Displayed in two rows as clickable cards:

| Category            | Icon                    | Border Color |
|---------------------|--------------------------|--------------|
| 🧠 Memory            | `iconoMem`               | Cyan         |
| 🧩 Logic             | `iconoLog`               | Cyan         |
| 🎯 Concentration     | `icono_concentracion`    | Cyan         |
| ⚡ Mental Agility    | `iconoAgi`               | Cyan         |

Each card includes:

- Themed image
- Category name
- On-click: navigates to that category's game list

### 🖼️ Visual Design

- Background image: `Login2`
- Floating logo centered at the top: `logo`
- Bottom black line for design polish

---

## 🔒 Logout Process

When the user clicks the logout icon:

1. A **confirmation dialog** appears.
2. If confirmed:
   - Executes `AuthController.signOut()`
   - Clears the session (`LoginController.clear()`)
   - Redirects to the login screen
   - Displays a snackbar with a success message

---

## ⚙️ Technical Features

- **Session recovery**: Uses Supabase to restore session data if available.
- **Scaffold with Snackbar**: For success/error feedback.
- **Vertical scroll support**: Optimized for small screen devices.
- **Responsive layout**: Scales well across screen sizes and orientations.

---

## 🔁 Available Navigation Paths

| Action                       | Target Screen           |
|------------------------------|--------------------------|
| Tap avatar                   | Profile screen           |
| Tap settings icon            | Settings screen          |
| Tap leaderboard button       | Global leaderboard view  |
| Tap category card            | Game list by category    |

---

📌 **Tip:** Use this screen as your launchpad to train your brain with games organized by cognitive skills.
