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
  sum(extendedprice*(1-discount)*(1+tax)) as sum_charge
FROM
  lineitem
WHERE
  shipdate <= DATE('1998-09-01')
GROUP BY
  returnflag, linestatus
ORDER BY
  returnflag, linestatus;

CREATE TABLE TEST (
        val       INT
    );

SELECT
  sum(val)
FROM
  TEST;