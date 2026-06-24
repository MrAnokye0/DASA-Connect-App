# DASA Connect 2026 - MySQL Backend Server

Welcome to the **Digital Assets Summit Africa (DASA) 2026** backend service! This service runs a lightweight, high-performance Node.js & Express REST API that integrates directly with a **MySQL Database**. 

Our Android App uses an **offline-first approach** with local Room Database, meaning the app remains 100% functional even when offline, and automatically background-syncs profile edits, session bookmarks, and summit feedback to this remote MySQL database when a network is available.

---

## 🏗️ Architecture

```
                      +-----------------------------+
                      |   DASA Connect Android App  |
                      |   (Jetpack Compose Client)  |
                      +--------------+--------------+
                                     |
                                     | HTTP REST Requests (JSON)
                                     v
                      +--------------+--------------+
                      |    Express API Middleware   |
                      |      (Node.js / CORS)       |
                      +--------------+--------------+
                                     |
                                     | Port 3306 (Secure SSL/Tunnel)
                                     v
                      +--------------+--------------+
                      |        MySQL Database       |
                      |   (schema.sql Schema)       |
                      +-----------------------------+
```

---

## 🛠️ Requirements

1. **Node.js** (v16.0.0 or higher recommended)
2. **MySQL Database Server** (v8.0 or higher recommended)
   - Either a local MySQL Server (MAMP, XAMPP, or manual installation)
   - Or a cloud-based instance (GCP Cloud SQL, AWS RDS, PlanetScale, etc.)

---

## 🚀 Step 1: Initialize the MySQL Database

1. Open your MySQL client (e.g., MySQL Workbench, DBeaver, phpMyAdmin, or terminal).
2. Execute the entire database initialization script located in this folder:
   ```bash
   mysql -u your_username -p < schema.sql
   ```
   *This creates a database called `dasa_connect_db` and seeds it with speakers, sponsors, event sessions, and networking attendees matching the Android app state!*

---

## ⚡ Step 2: Configure Environment Variables

Create a file named `.env` inside the `backend` folder (or copy from root `.env.example`) and supply your database credentials:

```ini
PORT=3000

# MySQL Database Configurations
DB_HOST=localhost
DB_PORT=3306
DB_USER=root
DB_PASSWORD=your_secure_password
DB_NAME=dasa_connect_db
```

---

## 📦 Step 3: Run the Backend Service

1. Navigate to the `backend` directory:
   ```bash
   cd backend
   ```
2. Install the necessary Node.js packages:
   ```bash
   npm install
   ```
3. Start the server in production mode:
   ```bash
   npm start
   ```
   *For development with auto-reload, you can run:*
   ```bash
   npm run dev
   ```

You should see output similar to:
```text
=======================================================
🌐 DASA Connect Backend Server running on port 3000
🔌 Local Emulator URL: http://10.0.2.2:3000
=======================================================
📡 Attempting connection to MySQL at localhost:3306...
✅ Connected successfully to MySQL database!
🔨 Bootstrapping database schema and checking tables...
🚀 Database schema bootstrap complete!
```

---

## 📱 Step 4: Connecting the Android Emulator

Because the Android emulator runs inside a sandbox, standard `localhost` references in your Android app map to the emulator itself, *not* the host computer.

- To connect your emulator to this Express + MySQL backend running on your host machine, use the loopback IP: **`http://10.0.2.2:3000`**.
- This is already configured as the default fallback inside the Android client's `RetrofitClient`!
- You can override this URL by setting `BACKEND_API_URL` in your `.env` file in the root of the Android project.
