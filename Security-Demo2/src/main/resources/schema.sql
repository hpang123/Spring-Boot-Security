DROP TABLE todo IF EXISTS;

CREATE TABLE todo (
  id bigint AUTO_INCREMENT PRIMARY KEY ,
  owner VARCHAR(255) NOT NULL,
  description VARCHAR(255) NOT NULL,
  completed BOOLEAN NOT NULL DEFAULT false
);

DROP TABLE USERS IF EXISTS;
CREATE TABLE USERS (
    USERNAME    VARCHAR(50)    NOT NULL,
    PASSWORD    VARCHAR(60)    NOT NULL,
    ENABLED     SMALLINT,
    PRIMARY KEY (USERNAME)
);

DROP TABLE AUTHORITIES IF EXISTS;
CREATE TABLE AUTHORITIES (
    USERNAME    VARCHAR(50)    NOT NULL,
    AUTHORITY   VARCHAR(50)    NOT NULL,
    FOREIGN KEY (USERNAME) REFERENCES USERS
);