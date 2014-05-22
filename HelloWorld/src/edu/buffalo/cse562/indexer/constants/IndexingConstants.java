package edu.buffalo.cse562.indexer.constants;

public class IndexingConstants {

    public static final String RECORD_MANAGER_NAME = "RQE";

    /*public static List<String> queries = Arrays.asList(
            "query01.sql",
            "query02.sql",
            "query03.sql",
            "query04.sql",
            "query05.sql",
            "query06.sql"
    );*/

    public static String queries =
            "SELECT COUNT(*) FROM LINEITEM;" +
                    "SELECT COUNT(ORDERKEY) FROM ORDERS;" +
                    "SELECT ORDERPRIORITY, COUNT(ORDERKEY) FROM ORDERS WHERE ORDERDATE >= date('1992-01-01') AND ORDERDATE < date('1995-01-01') GROUP BY ORDERPRIORITY ORDER BY ORDERPRIORITY;" +
                    "SELECT ORDERS.ORDERKEY, COUNT(DISTINCT LINENUMBER) AS LINECOUNT FROM ORDERS, LINEITEM WHERE ORDERS.ORDERKEY=LINEITEM.ORDERKEY AND ORDERS.ORDERKEY>=50 AND ORDERS.ORDERKEY<=250 GROUP BY ORDERS.ORDERKEY ORDER BY ORDERS.ORDERKEY;" +
                    "select customer.custkey, sum(lineitem.extendedprice * (1 - lineitem.discount)) as revenue, customer.acctbal, nation.name, customer.address, customer.phone, customer.comment from customer, orders, lineitem, nation where customer.custkey = orders.custkey and lineitem.orderkey = orders.orderkey and orders.orderdate >= date('1995-03-05') and orders.orderdate < date('1995-03-15') and lineitem.returnflag = 'R' and customer.nationkey = nation.nationkey group by customer.custkey, customer.acctbal, customer.phone, nation.name, customer.address, customer.comment order by revenue asc limit 100;" +
                    "select lineitem.shipmode, count(distinct orders.orderkey) from orders, lineitem where orders.orderkey = lineitem.orderkey and (lineitem.shipmode='AIR' or lineitem.shipmode='MAIL' or lineitem.shipmode='TRUCK' or lineitem.shipmode='SHIP') and orders.orderpriority <> '1-URGENT' and orders.orderpriority <> '2-HIGH' and lineitem.commitdate < lineitem.receiptdate and lineitem.shipdate = date('1997-05-14') group by lineitem.shipmode order by lineitem.shipmode;";

}

