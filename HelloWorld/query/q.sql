CREATE TABLE table1(
sl int,
name string,
avg float
)
CREATE TABLE table2(
sl int,
name string,
avg float
)
CREATE TABLE table3(
sl int,
name string,
avg float
)
SELECT * FROM table1,table2,table3 WHERE avg=22.5;