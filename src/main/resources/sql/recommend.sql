-- 태그 테이블
CREATE TABLE laptop_tag (
    tag_no INT NOT NULL AUTO_INCREMENT, --태그 번호
    tag_data VARCHAR(50) NOT NULL,--태그 데이터
    tag_etc VARCHAR(50) NOT NULL , --시스템, 사용자 등등 기타 태그

    CONSTRAINT laptop_tag_PK PRIMARY KEY(tag_no)
);
-- 매핑 테이블
CREATE TABLE machine_tagkey(
    product_no INT NOT NULL ,
    tag_no INT NOT NULL,
    UNIQUE (product_no, tag_no), --중복방지
   CONSTRAINT  product_FK FOREIGN KEY(product_no) REFERENCES product(product_no),
    CONSTRAINT tag_FK FOREIGN KEY(tag_no) REFERENCES laptop_tag(tag_no)
);

