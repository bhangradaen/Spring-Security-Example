DROP SEQUENCE hibernate_sequence;
CREATE SEQUENCE hibernate_sequence;

DROP TABLE functions;
DROP TABLE roles;
DROP TABLE role_function;
DROP TABLE users;
DROP TABLE user_role;

CREATE TABLE functions (
  function_id NUMBER (10, 0) NOT NULL AUTO_INCREMENT,
  name VARCHAR2(255) DEFAULT NULL,
    PRIMARY KEY(function_id));

CREATE TABLE roles (
  role_id NUMBER (10, 0) NOT NULL AUTO_INCREMENT,
  name VARCHAR2(255) DEFAULT NULL,
    PRIMARY KEY(role_id));

CREATE TABLE role_function (
  role_id NUMBER (10, 0) NOT NULL,
  function_id NUMBER (10, 0) NOT NULL,
    PRIMARY KEY(role_id, function_id),
      FOREIGN KEY (role_id) REFERENCES roles (role_id),
      FOREIGN KEY (function_id) REFERENCES functions (function_id));

CREATE TABLE users (
  user_id NUMBER(10,0) NOT NULL AUTO_INCREMENT,
  username VARCHAR2(20) NOT NULL,
  hashed_password VARCHAR2(60) NOT NULL,
  first_name VARCHAR2(30) DEFAULT NULL,
  last_name VARCHAR2(30) DEFAULT NULL,
  email VARCHAR2(50) NOT NULL,
  active BOOLEAN NOT NULL,
    PRIMARY KEY (user_id));

CREATE TABLE user_role (
  user_id NUMBER (10, 0) NOT NULL,
  role_id NUMBER (10, 0) NOT NULL,
    PRIMARY KEY(user_id, role_id),
      FOREIGN KEY (user_id) REFERENCES users (user_id),
      FOREIGN KEY (role_id) REFERENCES roles (role_id));

INSERT INTO functions VALUES (1, 'CREATE');
INSERT INTO functions VALUES (2, 'READ');
INSERT INTO functions VALUES (3, 'UPDATE');
INSERT INTO functions VALUES (4, 'DELETE');

INSERT INTO roles VALUES (1, 'Base Member');
INSERT INTO roles VALUES (2, 'Content Creator');
INSERT INTO roles VALUES (3, 'Admin');

INSERT INTO role_function VALUES (1, 2);
INSERT INTO role_function VALUES (2, 1);
INSERT INTO role_function VALUES (2, 2);
INSERT INTO role_function VALUES (2, 3);
INSERT INTO role_function VALUES (3, 1);
INSERT INTO role_function VALUES (3, 2);
INSERT INTO role_function VALUES (3, 3);
INSERT INTO role_function VALUES (3, 4);

INSERT INTO users VALUES (1, 'joe_5446', '$2a$04$nrDL4h/fKLbyWXHp2Hh87.S3S5rwOjuKAP5RsXSNf1i/fRyfPiACW', 'Joe', 'Schmo', 'joeschmo@gmail.com', true); // password is passwordone
INSERT INTO users VALUES (2, 'bob_in_water', '$2a$04$fiPxrVfM7iEFK1AhoJTQEu9leWB2Ok5JS8Yt/e2wqtaW35UwPICsK', 'Bob', 'Jameson', 'bobjameson@gmail.com', true); // password is passwordtwo

INSERT INTO user_role VALUES (1, 2);
INSERT INTO user_role VALUES (2, 1);