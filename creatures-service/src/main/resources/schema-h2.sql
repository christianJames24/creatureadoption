DROP TABLE IF EXISTS creatures;

CREATE TABLE creatures (
                           id INT AUTO_INCREMENT PRIMARY KEY,
                           creature_id VARCHAR(36) NOT NULL UNIQUE,
                           registration_code VARCHAR(15) NOT NULL UNIQUE,
                           name VARCHAR(50) NOT NULL,
                           species VARCHAR(50) NOT NULL,
                           type VARCHAR(20) NOT NULL,
                           rarity VARCHAR(20) NOT NULL,
                           level INT NOT NULL,
                           age INT NOT NULL,
                           health INT NOT NULL,
                           experience INT NOT NULL,
                           status VARCHAR(20) NOT NULL,
                           strength INT NOT NULL,
                           intelligence INT NOT NULL,
                           agility INT NOT NULL,
                           temperament VARCHAR(20) NOT NULL
);