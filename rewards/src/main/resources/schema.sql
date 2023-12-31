CREATE TABLE IF NOT EXISTS CUSTOMER (
CUSTOMER_ID int primary key,
CUSTOMER_NAME VARCHAR2(50)
);

CREATE TABLE IF NOT EXISTS TRANSACTION (
TRANSACTION_ID int primary key,
CUSTOMER_ID int,
TRANSACTION_DATE DATE,
AMOUNT int);

ALTER TABLE TRANSACTION
    ADD FOREIGN KEY (CUSTOMER_ID)
    REFERENCES CUSTOMER(CUSTOMER_ID);
