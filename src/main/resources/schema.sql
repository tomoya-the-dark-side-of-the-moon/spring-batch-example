CREATE TABLE server (
	id INTEGER NOT NULL PRIMARY KEY,
	host VARCHAR(20) NOT NULL,
	user_name VARCHAR(20) NOT NULL,
	db_name VARCHAR(20) NOT NULL,
	password VARCHAR(20) NOT NULL
);
INSERT INTO server VALUES (1, 'localhost', 'user1', 'db1', 'password1');
INSERT INTO server VALUES (2, 'localhost', 'user2', 'db2', 'password2');
INSERT INTO server VALUES (3, 'localhost', 'user3', 'db3', 'password3');
CREATE TABLE reservation (
	id INTEGER NOT NULL,
	file_name VARCHAR(40) NOT NULL,
	PRIMARY KEY (id, file_name),
	CONSTRAINT fk_id FOREIGN KEY (id) REFERENCES server(id)
);
INSERT INTO reservation VALUES (1, 'bon_jovi.csv');
INSERT INTO reservation VALUES (2, 'metallica.csv');
INSERT INTO reservation VALUES (3, 'iron_maiden.csv');
