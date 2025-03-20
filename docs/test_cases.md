# 🟦 BrainAlpha - Test Cases

##Overview
This document contains the **test cases** for the MVP version of BrainAlpha.

---

## 🟦 1️⃣ User Registration & Authentication
### **Test Case 1.1 - User Sign Up**
- **Description:** Verify that a user can successfully register an account.
- **Preconditions:** User must have a valid email.
- **Steps:**
  1. Open the BrainAlpha app.
  2. Navigate to the Sign-Up page.
  3. Enter a valid email and password.
  4. Click "Register".
- **Expected Result:** The user should receive a confirmation message and be redirected to the home screen.

### **Test Case 1.2 - User Login**
- **Description:** Verify that a registered user can log in.
- **Preconditions:** The user must already be registered.
- **Steps:**
  1. Open the BrainAlpha app.
  2. Navigate to the Login page.
  3. Enter valid credentials.
  4. Click "Login".
- **Expected Result:** The user is authenticated and taken to the main screen.

---

## 🟦 2️⃣ Playing a Cognitive Training Game
### **Test Case 2.1 - Start a Game**
- **Description:** Verify that a user can start a training session.
- **Preconditions:** User must be logged in.
- **Steps:**
  1. Navigate to the game selection screen.
  2. Choose a game from the list.
  3. Click "Start Game".
- **Expected Result:** The selected game launches and is playable.

### **Test Case 2.2 - End a Game Session**
- **Description:** Verify that a game session properly ends and saves results.
- **Preconditions:** User must be playing a game.
- **Steps:**
  1. Play the game for at least 10 seconds.
  2. Click "End Session".
- **Expected Result:** The session data is saved in the database.

---

## 🟦 3️⃣ Saving User Progress
### **Test Case 3.1 - Save Game Progress**
- **Description:** Verify that a user’s score is saved after playing a game.
- **Preconditions:** User must have completed a game session.
- **Steps:**
  1. Finish a game.
  2. Exit the session.
- **Expected Result:** The user's score is updated in the **user_progress** table.

---

## 🟦 4️⃣ Viewing User Statistics
### **Test Case 4.1 - View Progress**
- **Description:** Verify that users can check their training progress.
- **Preconditions:** User must have played at least one game.
- **Steps:**
  1. Navigate to the "Progress" section.
  2. View score history and achievements.
- **Expected Result:** User can see game statistics.

---

## 🟦 5️⃣ Personalizing Settings
### **Test Case 5.1 - Change Language**
- **Description:** Verify that a user can change the app's language.
- **Preconditions:** User must be logged in.
- **Steps:**
  1. Open settings.
  2. Change the language from English to Spanish.
  3. Save settings.
- **Expected Result:** The app updates its language.

### **Test Case 5.2 - Enable Dark Mode**
- **Description:** Verify that a user can enable dark mode.
- **Preconditions:** User must be logged in.
- **Steps:**
  1. Open settings.
  2. Enable "Dark Mode".
  3. Save settings.
- **Expected Result:** The app switches to dark mode.

---