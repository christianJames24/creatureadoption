DROP TABLE IF EXISTS customer_phonenumbers;
DROP TABLE IF EXISTS customers;

CREATE TABLE customers (
                           id INT AUTO_INCREMENT PRIMARY KEY,
                           customer_id VARCHAR(36) NOT NULL UNIQUE,
                           first_name VARCHAR(100) NOT NULL,
                           last_name VARCHAR(100) NOT NULL,
                           email_address VARCHAR(255) NOT NULL,
                           contact_method_preference VARCHAR(10) NOT NULL,
                           street_address VARCHAR(255) NOT NULL,
                           city VARCHAR(100) NOT NULL,
                           province VARCHAR(100) NOT NULL,
                           country VARCHAR(100) NOT NULL,
                           postal_code VARCHAR(20) NOT NULL
);

CREATE TABLE customer_phonenumbers (
                                       id INT AUTO_INCREMENT PRIMARY KEY,
                                       customer_id INT NOT NULL,
                                       type VARCHAR(20) NOT NULL,
                                       number VARCHAR(20) NOT NULL,
                                       FOREIGN KEY (customer_id) REFERENCES customers(id)
);