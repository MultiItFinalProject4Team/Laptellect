-- 상품 테이블
CREATE TABLE product (
    product_no INT NOT NULL AUTO_INCREMENT,
    type_no INT NOT NULL,
    product_name VARCHAR(255) NOT NULL,
    price INT NOT NULL,
    product_code VARCHAR(20) NOT NULL,
    reference_code VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NULL ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT product_PK PRIMARY KEY(product_no),
    CONSTRAINT fk_product_type_no FOREIGN KEY (type_no) REFERENCES product_type(type_no)
);
-- product 테이블 제약조건
ALTER TABLE product
ADD CONSTRAINT unique_product_code UNIQUE (product_code);

alter table product
add column view_count INT not null default 0;

-- 상품 카테고리 테이블
CREATE TABLE product_category (
    category_no VARCHAR(50) NOT NULL,
    type_no INT NOT NULL,
    options VARCHAR(50),
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
    category_no VARCHAR(50) NOT NULL,
    option_value VARCHAR(50),
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
	CONSTRAINT wishlist_PK PRIMARY KEY(wishlist_no),
    CONSTRAINT wishlist_product_no_fk FOREIGN KEY (product_no) REFERENCES product(product_no),
	CONSTRAINT wishlist_member_no_fk FOREIGN KEY (member_no) REFERENCES mem_member(member_no) ON DELETE CASCADE
);

-- 이미지 매핑 테이블
CREATE TABLE images (
	image_no INT NOT NULL AUTO_INCREMENT,
	reference_code VARCHAR(255) NOT NULL,
	origin_name VARCHAR(255) NOT NULL,
	upload_name VARCHAR(255) NOT NULL,
	created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
	updated_at TIMESTAMP NULL ON UPDATE CURRENT_TIMESTAMP,
	CONSTRAINT images_PK PRIMARY KEY(image_no)
);


-- 리뷰 데이터 테이블
create table review (
	review_no INT NOT NULL AUTO_INCREMENT PRIMARY key,
	product_no INT NOT NULL,
	rating INT NOT NULL,
	title varchar(255) NOT NULL,
	content text NOT null,
	create_date TIMESTAMP default CURRENT_TIMESTAMP,
	FOREIGN key (product_no) REFERENCES product(product_no) ON DELETE CASCADE

);

-- 프로덕트 디테일 view
CREATE VIEW vw_product_detail AS
SELECT
	p.product_no,
    p.product_name,
    p.product_code,
    p.type_no,
    p.price,
    c.category_no,
    c.options,
    s.option_value,
    i.upload_name
FROM
    product p
JOIN
    product_spec s ON p.product_no = s.product_no
JOIN
    product_category c ON s.category_no = c.category_no
JOIN
    images i ON p.reference_code = i.reference_code;

    create view vw_spec_and_value as
    SELECT
    s.spec_no,
    s.product_no,
    s.category_no,
    s.option_value,
    c.options
    FROM
    product_spec s
    JOIN
    product p ON s.product_no = p.product_no
    JOIN
    product_category c ON s.category_no = c.category_no

-- view 테이블 삭제

 drop view vw_spec_and_value
 drop view vw_product_detail





-- 테이블 데이터 삭제

delete from images;
delete from product ;
delete from product_category;
delete from product_spec ;
delete from review ;