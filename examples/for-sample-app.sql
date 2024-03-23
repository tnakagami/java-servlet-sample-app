USE database;

-- For UserRole table
CREATE TABLE UserRole (
  id   INT         AUTO_INCREMENT,
  type VARCHAR(16) NOT NULL UNIQUE DEFAULT 'Viewer',
  PRIMARY KEY (id)
) ;

-- For User table
CREATE TABLE User (
  id   INT          AUTO_INCREMENT,
  name VARCHAR(255) NOT NULL CHECK (LENGTH(name) > 0),
  role INT,
  PRIMARY KEY (id),
  FOREIGN KEY (role) REFERENCES UserRole(id) ON UPDATE CASCADE ON DELETE SET NULL
) ;

-- Insert records of user's role
INSERT INTO UserRole(type) values ('Admin'), ('Editor'), (default) ;
