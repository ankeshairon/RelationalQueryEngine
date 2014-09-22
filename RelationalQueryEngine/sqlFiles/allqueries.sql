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
        comment        VARCHAR(44),
        PRIMARY KEY (orderkey, linenumber),
        INDEX shipidx (shipdate)
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
        comment        VARCHAR(79),
        PRIMARY KEY (orderkey),
        INDEX orderidx (orderdate)
    );

CREATE TABLE PART (
        partkey      INT,
        name         VARCHAR(55),
        mfgr         CHAR(25),
        brand        CHAR(10),
        type         VARCHAR(25),
        size         INT,
        container    CHAR(10),
        retailprice  DECIMAL,
        comment      VARCHAR(23),
        PRIMARY KEY (partkey)
    );

CREATE TABLE CUSTOMER (
        custkey      INT,
        name         VARCHAR(25),
        address      VARCHAR(40),
        nationkey    INT,
        phone        CHAR(15),
        acctbal      DECIMAL,
        mktsegment   CHAR(10),
        comment      VARCHAR(117),
        PRIMARY KEY (custkey)
    );

CREATE TABLE SUPPLIER (
        suppkey      INT,
        name         CHAR(25),
        address      VARCHAR(40),
        nationkey    INT,
        phone        CHAR(15),
        acctbal      DECIMAL,
        comment      VARCHAR(101),
        PRIMARY KEY (suppkey)
    );

CREATE TABLE PARTSUPP (
        partkey      INT,
        suppkey      INT,
        availqty     INT,
        supplycost   DECIMAL,
        comment      VARCHAR(199),
        PRIMARY KEY (partkey, suppkey),
        INDEX suppliers(suppkey)
    );

CREATE TABLE NATION (
        nationkey    INT,
        name         CHAR(25),
        regionkey    INT,
        comment      VARCHAR(152),
        PRIMARY KEY (nationkey),
        INDEX nationname(name)
    );

CREATE TABLE REGION (
        regionkey    INT,
        name         CHAR(25),
        comment      VARCHAR(152),
        PRIMARY KEY (regionkey),
        INDEX regionname(name)
    );




-- [DELTA]
--    Range: 60-120
--    Default: 90

--tpch1

SELECT
  returnflag,
  linestatus,
  sum(quantity) as sum_qty,
  sum(extendedprice) as sum_base_price,
  sum(extendedprice*(1-discount)) as sum_disc_price,
  sum(extendedprice*(1-discount)*(1+tax)) as sum_charge,
  avg(quantity) as avg_qty,
  avg(extendedprice) as avg_price,
  avg(discount) as avg_disc,
  count(*) as count_order
FROM
  lineitem
WHERE
  shipdate <= DATE('1998-09-01')
GROUP BY
  returnflag, linestatus
ORDER BY
  returnflag, linestatus;

--tpch3

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

--tpch5

SELECT
  nation.name,
  sum(lineitem.extendedprice * (1 - lineitem.discount)) AS revenue
FROM
  customer, orders, lineitem, nation, region
WHERE
  customer.custkey = orders.custkey
  and lineitem.orderkey = orders.orderkey
  and customer.nationkey = nation.nationkey
  and nation.regionkey = region.regionkey
  and region.name = 'ASIA'
  and orders.orderdate >= DATE( '1994-01-01')
  and orders.orderdate < DATE ('1995-01-01')
GROUP BY nation.name
ORDER BY revenue desc;

--tpch6

select
sum(extendedprice*discount) as revenue
from
lineitem
where
shipdate >= DATE('1994-01-01')
and shipdate < date ('1995-01-01')
and  discount > 0.05
and discount< 0.07
and quantity < 24;


--tpch7

select suppnation, custnation, sum(volume) as revenue
from (
select n1.name as suppnation, n2.name as custnation, lineitem.extendedprice * (1 - lineitem.discount) as volume
from supplier, lineitem, orders, customer, nation n1, nation n2
where supplier.suppkey = lineitem.suppkey
and orders.orderkey = lineitem.orderkey
and customer.custkey = orders.custkey
and supplier.nationkey = n1.nationkey
and customer.nationkey = n2.nationkey
and (
  ( (n1.name = 'FRANCE') and (n2.name = 'GERMANY') ) or
  ( (n1.name = 'GERMANY') and (n2.name = 'FRANCE') )
)
and lineitem.shipdate >= date('1995-01-01')
and lineitem.shipdate <= date('1996-12-31')
) as shipping
group by
suppnation,
custnation
order by
suppnation,
custnation;


--tpch10

select customer.custkey, customer.name, sum(lineitem.extendedprice * (1 - lineitem.discount)) as revenue, customer.acctbal, nation.name, customer.address, customer.phone, customer.comment
from customer, orders, lineitem, nation
where customer.custkey = orders.custkey
and lineitem.orderkey = orders.orderkey
and orders.orderdate >= date('1993-10-01')
and orders.orderdate < date('1994-01-01')
and lineitem.returnflag = 'R'
and customer.nationkey = nation.nationkey
group by customer.custkey, customer.name, customer.acctbal, customer.phone, nation.name, customer.address, customer.comment
order by revenue asc
limit 20;


--tpch12

select lineitem.shipmode, count(distinct orders.orderkey)
from orders, lineitem
where orders.orderkey = lineitem.orderkey
and (lineitem.shipmode='MAIL' or lineitem.shipmode='SHIP')
and orders.orderpriority <> '1-URGENT' and orders.orderpriority <> '2-HIGH'
and lineitem.commitdate < lineitem.receiptdate
and lineitem.shipdate < lineitem.commitdate
and lineitem.receiptdate >= date('1994-01-01')
and lineitem.receiptdate < date('1995-01-01')
group by lineitem.shipmode
order by lineitem.shipmode;


--tpch16

select part.brand, part.type, part.size, count(distinct partsupp.suppkey) as suppliercount
from partsupp, part
where part.partkey = partsupp.partkey and part.brand <> 'Brand#11'
group by part.brand, part.type, part.size
order by suppliercount, part.brand, part.type, part.size;
