const express = require('express');
const mysql = require('mysql2/promise');
const cors = require('cors');
require('dotenv').config();

const app = express();
const PORT = process.env.PORT || 3000;

// Enable JSON middleware and CORS for remote Android clients
app.use(cors());
app.use(express.json());

// MySQL connection pool configuration
const dbConfig = {
  host: process.env.DB_HOST || 'localhost',
  user: process.env.DB_USER || 'root',
  password: process.env.DB_PASSWORD || '',
  database: process.env.DB_NAME || 'dasa_connect_db',
  port: parseInt(process.env.DB_PORT || '3306', 10),
  waitForConnections: true,
  connectionLimit: 10,
  queueLimit: 0
};

let pool;

// Initialize database connection and verify/setup tables dynamically
async function initDb() {
  try {
    pool = mysql.createPool(dbConfig);
    console.log(`📡 Attempting connection to MySQL at ${dbConfig.host}:${dbConfig.port}...`);
    
    // Check connection
    const connection = await pool.getConnection();
    console.log('✅ Connected successfully to MySQL database!');
    connection.release();

    // Dynamically bootstrap tables if they are missing
    await bootstrapTables();
  } catch (error) {
    console.error('❌ Failed to connect to MySQL database.');
    console.error('Error details:', error.message);
    console.log('💡 Note: Please ensure your local/cloud MySQL server is running and credentials in .env are correct.');
  }
}

async function bootstrapTables() {
  try {
    console.log('🔨 Bootstrapping database schema and checking tables...');
    
    // 1. User Profiles Table
    await pool.query(`
      CREATE TABLE IF NOT EXISTS user_profiles (
        email VARCHAR(255) PRIMARY KEY,
        name VARCHAR(255) NOT NULL,
        company VARCHAR(255),
        title VARCHAR(255),
        role VARCHAR(50) DEFAULT 'Delegate',
        qr_code_payload VARCHAR(500),
        bio TEXT,
        networking_prefs VARCHAR(255) DEFAULT 'Open to Network',
        phone VARCHAR(50),
        linkedin VARCHAR(255),
        twitter VARCHAR(255),
        github VARCHAR(255),
        card_theme VARCHAR(50) DEFAULT 'Gold Premium',
        is_registered BOOLEAN DEFAULT TRUE,
        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
      )
    `);

    // Ensure new columns exist on user_profiles if it was created in a prior session
    const addColumn = async (col, definition) => {
      try {
        await pool.query(`ALTER TABLE user_profiles ADD COLUMN ${col} ${definition}`);
        console.log(`✓ Added column ${col} to user_profiles`);
      } catch (e) {
        // Col probably already exists, which is expected on subsequent runs
      }
    };
    await addColumn('bio', 'TEXT');
    await addColumn('networking_prefs', "VARCHAR(255) DEFAULT 'Open to Network'");
    await addColumn('phone', 'VARCHAR(50)');
    await addColumn('linkedin', 'VARCHAR(255)');
    await addColumn('twitter', 'VARCHAR(255)');
    await addColumn('github', 'VARCHAR(255)');
    await addColumn('card_theme', "VARCHAR(50) DEFAULT 'Gold Premium'");

    // 2. Speakers Table
    await pool.query(`
      CREATE TABLE IF NOT EXISTS speakers (
        id VARCHAR(50) PRIMARY KEY,
        name VARCHAR(255) NOT NULL,
        title VARCHAR(255) NOT NULL,
        company VARCHAR(255) NOT NULL,
        bio TEXT,
        linkedin VARCHAR(255),
        avatar_url VARCHAR(500)
      )
    `);

    // 3. Sponsors Table
    await pool.query(`
      CREATE TABLE IF NOT EXISTS sponsors (
        id VARCHAR(50) PRIMARY KEY,
        name VARCHAR(255) NOT NULL,
        tier VARCHAR(50) NOT NULL,
        description TEXT,
        logo_url VARCHAR(500),
        website VARCHAR(255)
      )
    `);

    // 4. Event Sessions Table
    await pool.query(`
      CREATE TABLE IF NOT EXISTS event_sessions (
        id VARCHAR(50) PRIMARY KEY,
        title VARCHAR(255) NOT NULL,
        description TEXT,
        start_time VARCHAR(50) NOT NULL,
        end_time VARCHAR(50) NOT NULL,
        date VARCHAR(50) NOT NULL,
        speaker_id VARCHAR(50),
        speaker_name VARCHAR(255),
        location VARCHAR(255),
        category VARCHAR(100),
        FOREIGN KEY (speaker_id) REFERENCES speakers(id) ON DELETE SET NULL
      )
    `);

    // 5. Bookmarks Table
    await pool.query(`
      CREATE TABLE IF NOT EXISTS session_bookmarks (
        id INT AUTO_INCREMENT PRIMARY KEY,
        user_email VARCHAR(255) NOT NULL,
        session_id VARCHAR(50) NOT NULL,
        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        UNIQUE KEY unique_user_session_bookmark (user_email, session_id),
        FOREIGN KEY (user_email) REFERENCES user_profiles(email) ON DELETE CASCADE,
        FOREIGN KEY (session_id) REFERENCES event_sessions(id) ON DELETE CASCADE
      )
    `);

    // 6. Feedback Table
    await pool.query(`
      CREATE TABLE IF NOT EXISTS session_feedbacks (
        id INT AUTO_INCREMENT PRIMARY KEY,
        session_id VARCHAR(50) NOT NULL,
        user_email VARCHAR(255),
        rating FLOAT NOT NULL,
        comment TEXT,
        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        FOREIGN KEY (session_id) REFERENCES event_sessions(id) ON DELETE CASCADE
      )
    `);

    // 7. Attendees Table
    await pool.query(`
      CREATE TABLE IF NOT EXISTS attendees (
        id VARCHAR(50) PRIMARY KEY,
        name VARCHAR(255) NOT NULL,
        company VARCHAR(255),
        title VARCHAR(255),
        role VARCHAR(50) DEFAULT 'Delegate',
        connection_status VARCHAR(50) DEFAULT 'none',
        digital_card VARCHAR(500)
      )
    `);

    // 8. Announcements Table
    await pool.query(`
      CREATE TABLE IF NOT EXISTS announcements (
        id INT AUTO_INCREMENT PRIMARY KEY,
        title VARCHAR(255) NOT NULL,
        content TEXT NOT NULL,
        category VARCHAR(50) DEFAULT 'General',
        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
      )
    `);

    console.log('🚀 Database schema bootstrap complete!');
  } catch (error) {
    console.error('⚠️ Schema bootstrap failed:', error.message);
  }
}

// Ensure pool is ready before requests
const checkDb = (req, res, next) => {
  if (!pool) {
    return res.status(503).json({
      success: false,
      message: "Database connection pool is offline. Please check database logs and .env settings."
    });
  }
  next();
};

// ==========================================
// REST API ROUTES
// ==========================================

// Health Check API
app.get('/api/health', (req, res) => {
  res.json({
    status: "healthy",
    database: pool ? "connected" : "disconnected",
    timestamp: new Date()
  });
});

// 1. USER PROFILES ENDPOINTS

// Get User Profile by Email
app.get('/api/profile/:email', checkDb, async (req, res) => {
  const { email } = req.params;
  try {
    const [rows] = await pool.query('SELECT * FROM user_profiles WHERE email = ?', [email]);
    if (rows.length === 0) {
      return res.status(404).json({ success: false, message: 'Profile not found' });
    }
    res.json({ success: true, data: rows[0] });
  } catch (err) {
    res.status(500).json({ success: false, error: err.message });
  }
});

// Upsert User Profile
app.post('/api/profile', checkDb, async (req, res) => {
  const { 
    email, 
    name, 
    company, 
    title, 
    role, 
    qrCodePayload,
    bio,
    networkingPrefs,
    phone,
    linkedin,
    twitter,
    github,
    cardTheme
  } = req.body;

  if (!email || !name) {
    return res.status(400).json({ success: false, message: "Email and name are required." });
  }

  try {
    const [result] = await pool.query(
      `INSERT INTO user_profiles (
         email, name, company, title, role, qr_code_payload, 
         bio, networking_prefs, phone, linkedin, twitter, github, card_theme
       ) 
       VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) 
       ON DUPLICATE KEY UPDATE 
         name = VALUES(name), 
         company = VALUES(company), 
         title = VALUES(title), 
         role = VALUES(role),
         qr_code_payload = VALUES(qr_code_payload),
         bio = VALUES(bio),
         networking_prefs = VALUES(networking_prefs),
         phone = VALUES(phone),
         linkedin = VALUES(linkedin),
         twitter = VALUES(twitter),
         github = VALUES(github),
         card_theme = VALUES(card_theme)`,
      [
        email, 
        name, 
        company || null, 
        title || null, 
        role || 'Delegate', 
        qrCodePayload || null,
        bio || null,
        networkingPrefs || 'Open to Network',
        phone || null,
        linkedin || null,
        twitter || null,
        github || null,
        cardTheme || 'Gold Premium'
      ]
    );
    res.json({ success: true, message: "Profile successfully synchronized with MySQL server." });
  } catch (err) {
    res.status(500).json({ success: false, error: err.message });
  }
});


// 2. EVENT SESSIONS ENDPOINTS

// Get All Sessions
app.get('/api/sessions', checkDb, async (req, res) => {
  try {
    const [rows] = await pool.query('SELECT * FROM event_sessions');
    res.json({ success: true, data: rows });
  } catch (err) {
    res.status(500).json({ success: false, error: err.message });
  }
});


// 3. BOOKMARKS ENDPOINTS

// Sync and Toggle Bookmarked State in MySQL
app.post('/api/sessions/:id/bookmark', checkDb, async (req, res) => {
  const sessionId = req.params.id;
  const { email, bookmarked } = req.body; // Needs user email to track personalized bookmarks
  if (!email) {
    return res.status(400).json({ success: false, message: "User email is required to save bookmarks." });
  }

  try {
    if (bookmarked) {
      await pool.query(
        'INSERT IGNORE INTO session_bookmarks (user_email, session_id) VALUES (?, ?)',
        [email, sessionId]
      );
    } else {
      await pool.query(
        'DELETE FROM session_bookmarks WHERE user_email = ? AND session_id = ?',
        [email, sessionId]
      );
    }
    res.json({ success: true, bookmarked });
  } catch (err) {
    res.status(500).json({ success: false, error: err.message });
  }
});


// 4. FEEDBACK ENDPOINTS

// Submit Session Feedback
app.post('/api/sessions/:id/feedback', checkDb, async (req, res) => {
  const sessionId = req.params.id;
  const { email, rating, comment } = req.body;

  try {
    await pool.query(
      'INSERT INTO session_feedbacks (session_id, user_email, rating, comment) VALUES (?, ?, ?, ?)',
      [sessionId, email || null, rating, comment || null]
    );
    res.json({ success: true, message: "Feedback saved to MySQL server." });
  } catch (err) {
    res.status(500).json({ success: false, error: err.message });
  }
});


// 5. ANNOUNCEMENTS ENDPOINTS

// Fetch Latest Announcements
app.get('/api/announcements', checkDb, async (req, res) => {
  try {
    const [rows] = await pool.query('SELECT * FROM announcements ORDER BY created_at DESC');
    res.json({ success: true, data: rows });
  } catch (err) {
    res.status(500).json({ success: false, error: err.message });
  }
});

// Post a New Announcement (e.g. Broadcast Alerts)
app.post('/api/announcements', checkDb, async (req, res) => {
  const { title, content, category } = req.body;
  if (!title || !content) {
    return res.status(400).json({ success: false, message: "Title and content are required." });
  }

  try {
    await pool.query(
      'INSERT INTO announcements (title, content, category) VALUES (?, ?, ?)',
      [title, content, category || 'General']
    );
    res.status(201).json({ success: true, message: "Announcement broadcasted successfully to all connected users." });
  } catch (err) {
    res.status(500).json({ success: false, error: err.message });
  }
});

// Start express server
app.listen(PORT, () => {
  console.log(`=======================================================`);
  console.log(`🌐 DASA Connect Backend Server running on port ${PORT}`);
  console.log(`🔌 Local Emulator URL: http://10.0.2.2:${PORT}`);
  console.log(`=======================================================`);
  initDb();
});
