CREATE TABLE contact (
	id       	integer NOT NULL,
	firstName	varchar(25) NULL,
	lastName	varchar(25) NULL,
	phone	varchar(25) NULL,
	email	varchar(25) NULL
)

/

ALTER TABLE contact ADD CONSTRAINT pk_contact PRIMARY KEY (id)

/

CREATE SEQUENCE ids INCREMENT 1 START 1

