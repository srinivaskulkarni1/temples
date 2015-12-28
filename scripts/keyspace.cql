DROP KEYSPACE IF EXISTS temples;

CREATE KEYSPACE temples WITH replication = {'class':'SimpleStrategy', 'replication_factor':3};

USE temples;

CREATE TABLE temple(
god VARCHAR,
place varchar,
district varchar,
state varchar,
PRIMARY KEY ((god, place))
); 