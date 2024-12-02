-- Create household table
CREATE TABLE household (
                           eircode VARCHAR(8) PRIMARY KEY,
                           number_of_occupants INT NOT NULL,
                           max_number_of_occupants INT NOT NULL,
                           owner_occupied BIT NOT NULL
);

-- Modify pets table to include household_eircode as a foreign key
CREATE TABLE pets (
                      id BIGINT AUTO_INCREMENT PRIMARY KEY,
                      name VARCHAR(255) NOT NULL,
                      animal_type VARCHAR(255) NOT NULL,
                      breed VARCHAR(255) NOT NULL,
                      age INT NOT NULL,
                      household_eircode VARCHAR(8) NOT NULL,
                      CONSTRAINT fk_household FOREIGN KEY (household_eircode) REFERENCES household(eircode)
);

DROP TABLE IF EXISTS app_user;

CREATE TABLE app_user (
                          username VARCHAR(50) PRIMARY KEY,
                          password VARCHAR(100) NOT NULL,
                          role VARCHAR(20) NOT NULL,
                          unlocked BOOLEAN NOT NULL
);

