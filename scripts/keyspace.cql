DROP KEYSPACE IF EXISTS temples;

CREATE KEYSPACE temples WITH replication = {'class':'SimpleStrategy', 'replication_factor':3};

USE temples;

CREATE TABLE temple(
id VARCHAR,
god VARCHAR,
place VARCHAR,
district VARCHAR,
state VARCHAR,
PRIMARY KEY ((id))
); 