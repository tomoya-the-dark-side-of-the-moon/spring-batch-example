CREATE TABLE server (
	id INTEGER NOT NULL PRIMARY KEY,
	host VARCHAR(20) NOT NULL,
	user_name VARCHAR(20) NOT NULL,
	password VARCHAR(20) NOT NULL
);
INSERT INTO server VALUES (1, 'localhost', 'user1', 'password1');
INSERT INTO server VALUES (2, 'localhost', 'user2', 'password2');
INSERT INTO server VALUES (3, 'localhost', 'user3', 'password3');
