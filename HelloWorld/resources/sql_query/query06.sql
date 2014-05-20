UPDATE LINEITEM SET shipdate=date('1997-05-14') WHERE shipdate>=date('1997-05-02') AND shipdate<date('1997-05-14');
select lineitem.shipmode, count(distinct orders.orderkey)
from orders, lineitem
where orders.orderkey = lineitem.orderkey
and (lineitem.shipmode='AIR' or lineitem.shipmode='MAIL' or lineitem.shipmode='TRUCK' or lineitem.shipmode='SHIP')
and orders.orderpriority <> '1-URGENT' and orders.orderpriority <> '2-HIGH'
and lineitem.commitdate < lineitem.receiptdate
and lineitem.shipdate = date('1997-05-14')
group by lineitem.shipmode
order by lineitem.shipmode;
