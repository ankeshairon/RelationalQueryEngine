CREATE TABLE table1(ID int, NAME CHAR(25), C1 CHAR(5), C2 CHAR(5), V1 int, V2 int)
--CREATE TABLE table2(ID int, NAME CHAR(5), marks int)

insert into table1 values (1, 'n1', 'a', 'a', 1,1)  ;
insert into table1 values (11, 'n2', 'a', 'a', 1,1) ;
insert into table1 values (2, 'n3', 'b', 'a', 1,1)  ;
insert into table1 values (22, 'n4', 'b', 'a', 1,1) ;
insert into table1 values (3, 'n5', 'c', 'a', 1,1)  ;
insert into table1 values (33, 'n6', 'c', 'a', 1,1) ;
insert into table1 values (4, 'n7', 'd', 'a', 1,1)  ;
insert into table1 values (44, 'n8', 'd', 'a', 1,1) ;



SELECT table1.NAME FROM table1
GROUP BY G1

--
----schemas
--CREATE TABLE LINEITEM (
--        orderkey       INT,
--        partkey        INT,
--        suppkey        INT,
--        linenumber     INT,
--        quantity       DECIMAL,
--        extendedprice  DECIMAL,
--        discount       DECIMAL,
--        tax            DECIMAL,
--        returnflag     CHAR(1),
--        linestatus     CHAR(1),
--        shipdate       DATE,
--        commitdate     DATE,
--        receiptdate    DATE,
--        shipinstruct   VARCHAR(25),
--        shipmode       VARCHAR(10),
--        comment        VARCHAR(44)
--    );
--
--
--CREATE TABLE ORDERS (
--        orderkey       INT,
--        custkey        INT,
--        orderstatus    CHAR(1),
--        totalprice     DECIMAL,
--        orderdate      DATE,
--        orderpriority  VARCHAR(15),
--        clerk          VARCHAR(15),
--        shippriority   INT,
--        comment        VARCHAR(79)
--    );
--
--CREATE TABLE PART (
--        partkey      INT,
--        name         VARCHAR(55),
--        mfgr         VARCHAR(25),
--        brand        VARCHAR(10),
--        type         VARCHAR(25),
--        size         INT,
--        container    VARCHAR(10),
--        retailprice  DECIMAL,
--        comment      VARCHAR(23)
--    );
--
--CREATE TABLE CUSTOMER (
--        custkey      INT,
--        name         VARCHAR(25),
--        address      VARCHAR(40),
--        nationkey    INT,
--        phone        VARCHAR(15),
--        acctbal      DECIMAL,
--        mktsegment   VARCHAR(10),
--        comment      VARCHAR(117)
--    );
--
--CREATE TABLE SUPPLIER (
--        suppkey      INT,
--        name         VARCHAR(25),
--        address      VARCHAR(40),
--        nationkey    INT,
--        phone        VARCHAR(15),
--        acctbal      DECIMAL,
--        comment      VARCHAR(101)
--    );
--
--CREATE TABLE PARTSUPP (
--        partkey      INT,
--        suppkey      INT,
--        availqty     INT,
--        supplycost   DECIMAL,
--        comment      VARCHAR(199)
--    );
--
--CREATE TABLE NATION (
--        nationkey    INT,
--        name         VARCHAR(25),
--        regionkey    INT,
--        comment      VARCHAR(152)
--    );
--
--CREATE TABLE REGION (
--        regionkey    INT,
--        name         VARCHAR(25),
--        comment      VARCHAR(152)
--    );
--
--select lineitem.shipmode, count(distinct orders.orderkey)
--from orders, lineitem
--where orders.orderkey = lineitem.orderkey
--and (lineitem.shipmode='AIR' or lineitem.shipmode='MAIL' or lineitem.shipmode='TRUCK' or lineitem.shipmode='SHIP')
--and orders.orderpriority <> '1-URGENT'
--and orders.orderpriority <> '2-HIGH'
--and lineitem.commitdate < lineitem.receiptdate
--and lineitem.shipdate < lineitem.commitdate
--and lineitem.receiptdate >= date('1995-03-05')
--and lineitem.receiptdate < date('1996-03-05')
--group by lineitem.shipmode
--order by lineitem.shipmode;
