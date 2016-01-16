DROP KEYSPACE IF EXISTS temples;

CREATE KEYSPACE temples WITH replication = {'class':'SimpleStrategy', 'replication_factor':3};

USE temples;

CREATE TABLE temple(
id VARCHAR,
entity BLOB,
PRIMARY KEY ((id))
); 

INSERT INTO temple(ID, ENTITY ) 
VALUES (
'1',
textAsBlob('{"type": "temple","id": "1","district": "Udupi","god": "Shri Krishna","place": "Udupi","state": "Karnataka"}')
);

INSERT INTO temple(ID, ENTITY ) 
VALUES (
'2',
textAsBlob('{"type": "temple","id": "1","district": "Ahmednagar","god": "Sai Baba","place": "Shirdi","state": "Maharashtra"}')
);