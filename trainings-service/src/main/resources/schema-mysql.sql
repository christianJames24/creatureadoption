DROP TABLE IF EXISTS trainings;

CREATE TABLE trainings (
                           id INT AUTO_INCREMENT PRIMARY KEY,
                           training_id VARCHAR(36) NOT NULL UNIQUE,
                           training_code VARCHAR(15) NOT NULL UNIQUE,
                           name VARCHAR(100) NOT NULL,
                           description TEXT NOT NULL,
                           difficulty VARCHAR(20) NOT NULL,
                           duration INT NOT NULL,
                           status VARCHAR(20) NOT NULL,
                           category VARCHAR(20) NOT NULL,
                           price DECIMAL(10, 2) NOT NULL,
                           location VARCHAR(100) NOT NULL
);