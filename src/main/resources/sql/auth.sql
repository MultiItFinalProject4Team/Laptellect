-- ================= 테이블 목록 =================
-- 회원 테이블
CREATE TABLE mem_member (
    member_no INT NOT NULL AUTO_INCREMENT,
    member_name VARCHAR(30) NOT NULL,
    email VARCHAR(255) NOT NULL,
    nick_name VARCHAR(50),
    tel VARCHAR(15),
    point INT NOT NULL DEFAULT 0 CHECK (point >= 0),
    role VARCHAR(15) NOT NULL DEFAULT 'ROLE_USER',
    login_type VARCHAR(15) NOT NULL DEFAULT 'local',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NULL ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (member_no),
    CONSTRAINT member_name_uk UNIQUE (member_name),
    CONSTRAINT nick_name_uk UNIQUE (nick_name)
);

-- 회원 테이블 회원 탈퇴 컬럼 add
ALTER TABLE mem_member
ADD COLUMN is_active CHAR(1) NOT NULL DEFAULT 'N',
ADD COLUMN is_active_at TIMESTAMP NULL;

ALTER TABLE mem_member
MODIFY COLUMN email VARCHAR(255) NULL;

-- 비밀번호 테이블
CREATE TABLE mem_password (
    password_id INT NOT NULL AUTO_INCREMENT,
    member_no INT NOT NULL,
    member_password VARCHAR(128) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NULL ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (password_id),
    CONSTRAINT password_member_no_fk FOREIGN KEY (member_no) REFERENCES mem_member(member_no) ON DELETE CASCADE
);

-- 소셜 회원 테이블
CREATE TABLE mem_social_member (
    social_id INT NOT NULL AUTO_INCREMENT,
    member_no INT NOT NULL,
    external_id VARCHAR(64) NOT NULL,
    PRIMARY KEY (social_id),
    CONSTRAINT social_member_no_fk FOREIGN KEY (member_no) REFERENCES mem_member(member_no) ON DELETE CASCADE
);

-- 판매자 테이블
CREATE TABLE mem_seller_member (
    seller_id INT NOT NULL AUTO_INCREMENT,
    member_no INT NOT NULL,
    company_name VARCHAR(64) NOT NULL,
    representative_name VARCHAR(50) NOT NULL,
    business_registration_number VARCHAR(50) NOT NULL,
    PRIMARY KEY (seller_id),
    CONSTRAINT seller_member_no_fk FOREIGN KEY (member_no) REFERENCES mem_member(member_no) ON DELETE CASCADE
);

-- 배송지 테이블
CREATE TABLE mem_delivery_address (
    address_id INT NOT NULL AUTO_INCREMENT,
    member_no INT NOT NULL,
    address_name VARCHAR(255) NOT NULL,
    recipient_name VARCHAR(100),
    postal_code VARCHAR(10),
    address VARCHAR(255),
    detail_address VARCHAR(255),
    recipient_phone VARCHAR(15),
    request VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (address_id),
    CONSTRAINT address_member_no_fk FOREIGN KEY (member_no) REFERENCES mem_member(member_no) ON DELETE CASCADE
);

-- 로그인 로그 테이블
CREATE TABLE log_login (
    login_log_id INT NOT NULL AUTO_INCREMENT,
    member_no INT NOT NULL,
    login_ip VARCHAR(45) NOT NULL,
    user_agent VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (login_log_id),
    CONSTRAINT log_login_member_no_fk FOREIGN KEY (member_no) REFERENCES mem_member(member_no) ON DELETE CASCADE
);

-- 방문자 수 테이블
CREATE TABLE log_count_visit (
    visit_log INT NOT NULL AUTO_INCREMENT,
    visit_count INT NOT NULL DEFAULT 0,
    created_at DATE NOT NULL,
    PRIMARY KEY (visit_log),
    UNIQUE (created_at)
);
-- ================= SELECT 목록 =================


-- ================= UPDATE 목록 =================


-- ================= DELETE 목록 =================


-- ================= 테스트용 테이블 =================
-- 회원 테이블
CREATE TABLE mem_member (
    member_no INT NOT NULL AUTO_INCREMENT,
    member_name VARCHAR(30) NOT NULL,
    email VARCHAR(255) NOT NULL,
    nick_name VARCHAR(50),
    tel VARCHAR(15),
    point INT NOT NULL DEFAULT 0 CHECK (point >= 0),
    role VARCHAR(15) NOT NULL DEFAULT 'ROLE_USER',
    login_type VARCHAR(15) NOT NULL DEFAULT 'local',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NULL ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (member_no),
    CONSTRAINT member_name_uk UNIQUE (member_name),
    CONSTRAINT nick_name_uk UNIQUE (nick_name)
);

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

CREATE TRIGGER after_update_point
AFTER INSERT ON payment_point
FOR EACH ROW
BEGIN
    UPDATE mem_member
    SET
        point = point + NEW.payment_point_change
    WHERE
        member_no = NEW.member_no;
END;

CREATE TABLE wishlist (
	wishlist_no INT NOT NULL AUTO_INCREMENT,
	product_no INT NOT NULL,
	member_no INT NOT NULL,
	created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
	CONSTRAINT wishlist_PK PRIMARY KEY(wishlist_no),
    CONSTRAINT wishlist_product_no_fk FOREIGN KEY (product_no) REFERENCES product(product_no),
	CONSTRAINT wishlist_member_no_fk FOREIGN KEY (member_no) REFERENCES mem_member(member_no) ON DELETE CASCADE
);