-- 상품 테이블
CREATE TABLE product (
    product_no INT NOT NULL AUTO_INCREMENT,
    type_no INT NOT NULL,
    product_name VARCHAR(255) NOT NULL,
    price INT NOT NULL,
    product_code VARCHAR(20) NOT NULL,
    reference_code VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT product_PK PRIMARY KEY(product_no),
    CONSTRAINT fk_product_type_no FOREIGN KEY (type_no) REFERENCES product_type(type_no)
);
-- 상품 카테고리 테이블
CREATE TABLE product_category (
    category_no INT NOT NULL AUTO_INCREMENT,
    type_no INT NOT NULL,
    options VARCHAR(20),
    CONSTRAINT product_category_PK PRIMARY KEY(category_no),
    CONSTRAINT fk_product_category_type_no FOREIGN KEY (type_no) REFERENCES product_type(type_no)
);
-- 상품타입 테이블
CREATE TABLE product_type (
    type_no INT NOT NULL AUTO_INCREMENT,
    type_name VARCHAR(20) NOT NULL,
    CONSTRAINT product_type_PK PRIMARY KEY(type_no)
);

--상품타입 데이터
insert into product_type values(1,'노트북');
insert into product_type values(2,'마우스');
insert into product_type values(3,'키보드');


-- 상품스펙 테이블
CREATE TABLE product_spec (
    spec_no INT NOT NULL AUTO_INCREMENT,
    product_no INT NOT NULL,
    category_no INT NOT NULL,
    option_value VARCHAR(20),
    CONSTRAINT product_spec_PK PRIMARY KEY(spec_no),
    CONSTRAINT fk_product_spec_product_no FOREIGN KEY (product_no) REFERENCES product(product_no),
    CONSTRAINT fk_product_spec_category_no FOREIGN KEY (category_no) REFERENCES product_category(category_no)
);
-- 위시리스트 테이블
CREATE TABLE wishlist (
	wishlist_no INT NOT NULL AUTO_INCREMENT,
	product_no INT NOT NULL,
	member_no INT NOT NULL,
	created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
	CONSTRAINT wishlist_PK PRIMARY KEY(wishlist_no)
);