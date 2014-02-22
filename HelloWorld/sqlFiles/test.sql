CREATE TABLE personInfo(id int, first_name string, last_name string, Gender string, age int, State string)
SELECT first_name, COUNT(id), SUM(age), last_name from personInfo