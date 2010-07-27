delete from JPETSTORE.PROFILE
where USERID <> 'ACID'
AND USERID <> 'j2ee';

delete from JPETSTORE.SIGNON
where USERNAME <> 'ACID'
AND USERNAME <> 'j2ee';

delete from LINEITEM L
where (select USERID from ORDERS O where O.ORDERID = L.ORDERID) not in ('j2ee', 'ACID');

delete from ORDERSTATUS S
where (select USERID from ORDERS O where O.ORDERID = S.ORDERID) not in ('j2ee', 'ACID');


delete from ORDERS
where USERID not in ('j2ee', 'ACID');

delete from JPETSTORE.ACCOUNT
where USERID <> 'ACID'
AND USERID <> 'j2ee';