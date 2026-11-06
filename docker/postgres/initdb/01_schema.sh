#!/bin/sh
psql db1 -c """
CREATE TABLE member (
	id INTEGER NOT NULL PRIMARY KEY,
	first_name VARCHAR(20) NOT NULL,
	last_name VARCHAR(20) NOT NULL,
	full_name VARCHAR(40) NOT NULL
);
ALTER TABLE member OWNER TO user1;
"""
psql db2 -c """
CREATE TABLE member (
	id INTEGER NOT NULL PRIMARY KEY,
	first_name VARCHAR(20) NOT NULL,
	last_name VARCHAR(20) NOT NULL,
	full_name VARCHAR(40) NOT NULL
);
ALTER TABLE member OWNER TO user2;
"""
psql db3 -c """
CREATE TABLE member (
	id INTEGER NOT NULL PRIMARY KEY,
	first_name VARCHAR(20) NOT NULL,
	last_name VARCHAR(20) NOT NULL,
	full_name VARCHAR(40) NOT NULL
);
ALTER TABLE member OWNER TO user3;
"""
