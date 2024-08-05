-- New script in localhost 2.
-- Connection Type: dev
-- Url: jdbc:mysql://localhost:3306/
-- workspace : H:\workspace\multi\04_db
-- Date: 2024. 7. 31.
-- Time: 오전 9:47:41

USE scott;

CREATE TABLE payment (
	payment_no INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50),
    productname VARCHAR(50),
    productinfo VARCHAR(255),
    productprice INT,
    purchaseprice INT,
    date_created TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    im_port_id varchar(255),
    refund CHAR(1) DEFAULT 'N',
    refund_date TIMESTAMP DEFAULT NULL
);
SELECT * from payment;
DROP TABLE payment;


CREATE TABLE paymentpage (
    username VARCHAR(50),
    productname VARCHAR(50),
    productinfo VARCHAR(255),
    date_created TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    productprice INT
);
INSERT INTO paymentpage values("jack", "LG GRAM", "WQXGA / GEFORCE RTX 3050 / 144HZ /WINDOWS내장 / 1440*1060", DEFAULT, 200);
DROP TABLE paymentpage;
SELECT * FROM paymentpage;


CREATE TABLE payment_point (
    payment_point_no INT NOT NULL AUTO_INCREMENT,
    member_no INT NOT NULL,
    im_port_id varchar(255),
    payment_point_change INT NOT NULL,
    payment_point_info VARCHAR(20),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (payment_point_no),
    CONSTRAINT member_no_fk FOREIGN KEY (member_no) REFERENCES mem_member(member_no) ON DELETE CASCADE
);
DROP TABLE payment_point;
SELECT * FROM payment_point;



CREATE TABLE payment_product_reviews (
    payment_product_reviews_no INT AUTO_INCREMENT PRIMARY key,
    username varchar(255),
    product_name varchar(255),
    tag_answer char(1) DEFAULT 'N',
    content varchar(255),
    rating varchar(255),
    im_port_id varchar(255),
    create_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    modify_date TIMESTAMP DEFAULT NULL
--     CONSTRAINT `payment_product_revies` FOREIGN KEY (`payment_product_no`) REFERENCES `payment_product` (`payment_product_no`),
--     CONSTRAINT `payment_product_revies` FOREIGN KEY (`product_no`) REFERENCES `products` (`product_no`),
--     CONSTRAINT `payment_product` FOREIGN KEY (`non_sentiment_no`) REFERENCES `laptop_non_sentiment` (`non_sentiment_no`)
);
INSERT INTO payment_product_reviews values( NULL , 1, 1 , 1, default ,default);
DROP TABLE payment_product_reviews;
SELECT * FROM payment_product_reviews;