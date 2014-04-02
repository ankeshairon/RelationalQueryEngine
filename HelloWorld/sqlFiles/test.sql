
CREATE TABLE table1(ID int, NAME CHAR(5), G1 CHAR(5), G2 CHAR(5), G3 CHAR(5), G4 CHAR(5))
CREATE TABLE table2(ID int, NAME CHAR(5), marks int)

SELECT table1.NAME FROM table1, table2
WHERE
 ((G1='a'  AND G2='b') OR ( G3='c' AND G4='d'))
 AND G1='e'