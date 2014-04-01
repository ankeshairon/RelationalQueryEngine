CREATE TABLE PLAYERS(ID string, FIRSTNAME string, LASTNAME string, FIRSTSEASON int, LASTSEASON int, WEIGHT int, BIRTHDATE date)

SELECT FIRSTNAME, LASTNAME, WEIGHT, BIRTHDATE FROM PLAYERS WHERE WEIGHT>200

CREATE TABLE PLAYERS(ID string, FIRSTNAME string, LASTNAME string, FIRSTSEASON int, LASTSEASON int, WEIGHT int, BIRTHDATE date)

SELECT FIRSTSEASON, COUNT(ID) FROM PLAYERS GROUP BY FIRSTSEASON

CREATE TABLE PLAYERS(ID string, FIRSTNAME string, LASTNAME string, FIRSTSEASON int, LASTSEASON int, WEIGHT int, BIRTHDATE date)

SELECT FIRSTNAME, LASTNAME, FIRSTSEASON, LASTSEASON FROM PLAYERS WHERE LASTSEASON-FIRSTSEASON>5

CREATE TABLE PLAYERS(ID string, FIRSTNAME string, LASTNAME string, FIRSTSEASON int, LASTSEASON int, WEIGHT int, BIRTHDATE date)

SELECT EXPERIENCE, COUNT(ID) FROM (SELECT ID, FIRSTNAME, LASTNAME, (LASTSEASON-FIRSTSEASON) AS EXPERIENCE FROM PLAYERS) GROUP BY EXPERIENCE

CREATE TABLE LINEITEM (
        orderkey       INT,
        partkey        INT,
        suppkey        INT,
        linenumber     INT,
        quantity       DECIMAL,
        extendedprice  DECIMAL,
        discount       DECIMAL,
        tax            DECIMAL,
        returnflag     CHAR(1),
        linestatus     CHAR(1),
        shipdate       DATE,
        commitdate     DATE,
        receiptdate    DATE,
        shipinstruct   CHAR(25),
        shipmode       CHAR(10),
        comment        VARCHAR(44)
    );

SELECT
  returnflag,
  linestatus,
  sum(quantity) as sum_qty,
  sum(extendedprice) as sum_base_price, sum(extendedprice*(1-discount)) as sum_disc_price,
  sum(extendedprice*(1-discount)*(1+tax)) as sum_charge, avg(quantity) as avg_qty,
  avg(extendedprice) as avg_price,
  avg(discount) as avg_disc,
  count(*) as count_order
FROM
  lineitem
WHERE
  shipdate <= DATE('1998-09-01') -- (- interval '[DELTA]' day (3))
GROUP BY
  returnflag, linestatus
ORDER BY
  returnflag, linestatus;

CREATE TABLE LINEITEM (
        orderkey       INT,
        partkey        INT,
        suppkey        INT,
        linenumber     INT,
        quantity       DECIMAL,
        extendedprice  DECIMAL,
        discount       DECIMAL,
        tax            DECIMAL,
        returnflag     CHAR(1),
        linestatus     CHAR(1),
        shipdate       DATE,
        commitdate     DATE,
        receiptdate    DATE,
        shipinstruct   CHAR(25),
        shipmode       CHAR(10),
        comment        VARCHAR(44)
    );

CREATE TABLE ORDERS (
        orderkey       INT,
        custkey        INT,
        orderstatus    CHAR(1),
        totalprice     DECIMAL,
        orderdate      DATE,
        orderpriority  CHAR(15),
        clerk          CHAR(15),
        shippriority   INT,
        comment        VARCHAR(79)
    );

CREATE TABLE CUSTOMER (
        custkey      INT,
        name         VARCHAR(25),
        address      VARCHAR(40),
        nationkey    INT,
        phone        CHAR(15),
        acctbal      DECIMAL,
        mktsegment   CHAR(10),
        comment      VARCHAR(117)
    );

SELECT
  lineitem.orderkey,
  sum(lineitem.extendedprice*(1-lineitem.discount)) as revenue,
  orders.orderdate,
  orders.shippriority
FROM
  customer,
  orders,
  lineitem
WHERE
  customer.mktsegment = 'BUILDING' and customer.custkey = orders.custkey
  and lineitem.orderkey = orders.orderkey
  and orders.orderdate < DATE('1995-03-15')
  and lineitem.shipdate > DATE('1995-03-15')
GROUP BY lineitem.orderkey, orders.orderdate, orders.shippriority
ORDER BY revenue desc, orders.orderdate;

CREATE TABLE LINEITEM (
        orderkey       INT,
        partkey        INT,
        suppkey        INT,
        linenumber     INT,
        quantity       DECIMAL,
        extendedprice  DECIMAL,
        discount       DECIMAL,
        tax            DECIMAL,
        returnflag     CHAR(1),
        linestatus     CHAR(1),
        shipdate       DATE,
        commitdate     DATE,
        receiptdate    DATE,
        shipinstruct   CHAR(25),
        shipmode       CHAR(10),
        comment        VARCHAR(44)
    );

CREATE TABLE ORDERS (
        orderkey       INT,
        custkey        INT,
        orderstatus    CHAR(1),
        totalprice     DECIMAL,
        orderdate      DATE,
        orderpriority  CHAR(15),
        clerk          CHAR(15),
        shippriority   INT,
        comment        VARCHAR(79)
    );

CREATE TABLE CUSTOMER (
        custkey      INT,
        name         VARCHAR(25),
        address      VARCHAR(40),
        nationkey    INT,
        phone        CHAR(15),
        acctbal      DECIMAL,
        mktsegment   CHAR(10),
        comment      VARCHAR(117)
    );

CREATE TABLE NATION (
        nationkey    INT,
        name         CHAR(25),
        regionkey    INT,
        comment      VARCHAR(152)
    );

SELECT
  nation.name,
  sum(lineitem.extendedprice * (1 - lineitem.discount)) AS revenue
FROM
  nation, customer, orders, lineitem
WHERE
  customer.custkey = orders.custkey
  and lineitem.orderkey = orders.orderkey
  and customer.nationkey = nation.nationkey
  and orders.orderdate >= DATE( '1994-01-01')
  and orders.orderdate < DATE ('1995-01-1')
GROUP BY nation.name
ORDER BY revenue desc;

CREATE TABLE LINEITEM (
        orderkey       INT,
        partkey        INT,
        suppkey        INT,
        linenumber     INT,
        quantity       DECIMAL,
        extendedprice  DECIMAL,
        discount       DECIMAL,
        tax            DECIMAL,
        returnflag     CHAR(1),
        linestatus     CHAR(1),
        shipdate       DATE,
        commitdate     DATE,
        receiptdate    DATE,
        shipinstruct   CHAR(25),
        shipmode       CHAR(10),
        comment        VARCHAR(44)
    );
select
sum(extendedprice*discount) as revenue
from
lineitem
where
shipdate >= DATE('1994-01-01')
and shipdate < date ('1995-01-01')
and discount >0.06 - 0.01 and discount<0.06+ 0.01 and quantity < 24;