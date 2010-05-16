-- HSQL DATABASE

-- Dropping Tables

DROP TABLE ACCOUNT;
DROP TABLE ADDRESS;

-- Creating Tables

CREATE TABLE ACCOUNT (
    ACC_ID             INTEGER NOT NULL,
    ACC_FIRST_NAME     VARCHAR(255) NOT NULL,
    ACC_LAST_NAME      VARCHAR(255) NOT NULL,
    ACC_EMAIL          VARCHAR(255),
    PRIMARY KEY (ACC_ID)
);

CREATE TABLE ADDRESS (
    ADR_ID             INTEGER NOT NULL,
    ADR_ACC_ID         INTEGER NOT NULL,
    ADR_DESCRIPTION    VARCHAR(255) NOT NULL,
    ADR_STREET         VARCHAR(255) NOT NULL,
    ADR_CITY           VARCHAR(255) NOT NULL,
    ADR_PROVINCE       VARCHAR(255) NOT NULL,
    ADR_POSTAL_CODE    VARCHAR(255) NOT NULL,
    PRIMARY KEY (ADR_ID)
);

-- Creating Test Data

INSERT INTO ACCOUNT VALUES(1,'Clinton', 'Begin', 'clinton.begin@ibatis.com');
INSERT INTO ACCOUNT VALUES(2,'Jim', 'Smith', 'jim.smith@somewhere.com');
INSERT INTO ACCOUNT VALUES(3,'Elizabeth', 'Jones', null);
INSERT INTO ACCOUNT VALUES(4,'Bob', 'Jackson', 'bob.jackson@somewhere.com');
INSERT INTO ACCOUNT VALUES(5,'Amanda', 'Goodman', null);

INSERT INTO ADDRESS VALUES(1, 1, 'Fake', '434 Edward St.', 'Edmonton', 'Alberta', 'L5G 2P9');
INSERT INTO ADDRESS VALUES(2, 2, 'Fake', '652 John St.', 'Vancouver', 'British Columbia', 'B42 4W2');
INSERT INTO ADDRESS VALUES(3, 3, 'Fake', '863 William St.', 'Regina', 'Saskatchewan', 'J9K 4L6');
INSERT INTO ADDRESS VALUES(4, 4, 'Fake', '237 Arthur St.', 'Winnipeg', 'Manitoba', 'P4G 2D3');
INSERT INTO ADDRESS VALUES(5, 5, 'Fake', '989 Memorial St.', 'Toronto', 'Ontario', 'Q5J 4F4');

