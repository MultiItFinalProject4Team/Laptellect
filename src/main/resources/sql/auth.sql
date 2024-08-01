-- 회원 테이블
CREATE TABLE mem_member (
    member_no INT NOT NULL AUTO_INCREMENT,
    member_name VARCHAR(30) NOT NULL,
    email VARCHAR(255) NOT NULL,
    nick_name VARCHAR(50),
    tel VARCHAR(15),
    point INT NOT NULL DEFAULT 0 CHECK (payment_possession_point >= 0),
    role VARCHAR(15) NOT NULL DEFAULT 'ROLE_USER',
    login_type VARCHAR(15) NOT NULL DEFAULT 'local',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NULL ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (member_no),
    UNIQUE (member_name),
    UNIQUE (nick_name)
);

-- 비밀번호 테이블
CREATE TABLE mem_password (
    password_id INT NOT NULL AUTO_INCREMENT,
    member_no INT NOT NULL,
    password VARCHAR(128) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NULL ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (password_id),
    FOREIGN KEY (member_no) REFERENCES mem_member(member_no) ON DELETE CASCADE
);

-- 소셜 회원 테이블
CREATE TABLE mem_social_member (
    social_id INT NOT NULL AUTO_INCREMENT,
    member_no INT NOT NULL,
    external_id VARCHAR(64) NOT NULL,
    PRIMARY KEY (social_id),
    FOREIGN KEY (member_no) REFERENCES mem_member(member_no) ON DELETE CASCADE
);

-- 배송지 테이블
CREATE TABLE mem_delivery_address (
    address_id INT NOT NULL AUTO_INCREMENT,
    member_no INT NOT NULL,
    recipient_name VARCHAR(100) NOT NULL,
    postal_code VARCHAR(10) NOT NULL,
    address VARCHAR(255) NOT NULL,
    detail_address VARCHAR(255) NOT NULL,
    recipient_phone VARCHAR(15) NOT NULL,
    request VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (address_id),
    FOREIGN KEY (member_no) REFERENCES mem_member(member_no)
);