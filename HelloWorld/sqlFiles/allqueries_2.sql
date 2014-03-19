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


CREATE TABLE ORDERS (
        orderkey       INT,
        custkey        INT,
        orderstatus    CHAR(1),
        orderdate      DATE,
        orderpriority  VARCHAR(15),
        clerk          VARCHAR(15),
        shippriority   INT,
        comment        VARCHAR(79)
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

--tpch2

SELECT
  s1.acctbal, s1.name, n1.name, p1.partkey, p1.mfgr, s1.address, s1.phone, s1.comment
FROM
  part p1, supplier s1, partsupp ps1, nation n1, region r1
WHERE
  p1.partkey = ps1.partkey
  AND s1.suppkey = ps1.suppkey and p1.size = 15
  AND p1.type like '%BRASS'
  AND s1.nationkey = n1.nationkey and n1.regionkey = r1.regionkey and r1.name = 'EUROPE'
  AND ps1.supplycost = (
  SELECT min(ps2.supplycost)
                        FROM partsupp ps2, supplier s2, nation n2, region r2
                        WHERE
                            p1.partkey = ps2.partkey
                            AND s2.suppkey = ps2.suppkey
                            AND s2.nationkey = n2.nationkey
                            AND n2.regionkey = r2.regionkey
                            AND r2.name = 'EUROPE'
                      )
ORDER BY s1.acctbal desc, n1.name, s1.name, p1.partkey;

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
  ( (n1.n_name = 'FRANCE') and (n2.n_name = 'GERMANY') ) or
  ( (n1.n_name = 'GERMANY') and (n2.n_name = 'FRANCE') )
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
