-- ==========================================
-- DIGITAL ASSETS SUMMIT AFRICA (DASA) 2026
-- MySQL Database Initialization Schema
-- ==========================================

CREATE DATABASE IF NOT EXISTS dasa_connect_db;
USE dasa_connect_db;

-- 1. USER PROFILES Table
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
);

-- 2. SPEAKERS Table
CREATE TABLE IF NOT EXISTS speakers (
    id VARCHAR(50) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    title VARCHAR(255) NOT NULL,
    company VARCHAR(255) NOT NULL,
    bio TEXT,
    linkedin VARCHAR(255),
    avatar_url VARCHAR(500)
);

-- 3. SPONSORS Table
CREATE TABLE IF NOT EXISTS sponsors (
    id VARCHAR(50) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    tier VARCHAR(50) NOT NULL, -- Platinum, Gold, Silver
    description TEXT,
    logo_url VARCHAR(500),
    website VARCHAR(255)
);

-- 4. EVENT SESSIONS Table
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
);

-- 5. SESSION BOOKMARKS Table (tracks personalized bookmarks per user email)
CREATE TABLE IF NOT EXISTS session_bookmarks (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_email VARCHAR(255) NOT NULL,
    session_id VARCHAR(50) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY unique_user_session_bookmark (user_email, session_id),
    FOREIGN KEY (user_email) REFERENCES user_profiles(email) ON DELETE CASCADE,
    FOREIGN KEY (session_id) REFERENCES event_sessions(id) ON DELETE CASCADE
);

-- 6. FEEDBACKS Table
CREATE TABLE IF NOT EXISTS session_feedbacks (
    id INT AUTO_INCREMENT PRIMARY KEY,
    session_id VARCHAR(50) NOT NULL,
    user_email VARCHAR(255),
    rating FLOAT NOT NULL,
    comment TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (session_id) REFERENCES event_sessions(id) ON DELETE CASCADE
);

-- 7. ATTENDEES (NETWORKING) Table
CREATE TABLE IF NOT EXISTS attendees (
    id VARCHAR(50) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    company VARCHAR(255),
    title VARCHAR(255),
    role VARCHAR(50) DEFAULT 'Delegate',
    connection_status VARCHAR(50) DEFAULT 'none', -- none, pending_sent, pending_received, connected
    digital_card VARCHAR(500)
);

-- 8. ANNOUNCEMENTS Table
CREATE TABLE IF NOT EXISTS announcements (
    id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    content TEXT NOT NULL,
    category VARCHAR(50) DEFAULT 'General', -- General, Schedule, Alert
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);


-- ==========================================
-- SEED DATA INSERTIONS (Matches App State)
-- ==========================================

-- Seed Speakers
INSERT INTO speakers (id, name, title, company, bio, linkedin, avatar_url) VALUES
('spk1', 'Dr. Elikem Adonoo', 'Director of Financial Technology', 'Bank of Ghana', 'Dr. Elikem is a leading pioneer in CBDC frameworks across Sub-Saharan Africa. He leads the eCedi project and regulates retail payment frameworks.', 'linkedin.com/in/elikem-adonoo', 'https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?auto=format&fit=crop&q=80&w=200'),
('spk2', 'Fatoumata Diallo', 'Founder & CEO', 'Nile Digital Capital', 'Fatoumata manages a $50M venture fund backing infrastructure startups in East and West Africa. She holds degrees from INSEAD.', 'linkedin.com/in/fatoumata-diallo', 'https://images.unsplash.com/photo-1573496359142-b8d87734a5a2?auto=format&fit=crop&q=80&w=200'),
('spk3', 'Chidi Okechukwu', 'Chief Policy Strategist', 'Digital Assets Africa Initiative', 'Chidi advises ministries of finance and tech regulators on crypto asset taxonomy, anti-money laundering, and cross-border settlement architectures.', 'linkedin.com/in/chidi-okechukwu', 'https://images.unsplash.com/photo-1500648767791-00dcc994a43e?auto=format&fit=crop&q=80&w=200'),
('spk4', 'Amara Mensah', 'Head of Blockchain Strategy', 'Standard Capital Group', 'Amara has 12 years of investment banking experience and currently oversees asset tokenization pilots on private Ethereum networks.', 'linkedin.com/in/amara-mensah', 'https://images.unsplash.com/photo-1580489944761-15a19d654956?auto=format&fit=crop&q=80&w=200')
ON DUPLICATE KEY UPDATE name=VALUES(name), title=VALUES(title), company=VALUES(company);

-- Seed Sponsors
INSERT INTO sponsors (id, name, tier, description, logo_url, website) VALUES
('spon1', 'Apex Clearing Bank', 'Platinum', 'Apex Clearing Bank is a tier-1 banking institution driving wholesale liquid digital clearing across the African continent.', 'https://images.unsplash.com/photo-1559526324-4b87b5e36e44?auto=format&fit=crop&q=80&w=200', 'https://apexclearing.com'),
('spon2', 'Vanguard Digital Ledger', 'Platinum', 'Providing institutional-grade custody systems, nodes management, and compliant asset fractionalization architectures.', 'https://images.unsplash.com/photo-1486406146926-c627a92ad1ab?auto=format&fit=crop&q=80&w=200', 'https://vanguardledger.io'),
('spon3', 'ePay Network', 'Gold', 'ePay Network connects instant mobile money schemes with global stablecoin settlement layers seamlessly.', 'https://images.unsplash.com/photo-1563013544-824ae1d704d3?auto=format&fit=crop&q=80&w=200', 'https://epaynetwork.org'),
('spon4', 'TrustTech Custody', 'Silver', 'Cold storage and enterprise multisig wallet integration for commercial entities.', 'https://images.unsplash.com/photo-1454165804606-c3d57bc86b40?auto=format&fit=crop&q=80&w=200', 'https://trusttech.io')
ON DUPLICATE KEY UPDATE name=VALUES(name), tier=VALUES(tier), description=VALUES(description);

-- Seed Event Sessions
INSERT INTO event_sessions (id, title, description, start_time, end_time, date, speaker_id, speaker_name, location, category) VALUES
('ses1', 'Opening Plenary: Africa\'s Role in the Global Web3 Ecosystem', 'An executive opening panel looking at standardizing cross-border digital payments, digital dollarization risks, and sub-saharan growth trajectories.', '09:00 AM', '10:30 AM', 'Day 1 - Oct 12', 'spk2', 'Fatoumata Diallo', 'Plenary Hall (Level 3)', 'Policy'),
('ses2', 'Central Bank Digital Currencies: CBDC Pilots & Practical Lessons', 'A deep dive into the technology choices (e.g. Hyperledger vs Corda) of African central banks. Real operational data from eCedi and eNaira implementations.', '11:00 AM', '12:30 PM', 'Day 1 - Oct 12', 'spk1', 'Dr. Elikem Adonoo', 'Executive Boardroom A', 'CBDC'),
('ses3', 'Asset Tokenization & The Future of African Debt Markets', 'Analyzing how fractionalizing bonds and treasury bills onto public ledgers increases domestic retail access and global institutional liquidity pool inputs.', '02:00 PM', '03:30 PM', 'Day 1 - Oct 12', 'spk4', 'Amara Mensah', 'Exhibition Theater', 'DeFi'),
('ses4', 'Fireside Chat: Regulating Crypto Assets without Stifling Innovation', 'A standard policy panel balancing consumer protection compliance with sandboxing strategies for high-frequency trading platforms and wallet providers.', '04:00 PM', '05:00 PM', 'Day 1 - Oct 12', 'spk3', 'Chidi Okechukwu', 'Plenary Hall (Level 3)', 'Policy'),
('ses5', 'Day 2 Keynote: Cross-Border Liquidity and Stablecoin Rails', 'Analyzing the friction point of traditional correspondent banking and presenting the active scaling of stablecoin networks in liquidity-scarce hubs.', '09:30 AM', '10:45 AM', 'Day 2 - Oct 13', 'spk2', 'Fatoumata Diallo', 'Plenary Hall (Level 3)', 'DeFi'),
('ses6', 'Technical Deep Dive: Multi-Sig Safes and Cold Storage Architectures', 'A highly developer-oriented session on protecting institutional digital treasury assets from hacking vectors and physical compromise protocols.', '11:15 AM', '12:30 PM', 'Day 2 - Oct 13', 'spk4', 'Amara Mensah', 'Tech Lab (Room 202)', 'Web3')
ON DUPLICATE KEY UPDATE title=VALUES(title), description=VALUES(description);

-- Seed Attendees
INSERT INTO attendees (id, name, company, title, role, connection_status, digital_card) VALUES
('att1', 'Kofi Boateng', 'Ghana Web3 Hub', 'Managing Director', 'Delegate', 'none', 'Email: kofi@ghanaweb3.org | Telegram: @kofiboat'),
('att2', 'Nneka Ndlovu', 'Nile Digital Capital', 'Senior Associate', 'Sponsor', 'pending_received', 'Email: nneka@niledigital.cap'),
('att3', 'Tariq Yusuf', 'Sovereign Ledger Inc.', 'Lead Security Architect', 'Delegate', 'connected', 'Email: tariq@sovereignledger.io | GitHub: tyusuf'),
('att4', 'Zola Mtetwa', 'Fintech Association of SA', 'Executive Director', 'Delegate', 'none', 'Email: zola@fintechassociation.za')
ON DUPLICATE KEY UPDATE name=VALUES(name), company=VALUES(company), title=VALUES(title);

-- Seed Announcements
INSERT INTO announcements (id, title, content, category) VALUES
(1, 'Welcome to DASA Connect!', 'Welcome to the Digital Assets Summit Africa (DASA) companion application. Enjoy high-speed networking, live session bookmarks, and your personalized digital QR badge.', 'General'),
(2, 'Opening Plenary Starts Soon', 'Please make your way to the Plenary Hall (Level 3). The opening panel on Africa\'s Web3 Ecosystem will start in 10 minutes.', 'Schedule')
ON DUPLICATE KEY UPDATE title=VALUES(title), content=VALUES(content);
