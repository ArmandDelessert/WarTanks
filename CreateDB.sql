CREATE TABLE maps(
	id int(11) NOT NULL AUTO_INCREMENT,
	map_name varchar (255) NOT NULL UNIQUE,
	description TEXT (255) NOT NULL,
	image varchar(255),
	size int(11),
	nb_player int(11),
	map_file varchar(255),
	PRIMARY KEY (id)
);
CREATE TABLE scores(
	id int(11) NOT NULL AUTO_INCREMENT,
	user_name varchar(255),
	score int(11),
	PRIMARY KEY (id)
);