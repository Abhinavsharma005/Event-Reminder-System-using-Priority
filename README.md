# Event Reminder System with Priority & Alarms

A lightweight, local desktop application built using **Java AWT (Abstract Window Toolkit)** and connected to an embedded **SQLite Database** to log, organize, and trigger alarms for daily tasks and events.

*This project was developed as a Mini Project Assessment assigned by the University for Java Programming using AWT and Database Connectivity.*

---

## 📌 Project Overview
Students and individuals often struggle to keep track of deadlines, meetings, exams, and personal events. This application provides a simple interface to log events with specific priority levels (High, Medium, Low) and set a target alarm time. A background thread continually runs to check database entries and display a popup notification when the reminder time is reached.

---

## 📸 User Interface Screenshots

### Main Dashboard & Event List
![Main Dashboard](demo%20screenshot1.jpeg)

### Active Event Alarm Popup
![Alarm Dialog](demo%20screenshot2.jpeg)

---

## 🛠️ Tech Stack & Features
* **GUI Framework**: Java AWT (Frames, Labels, Panels, Layout Managers)
* **Database**: SQLite (No external database installation needed, stores data locally in `events.db`)
* **Database Connectivity**: SQLite JDBC Driver
* **Visual Theme**: Replicated custom light blue-grey card UI with color-coded priorities (Red/Orange/Green) and emoji indicators
* **Concurrency**: Background Daemon thread running every 15 seconds to check system time against reminder schedules
* **Popup Dialogs**: Modular AWT popup reminder dialogs for instant notifications

---

## 📁 File Structure
```
EventReminderProject/
│
├── EventReminder.java        # Main Java Source Code file containing all GUI and DB logic
├── EventReminder.class       # Compiled Java Bytecode file
├── EventReminder$1.class     # Compiled Anonymous Class (for Window Closing listener)
├── sqlite-jdbc-3.53.1.0.jar  # SQLite JDBC Driver jar library (required for DB connection)
├── events.db                 # Database file containing the SQLite table (auto-created)
└── README.md                 # Project documentation file (this file)
```

---

## 🔌 Database Schema
The database schema contains a single table called `events` which is automatically generated on startup:
```sql
CREATE TABLE IF NOT EXISTS events (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL,
    priority TEXT NOT NULL,
    description TEXT,
    reminder_time TEXT
);
```

---

## 🚀 Setup & Installation Instructions

### Step 1: Place the JDBC Driver JAR
Ensure the SQLite JDBC Driver JAR file (e.g. `sqlite-jdbc-3.53.1.0.jar`) is in the same directory as your `EventReminder.java` file. You can download the latest version from the official GitHub Releases of the [sqlite-jdbc](https://github.com/xerial/sqlite-jdbc) repository.

### Step 2: Open Terminal/Command Prompt
Open your terminal (CMD, PowerShell, or bash) and navigate to the project directory:
```cmd
cd "c:\Java Programs\EventReminderProject"
```

### Step 3: Compile the Java Code
Compile the code referencing the JDBC library classpath:

* **Windows (CMD/PowerShell):**
  ```cmd
  javac -cp ".;sqlite-jdbc-3.53.1.0.jar" EventReminder.java
  ```
* **macOS / Linux:**
  ```bash
  javac -cp ".:sqlite-jdbc-3.53.1.0.jar" EventReminder.java
  ```

### Step 4: Run the Application
Run the compiled application specifying the same classpath:

* **Windows (CMD/PowerShell):**
  ```cmd
  java -cp ".;sqlite-jdbc-3.53.1.0.jar" EventReminder
  ```
* **macOS / Linux:**
  ```bash
  java -cp ".:sqlite-jdbc-3.53.1.0.jar" EventReminder
  ```

---

## 💡 How it Works (Application Flow)
1. **Startup**: The main class loads the SQLite JDBC driver, builds the window GUI, and initializes/migrates the SQLite database `events.db`.
2. **Adding Reminders**: 
   - Fill in **Event Name**, select **Priority** (High/Medium/Low), input **Time** in `HH:mm` format (e.g. `14:30`), and write a **Description**.
   - Clicking **Add Event** validates the inputs, runs an SQL `INSERT` statement, resets form fields, and automatically updates the display list on the right.
3. **Display List**: The dynamic event display pulls records from the database and constructs separate custom card components, color-coding high-priority tasks in Red, medium in Orange, and low in Green.
4. **Alarm Reminders**: A background thread polls the system time. If the current time matches an event's reminder time, a custom modal alert window pops up on the screen to remind the user.
5. **Clearing Forms**: Clicking **Clear** resets the form inputs to blank and sets the Time field to the current system time.
