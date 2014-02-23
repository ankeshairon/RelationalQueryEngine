CREATE TABLE personInfo(id int, first_name string, last_name string, Gender string, age int, State string)
SELECT sum(age-id) as WeirdSum from personInfo