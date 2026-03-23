-- =========================================================
-- Table USER
-- =========================================================
CREATE TABLE user (
    userId INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100),
    email VARCHAR(150) UNIQUE NOT NULL,
    password VARCHAR(100) NOT NULL,
    location VARCHAR(100),
    bio TEXT,
    points INT DEFAULT 0,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- =========================================================
-- Table USER_AUTHENTICATED (hérite de USER)
-- =========================================================
CREATE TABLE user_authenticated (
    user_authenticated_id INT PRIMARY KEY,
    last_login TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_authenticated_id) REFERENCES user(userId) ON DELETE CASCADE
);

-- =========================================================
-- Table ADMIN
-- =========================================================
CREATE TABLE admin (
    adminId INT AUTO_INCREMENT PRIMARY KEY,
    userId INT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (userId) REFERENCES user(userId) ON DELETE CASCADE
);

-- =========================================================
-- Tables de référence : STATUS, CATEGORIES, TYPE, NOTE
-- =========================================================
CREATE TABLE status (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(50) UNIQUE NOT NULL,
    libelle VARCHAR(100) NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by INT,
    updated_by INT,
    FOREIGN KEY (created_by) REFERENCES user(userId) ON DELETE SET NULL,
    FOREIGN KEY (updated_by) REFERENCES user(userId) ON DELETE SET NULL
);

CREATE TABLE categories (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(50) UNIQUE NOT NULL,
    libelle VARCHAR(100) NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by INT,
    updated_by INT,
    FOREIGN KEY (created_by) REFERENCES user(userId) ON DELETE SET NULL,
    FOREIGN KEY (updated_by) REFERENCES user(userId) ON DELETE SET NULL
);

CREATE TABLE type (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(50) UNIQUE NOT NULL,
    libelle VARCHAR(100) NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by INT,
    updated_by INT,
    FOREIGN KEY (created_by) REFERENCES user(userId) ON DELETE SET NULL,
    FOREIGN KEY (updated_by) REFERENCES user(userId) ON DELETE SET NULL
);

CREATE TABLE note (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(50) UNIQUE NOT NULL,
    libelle VARCHAR(100) NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by INT,
    updated_by INT,
    FOREIGN KEY (created_by) REFERENCES user(userId) ON DELETE SET NULL,
    FOREIGN KEY (updated_by) REFERENCES user(userId) ON DELETE SET NULL
);

-- =========================================================
-- Table PUBLICATION
-- =========================================================
CREATE TABLE publication (
    publicationId INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(150) NOT NULL,
    description TEXT,
    location VARCHAR(100),
    dateCreated DATETIME DEFAULT CURRENT_TIMESTAMP,
    createdBy INT,
    status_id BIGINT,
    category_id BIGINT,
    type_id BIGINT,
    note_id BIGINT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    updated_by INT,
    FOREIGN KEY (createdBy) REFERENCES user(userId) ON DELETE CASCADE,
    FOREIGN KEY (updated_by) REFERENCES user(userId) ON DELETE SET NULL,
    FOREIGN KEY (status_id) REFERENCES status(id),
    FOREIGN KEY (category_id) REFERENCES categories(id),
    FOREIGN KEY (type_id) REFERENCES type(id),
    FOREIGN KEY (note_id) REFERENCES note(id)
);

-- =========================================================
-- Table MESSAGE
-- =========================================================
CREATE TABLE message (
    messageId INT AUTO_INCREMENT PRIMARY KEY,
    content TEXT NOT NULL,
    dateSent DATETIME DEFAULT CURRENT_TIMESTAMP,
    senderId INT,
    receiverId INT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    updated_by INT,
    FOREIGN KEY (senderId) REFERENCES user(userId) ON DELETE CASCADE,
    FOREIGN KEY (receiverId) REFERENCES user(userId) ON DELETE CASCADE,
    FOREIGN KEY (updated_by) REFERENCES user(userId) ON DELETE SET NULL
);

-- =========================================================
-- Table NOTIFICATION
-- =========================================================
CREATE TABLE notification (
    notificationId INT AUTO_INCREMENT PRIMARY KEY,
    content TEXT NOT NULL,
    date DATETIME DEFAULT CURRENT_TIMESTAMP,
    isRead BOOLEAN DEFAULT FALSE,
    userId INT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    updated_by INT,
    FOREIGN KEY (userId) REFERENCES user(userId) ON DELETE CASCADE,
    FOREIGN KEY (updated_by) REFERENCES user(userId) ON DELETE SET NULL
);
