# ⚙️ Settings Screen – User Guide

## 🎯 Purpose

The **Settings screen** allows users to customize their app experience and manage account-related preferences.

From here, users can:

- 🔁 Change the **language** of the app interface
- 👤 Navigate to their **profile** to change their name, email, or password
- 🔙 Return to the **home screen**

---

## 🧩 Interface Overview

- 🧠 **Top Bar**: Displays the "Settings" title centered in a dark toolbar.
- 🏠 **Back Button**: Allows you to return to the home screen.
- 🌐 **Language Selector**: Toggle between **English** and **Español**.
- 👤 **Account & Profile Section**:
  - Change your username
  - Change your email
  - Change your password

All account actions redirect to the **Profile Screen**.

---

## 🌍 Language Selection

To switch languages:

1. Tap the language currently displayed ("English" or "Español").
2. Choose your preferred language from the dropdown.
3. The interface will update immediately to reflect your choice.

---

## 👤 Account & Profile Access

Each of the following actions:

- 🔤 Change Username
- 📧 Change Email
- 🔐 Change Password

...will navigate you to the same **Profile Screen**, where edits are made securely.

---

## 🔙 Navigation

Use the **Back** button to return to the home screen at any time.

```kotlin
fun goHome() {
    navigateTo("home")
}
