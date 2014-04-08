--CREATE TABLE table1 (id1 int, name1 varchar(25));
--CREATE TABLE table2 (id2 int, name2 varchar(25));
--
--select * from table1, table2
--where id1=id2;

--CREATE TABLE t1(ID int, NAME CHAR(25), C1 CHAR(5), C2 CHAR(5), V1 int, V2 int)
--
--insert into t1 values (1,   'n1',   'a', '1', 1,1)  ;
--insert into t1 values (11, 'n2',    'b', '1', 1,1) ;
--insert into t1 values (2,   'n3',    'b', '2', 1,1)  ;
--insert into t1 values (22,  'n4',   'c', '1', 1,1) ;
--insert into t1 values (3,    'n5',   'c', '2', 1,1)  ;
--insert into t1 values (33,  'n6', '  c', '3', 1,1) ;
--insert into t1 values (4,    'n7',   'd', '1', 1,1)  ;
--insert into t1 values (44,  'n8', '  d', '2', 1,1) ;
--insert into t1 values (5,    'n9',   'd', '3', 1,1) ;
--insert into t1 values (55,  'n10', 'd', '4', 1,1) ;
--
--select name, count (distinct c1), c2 from t1 group by c2

--
--schemas
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
        shipinstruct   VARCHAR(25),
        shipmode       VARCHAR(10),
        comment        VARCHAR(44)
    );

CREATE TABLE PART (
        partkey      INT,
        name         VARCHAR(55),
        mfgr         VARCHAR(25),
        brand        VARCHAR(10),
        type         VARCHAR(25),
        size         INT,
        container    VARCHAR(10),
        retailprice  DECIMAL,
        comment      VARCHAR(23)
    );

CREATE TABLE CUSTOMER (
        custkey      INT,
        name         VARCHAR(25),
        address      VARCHAR(40),
        nationkey    INT,
        phone        VARCHAR(15),
        acctbal      DECIMAL,
        mktsegment   VARCHAR(10),
        comment      VARCHAR(117)
    );

CREATE TABLE SUPPLIER (
        suppkey      INT,
        name         VARCHAR(25),
        address      VARCHAR(40),
        nationkey    INT,
        phone        VARCHAR(15),
        acctbal      DECIMAL,
        comment      VARCHAR(101)
    );

CREATE TABLE ORDERS (
        orderkey       INT,
        custkey        INT,
        orderstatus    CHAR(1),
        totalprice     DECIMAL,
        orderdate      DATE,
        orderpriority  VARCHAR(15),
        clerk          VARCHAR(15),
        shippriority   INT,
        comment        VARCHAR(79)
    );

CREATE TABLE PARTSUPP (
        partkey      INT,
        suppkey      INT,
        availqty     INT,
        supplycost   DECIMAL,
        comment      VARCHAR(199)
    );

CREATE TABLE NATION (
        nationkey    INT,
        name         VARCHAR(25),
        regionkey    INT,
        comment      VARCHAR(152)
    );

CREATE TABLE REGION (
        regionkey    INT,
        name         VARCHAR(25),
        comment      VARCHAR(152)
    );


select suppnation, custnation, sum(volume) as revenue
from (
select n1.name as suppnation, n2.name as custnation, lineitem.extendedprice * (1 - lineitem.discount) as volume
from supplier, lineitem, orders, customer, nation n1, nation n2
where supplier.suppkey = lineitem.suppkey
and orders.orderkey = lineitem.orderkey
and customer.custkey = orders.custkey
and supplier.nationkey = n1.nationkey
and customer.nationkey = n2.nationkey
and lineitem.shipdate >= date('1995-01-01')
and lineitem.shipdate <= date('1996-12-31')
) as shipping
group by
suppnation,
custnation
order by
suppnation,
custnation;
