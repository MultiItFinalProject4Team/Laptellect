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
    product_name VARCHAR(50),
    purchase_price INT,
    date_created TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    im_port_id varchar(255),
    refund CHAR(1) DEFAULT 'N',
    refund_date TIMESTAMP DEFAULT NULL
);
SELECT * from payment;
DROP TABLE payment;

CREATE TABLE payment_point (
    payment_point_no INT NOT NULL AUTO_INCREMENT,
    member_no INT NOT NULL,
    im_port_id varchar(255),
    payment_point_change INT NOT NULL,
    payment_point_info VARCHAR(20),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
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
--     CONSTRAINT `payment_product_reviews` FOREIGN KEY (`product_no`) REFERENCES `products` (`product_no`),
--     CONSTRAINT `payment_product` FOREIGN KEY (`non_sentiment_no`) REFERENCES `laptop_non_sentiment` (`non_sentiment_no`)
);
INSERT INTO payment_product_reviews values( NULL , 1, 1 , 1, default ,default);
DROP TABLE payment_product_reviews;
SELECT * FROM payment_product_reviews;

SELECT * FROM ;

CREATE VIEW view_paymentpage AS
SELECT
    pd.product_name,
    pd.product_code,
    pd.type_no,
    pd.price,
    GROUP_CONCAT(CONCAT(pd.options, ' : ', pd.option_value) SEPARATOR ' / ') AS product_info,
    MAX(pd.upload_name) AS upload_name
FROM
    product_detail pd
GROUP BY
    pd.product_name, pd.product_code, pd.type_no, pd.price;
SELECT * FROM view_paymentpage;
DROP VIEW view_paymentpage;