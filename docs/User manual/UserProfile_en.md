# 👤 User Profile Screen – Guide

## 🎯 Purpose

The **User Profile** screen lets you:

- View your current username and email address.
- Check your total progress across all games.
- Edit your name, email, or password.
- Log out or delete your account securely.

---

## 🖼️ Interface Overview

### 🔷 Header

- **Title:** "Profile"
- Top app bar with centered white text and dark background.

### 👤 User Info Section

- Circular user avatar
- Username and email displayed beneath
- Top action buttons:
  - 🔴 **Log Out**
  - 🗑️ **Delete Account**
  - 🔙 **Back to Previous Screen**

### ✏️ Editable Fields

- Update **Name**
- Update **Email** *(requires current password)*
- Update **Password** *(requires current password)*

Each action opens a secure dialog for input.

---

## 🧠 Progress Overview

Shows total performance per minigame stored in your profile.

| Game               | Total Neurons | Current Difficulty |
|--------------------|---------------|--------------------|
| Stroop Test        | 240           | Hard               |
| Sudoku             | 85            | Medium             |
| Odd Emoji          | 30            | Easy               |

Each item includes:

- Game icon and title
- Total score (neurons)
- Difficulty badge (easy / medium / hard)

Also includes a **legend** explaining difficulty icons.

---

## 🔐 Security & Confirmations

- If your email is not verified, a **"Resend Verification"** button appears.
- Log out confirmation modal is shown before signing out.
- Account deletion also shows a confirmation and provides result feedback.

---

## ⚙️ Technical Functionality

- Data is fetched from Supabase and synced into `UserSession`.
- All updates are persisted both in Supabase and local state.
- Email/password changes require password verification.
- Progress is loaded from `GameSessionController.getAllTotalScores()`.

---

## 📌 Notes

- Some features may be restricted for guest users.
- It's recommended to verify your email to maintain account security.
- UI is fully responsive and works across screen sizes.

---

📈 Ready to track your progress?  
**Review your neurons and level up your brain!**
