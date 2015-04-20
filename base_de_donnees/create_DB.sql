/*
create_DB.sql

Author : Simon Baehler
Date : 17.04.2015

Description : This file is used to create the database for the WarTanks project.
*/

# Create the table that contains maps
CREATE TABLE maps(
	id int(11) NOT NULL AUTO_INCREMENT,
	map_name varchar(255) NOT NULL UNIQUE,
	description text(255) NOT NULL,
	image varchar(255),
	size int(11),
	nb_player int(11),
	map_file varchar(255),
	PRIMARY KEY (id)
);

# Create the table that contains score
CREATE TABLE scores(
	id int(11) NOT NULL AUTO_INCREMENT,
	user_name varchar(255),
	score int(11),
	PRIMARY KEY (id)
);
