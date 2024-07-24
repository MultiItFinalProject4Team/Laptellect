-- 상품 테이블
CREATE TABLE product (
	product_no INT NOT NULL AUTO_INCREMENT,
	type_no INT NOT NULL,
	product_name VARCHAR(255) NOT NULL,
	price INT NOT NULL,
	reference_code VARCHAR(255),
	created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
	updated_at TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
	CONSTRAINT product_PK PRIMARY KEY(product_no)
);
-- 상품 카테고리 테이블
CREATE TABLE product_category (
	category_no INT NOT NULL AUTO_INCREMENT,
	type_no INT NOT NULL,
	options VARCHAR(20) NULL,
	CONSTRAINT product_category_PK PRIMARY KEY(category_no)
);
-- 상품타입 테이블
CREATE TABLE product_type (
	type_no INT NOT NULL,
	type_name VARCHAR(20) NOT NULL,
	CONSTRAINT product_type_PK PRIMARY KEY(type_no)
);
-- 상품스펙 테이블
CREATE TABLE product_spec (
	spec_no INT NOT NULL AUTO_INCREMENT,
	product_no INT NOT NULL,
	category_no INT NOT NULL,
	option_value VARCHAR(20) NULL,
	CONSTRAINT product_spec_PK PRIMARY KEY(spec_no)
);
-- 위시리스트 테이블
CREATE TABLE wishlist (
	wishlist_no INT NOT NULL AUTO_INCREMENT,
	product_no INT NOT NULL,
	member_no INT NOT NULL,
	created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
	CONSTRAINT wishlist_PK PRIMARY KEY(wishlist_no)
);