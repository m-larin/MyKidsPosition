CREATE USER 'mkp'@'localhost' IDENTIFIED BY 'mkp';

CREATE database mkp;

GRANT ALL PRIVILEGES ON mkp.* TO 'mkp'@'localhost';

create table person_group (
id int NOT NULL AUTO_INCREMENT
PRIMARY KEY (id)
);

create table person (
id int NOT NULL AUTO_INCREMENT,
name varchar(255) not null,
group_id int not null,
PRIMARY KEY (id),
FOREIGN KEY (group_id) REFERENCES person_group(id)
);

create table position (
id int NOT NULL AUTO_INCREMENT,
date datetime not null,
accuracy int not null,
lat FLOAT not null,
lon FLOAT not null,
battery_level int NOT NULL,
person_id int not null,
PRIMARY KEY (id),
FOREIGN KEY (person_id) REFERENCES person(id)
);

create table group_request (
id int NOT NULL AUTO_INCREMENT,
date datetime not null,
request int NOT NULL,
person_id int not null,
PRIMARY KEY (id),
FOREIGN KEY (person_id) REFERENCES person(id)
);
