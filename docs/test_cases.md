# 🟦 BrainAlpha - Test Cases

## Overview
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

### **Test Case 1.2 - User Sign Up (Existing Email)**
- **Description:** Verify that an error is shown if a user tries to register with an email already in use.
- **Preconditions:** The email is already registered in the database.
- **Steps:**
  1. Open the BrainAlpha app.
  2. Navigate to the Sign-Up page.
  3. Enter an email that is already registered.
  4. Click "Register".
- **Expected Result:** The app displays an error message: **"This email is already in use."**  

### **Test Case 1.3 - User Login**
- **Description:** Verify that a registered user can log in.
- **Preconditions:** The user must already be registered.
- **Steps:**
  1. Open the BrainAlpha app.
  2. Navigate to the Login page.
  3. Enter valid credentials.
  4. Click "Login".
- **Expected Result:** The user is authenticated and taken to the main screen.

### **Test Case 1.4 - Invalid Login Attempt**
- **Description:** Verify that a user receives an error message when entering incorrect credentials.
- **Preconditions:** The user is registered but enters wrong credentials.
- **Steps:**
  1. Open the BrainAlpha app.
  2. Navigate to the Login page.
  3. Enter an incorrect password.
  4. Click "Login".
- **Expected Result:** The app displays an error message: **"Invalid email or password."**  

### **Test Case 1.5 - Login with Invalid Email Format**
- **Description:** Verify that a user cannot log in with an improperly formatted email.
- **Steps:**
  1. Open the BrainAlpha app.
  2. Navigate to the Login page.
  3. Enter "invalid_email" as email.
  4. Enter a password.
  5. Click "Login".
- **Expected Result:** The app shows an error: **"Invalid email format."**  


---

## 🟦 2️⃣ Playing a Cognitive Training Game
### **Test Case 2.1 - Start a Game (Logged-in User)**
- **Description:** Verify that a logged-in user can start a game.
- **Preconditions:** User must be authenticated.
- **Steps:**
  1. Navigate to the game selection screen.
  2. Choose a game from the list.
  3. Click "Start Game".
- **Expected Result:** The selected game launches and is playable.

### **Test Case 2.2 - Start a Game (Guest User)**
- **Description:** Verify that a guest user can start a game without login.
- **Preconditions:** The user is not logged in.
- **Steps:**
  1. Open the app.
  2. Choose a game.
  3. Click "Start Game".
- **Expected Result:** The game starts normally, but the score is **not saved** to the database.

### **Test Case 2.3 - End a Game Session**
- **Description:** Verify that a game session properly ends and saves results.
- **Preconditions:** User must be playing a game.
- **Steps:**
  1. Play the game for at least 10 seconds.
  2. Click "End Session".
- **Expected Result:** 
  - **If logged in:** The session data is saved in the database.  
  - **If playing as guest:** The session ends, but no progress is saved.

### **Test Case 2.4 - Pause Game**
- **Description:** Verify that a user can pause a game session and resume it.
- **Preconditions:** User must be playing a game.
- **Steps:**
  1. Start a game.
  2. Click the "Pause" button.
  3. Wait for 5 seconds.
  4. Click the "Resume" button.
- **Expected Result:** The game resumes from the paused state with the same progress.

### **Test Case 2.5 - Network Disconnection While Playing**
- **Description:** Verify how the game handles a network disconnection.
- **Preconditions:** The user is playing a game.
- **Steps:**
  1. Start a game.
  2. Disconnect the internet.
  3. Play for another 10 seconds.
  4. End the session.
- **Expected Result:** The game continues running, but **no data is saved** until the connection is restored.


---

## 🟦 3️⃣ Saving User Progress
### **Test Case 3.1 - Save Game Progress (Logged-in User)**
- **Description:** Verify that a user’s score is saved after playing a game.
- **Preconditions:** User must be authenticated and have completed a game session.
- **Steps:**
  1. Finish a game.
  2. Exit the session.
- **Expected Result:** The user's score is updated in the **user_progress** table.

### **Test Case 3.2 - Play as Guest (No Progress Saved)**
- **Description:** Verify that guest users' progress is **not saved**.
- **Preconditions:** The user is playing without login.
- **Steps:**
  1. Play a game as a guest.
  2. Finish a session.
- **Expected Result:** The game ends, but the **score is not stored**.

### **Test Case 3.3 - Try to View Progress Without Playing**
- **Description:** Verify that a user who has never played a game sees an appropriate message.
- **Preconditions:** The user is logged in but has not played any game.
- **Steps:**
  1. Navigate to the "Progress" screen.
- **Expected Result:** The app displays **"No progress data available."**  


---

## 🟦 4️⃣ Viewing User Statistics
### **Test Case 4.1 - View Progress**
- **Description:** Verify that users can check their training progress.
- **Preconditions:** User must be logged in and have played at least one game.
- **Steps:**
  1. Navigate to the "Progress" section.
  2. View score history and achievements.
- **Expected Result:** User can see game statistics.

### **Test Case 4.2 - Check Leaderboard (Only Logged-in Users)**
- **Description:** Verify that only authenticated users appear in the leaderboard.
- **Preconditions:** At least one logged-in user has played a game.
- **Steps:**
  1. Open the leaderboard.
  2. Check user rankings.
- **Expected Result:**  
  - **If logged in:** The user's score appears on the leaderboard.  
  - **If guest:** The score **does not appear** in the leaderboard.

---

## 🟦 5️⃣ Achievements System
### **Test Case 5.1 - Unlock an Achievement**
- **Description:** Verify that achievements unlock when the user meets the required conditions.
- **Preconditions:** User must have completed a required task (e.g., playing 5 games).
- **Steps:**
  1. Complete a milestone (e.g., "Win 5 games").
  2. Check the achievements page.
- **Expected Result:** A new achievement is unlocked.

---

## 🟦 6️⃣ Personalizing Settings
### **Test Case 6.1 - Change Language**
- **Description:** Verify that a user can change the app's language.
- **Preconditions:** User must be logged in.
- **Steps:**
  1. Open settings.
  2. Change the language from English to Spanish.
  3. Save settings.
- **Expected Result:** The app updates its language.

### **Test Case 6.2 - Enable Dark Mode**
- **Description:** Verify that a user can enable dark mode.
- **Preconditions:** User must be logged in.
- **Steps:**
  1. Open settings.
  2. Enable "Dark Mode".
  3. Save settings.
- **Expected Result:** The app switches to dark mode.

---