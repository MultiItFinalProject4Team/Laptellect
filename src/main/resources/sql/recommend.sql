-- 태그 테이블
CREATE TABLE laptop_tag (
    tag_no INT NOT NULL AUTO_INCREMENT, --태그 번호
    tag_data VARCHAR(50) NOT NULL,--태그 데이터
    CONSTRAINT laptop_tag_PK PRIMARY KEY(tag_no)
);
-- 매핑 테이블
CREATE TABLE machine_tagkey(
    product_no INT NOT NULL ,
    tag_no INT NOT NULL,
    PRIMARY KEY (product_no, tag_no),
   CONSTRAINT  product_FK FOREIGN KEY(product_no) REFERENCES product(product_no) ON DELETE CASCADE,
    CONSTRAINT tag_FK FOREIGN KEY(tag_no) REFERENCES laptop_tag(tag_no) ON DELETE CASCADE
);

--스팩 점수화 테이블
CREATE TABLE laptop_data(
    product_no INT NOT NULL AUTO_INCREMENT,
    cpu_score INT NULL,
    gpu_score INT NULL,
    ram_score INT NULL,
    storage_score INT NULL,
    price_score INT NULL,
    weight_score INT NULL,
    screen_size_score INT NULL,
    resolution_score INT NULL,
    FOREIGN KEY (product_no) REFERENCES product(product_no) ON DELETE CASCADE
)
--감정 분석 테이블
CREATE TABLE laptop_sentiment (
    product_no INT NOT NULL ,
        sentiment_positive INT NOT NULL,
    sentiment_denial INT NOT NULL,
    sentiment_neutrality INT NOT NULL,
    primary key (product_no),
    FOREIGN KEY (product_no) REFERENCES product(product_no) ON DELETE CASCADE
);

ALTER TABLE machine_tagkey
    ADD CONSTRAINT product_FK
     FOREIGN KEY (product_no)
     REFERENCES product(product_no)
     ON DELETE CASCADE ON UPDATE CASCADE;

ALTER TABLE machine_tagkey
    ADD CONSTRAINT tag_FK
    FOREIGN KEY (tag_no)
    REFERENCES laptop_tag(tag_no)
    ON DELETE CASCADE ON UPDATE CASCADE;

ALTER TABLE laptop_data
    ADD CONSTRAINT product_data_FK
    FOREIGN KEY (product_no)
    REFERENCES product(product_no)
    ON DELETE CASCADE ON UPDATE CASCADE;

ALTER TABLE laptop_sentiment
    ADD CONSTRAINT product_sentiment_FK
    FOREIGN KEY (product_no)
    REFERENCES product(product_no)
    ON DELETE CASCADE ON UPDATE CASCADE;

-- 태그 내용
INSERT INTO laptop_tag (tag_data) VALUES
('게이밍'),
('작업용'),
('사무용'),
('가벼움'),
('무거움'),
('넓은 화면'),
('작은 화면'),
('코딩'),
('슬림'),
('최신제품'),
('적은 USB 단자'),
('많은 USB 단자'),
('동영상 편집'),
('고전력'),
('저전력'),
('넉넉한 저장 공간'),
('높은 해상도'),
('리그오브레전드'),
('배틀그라운드'),
('로스트아크'),
('눈부심 방지'),
('중간 화면 사이즈'),
('착한 가격 겜트북'),
('착한 가격'),
('가성비 겜트북'),
('가성비'),
('고성능'),
('비즈니스 모델'),
('윈도우 있음'),
('펠월드'),
('초경량');



