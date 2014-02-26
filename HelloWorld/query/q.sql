CREATE TABLE table1(
sl1 int,
name1 string,
avg1 float
)
CREATE TABLE table2(
sl2 int,
name2 string,
avg2 float
)
CREATE TABLE table3(
sl3 int,
name3 string,
avg3 float
)
SELECT * FROM table1,table2,table3 WHERE name1='Ramesh';