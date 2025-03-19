# 🟦 BrainAlpha - Use Cases

##  Overview
This document outlines the key use cases of the BrainAlpha application for the MVP version.

---

## 🟦 Use Case 1: User Registration & Authentication  
**Description:** The user registers and logs in to the app using Supabase Auth.  

### **Actors**
- **Primary Actor:** User  
- **Secondary Actor:** Supabase Auth  

### **Preconditions**
✔ The user has a valid email.  
✔ The app is connected to Supabase.  

### **Flow of Events**
1️⃣ The user opens the app and chooses **Sign Up** or **Log In**.  
2️⃣ The user enters their **email** and **password**.  
3️⃣ The system validates the credentials using **Supabase Auth**.  
4️⃣ If successful, the user is redirected to the **home screen**.  

### **Postconditions**
✔ The user is authenticated and can use the app.  
✔ If it’s a new user, a record is created in the **`users`** table.  

---

## 🟦 Use Case 2: Playing a Cognitive Training Game  
**Description:** The user selects a game and plays a training session.  

### **Actors**
- **Primary Actor:** User  
- **Secondary Actor:** BrainAlpha App, Supabase Database  

### **Preconditions**
✔ The user is logged in.  
✔ The user has an internet connection (for progress sync).  

### **Flow of Events**
1️⃣ The user selects a **game category** (Memory, Logic, Focus, etc.).  
2️⃣ The system loads the game from the **`games`** table.  
3️⃣ The user plays the game session.  
4️⃣ The system calculates the **score**.  
5️⃣ The system saves the session data in the **`game_sessions`** table.  

### **Postconditions**
✔ The game session is recorded in the database.  
✔ The user's **`user_progress`** is updated.  

---

## 🟦 Use Case 3: Viewing Progress & Statistics  
**Description:** The user checks their cognitive progress through statistics.

### **Actors**
- **Primary Actor:** User  
- **Secondary Actor:** BrainAlpha App, Supabase Database  

### **Preconditions**
✔ The user has completed at least one game session.  
✔ The user's progress is stored in Supabase.  

### **Flow of Events**
1️⃣ The user navigates to the **progress tracking section**.  
2️⃣ The system fetches user data from the **`user_progress`** table.  
3️⃣ The system displays **game statistics** (scores, trends, achievements).  

### **Postconditions**
✔ The user sees their improvement over time.  
✔ Data is synchronized in real time with Supabase.  

**For more details, see the UML Use Case Diagram in [`use_case_diagram.md`](use_case_diagram.md).**  

---

## 🟦 Use Case 4: Customizing User Preferences  
**Description:** The user modifies their app settings (dark mode).

### **Actors**
- **Primary Actor:** User  
- **Secondary Actor:** BrainAlpha App, Supabase Database  

### **Preconditions**
✔ The user is logged in.  

### **Flow of Events**
1️⃣ The user navigates to the **settings** page.  
2️⃣ The user modifies preferences (e.g., toggles dark mode, selects language).  
3️⃣ The system updates the **`settings`** table with the new preferences.  

### **Postconditions**
✔ User preferences are saved persistently.  
✔ The app interface updates immediately based on changes.  

---