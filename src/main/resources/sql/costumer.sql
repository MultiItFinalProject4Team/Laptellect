CREATE TABLE personalquestion_category(
	personalq_categorycode varchar(50) PRIMARY KEY,
	personalq_categoryname varchar(100)
);

CREATE TABLE productquestion_category(
	productq_categorycode varchar(50) PRIMARY KEY,
	productq_categoryname varchar(100)
);

CREATE TABLE personal_question (
    personalq_no INT AUTO_INCREMENT PRIMARY KEY,
    member_no INT,
    personalq_categorycode VARCHAR(50),
    title VARCHAR(100) NOT NULL,
    content text NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    answer VARCHAR(2) DEFAULT 'N',
    reference_code VARCHAR(100),
    FOREIGN KEY (member_no) REFERENCES member(member_no),
    FOREIGN KEY (personalq_categorycode) REFERENCES personalquestion_category(personalq_categorycode)
);

CREATE TABLE product_question (
    productq_no INT AUTO_INCREMENT PRIMARY KEY,
    member_no INT,
    productq_categorycode VARCHAR(50),
    product_no int,
    title VARCHAR(100) NOT NULL,
    content text NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    answer VARCHAR(2) DEFAULT 'N',
    secret VARCHAR(2) DEFAULT 'N',
    reference_code VARCHAR(100),
    FOREIGN KEY (member_no) REFERENCES member(member_no),
    FOREIGN KEY (productq_categorycode) REFERENCES productquestion_category(productq_categorycode)
    #,FOREIGN KEY (product_no) REFERENCES product(product_no)
);

CREATE TABLE personal_answer(
	personala_no int AUTO_INCREMENT PRIMARY KEY,
	personalq_no int,
	title varchar(100) NOT null,
	content text NOT NULL,
	created_at timestamp DEFAULT current_timestamp,
	updated_at timestamp,
	reference_code varchar(100),
	FOREIGN KEY (personalq_no) REFERENCES personal_question(personalq_no)
);

CREATE TABLE product_answer(
	producta_no int AUTO_INCREMENT PRIMARY KEY,
	productq_no int,
	title varchar(100) NOT null,
	content text NOT NULL,
	created_at timestamp DEFAULT current_timestamp,
	updated_at timestamp,
	reference_code varchar(100),
	FOREIGN KEY (productq_no) REFERENCES product_question(productq_no)
);

CREATE TABLE images(
	image_no int AUTO_INCREMENT PRIMARY KEY,
	origin_name varchar(255),
	upload_name varchar(255),
	created_at timestamp DEFAULT current_timestamp,
	updated_at timestamp,
	reference_code varchar(100)
);

DELIMITER //

CREATE TRIGGER before_insert_personal_question
BEFORE INSERT ON personal_question
FOR EACH ROW
BEGIN
    DECLARE next_id INT;
    SET next_id = (SELECT AUTO_INCREMENT
                   FROM information_schema.TABLES
                   WHERE TABLE_SCHEMA = DATABASE()
                   AND TABLE_NAME = 'personal_question');
    SET NEW.reference_code = CONCAT('personalq', next_id);
END //

DELIMITER ;

DELIMITER //
CREATE TRIGGER before_insert_product_question
BEFORE INSERT ON product_question
FOR EACH ROW
BEGIN
    DECLARE next_id INT;
    SET next_id = (SELECT AUTO_INCREMENT
                   FROM information_schema.TABLES
                   WHERE TABLE_SCHEMA = DATABASE()
                   AND TABLE_NAME = 'product_question');
    SET NEW.reference_code = CONCAT('productq', next_id);
END //

DELIMITER ;

DELIMITER //

CREATE TRIGGER before_insert_personal_answer
BEFORE INSERT ON personal_answer
FOR EACH ROW
BEGIN
    DECLARE next_id INT;
    SET next_id = (SELECT AUTO_INCREMENT
                   FROM information_schema.TABLES
                   WHERE TABLE_SCHEMA = DATABASE()
                   AND TABLE_NAME = 'personal_answer');
    SET NEW.reference_code = CONCAT('personala', next_id);
END //

DELIMITER ;

DELIMITER //
CREATE TRIGGER before_insert_product_answer
BEFORE INSERT ON product_answer
FOR EACH ROW
BEGIN
    DECLARE next_id INT;
    SET next_id = (SELECT AUTO_INCREMENT
                   FROM information_schema.TABLES
                   WHERE TABLE_SCHEMA = DATABASE()
                   AND TABLE_NAME = 'product_answer');
    SET NEW.reference_code = CONCAT('producta', next_id);
END //

DELIMITER ;

INSERT INTO personalquestion_category VALUES ('personalq_member','회원');
INSERT INTO personalquestion_category VALUES ('personalq_else','기타');
INSERT INTO productquestion_category VALUES ('productq_product','상품');
INSERT INTO productquestion_category VALUES ('productq_else','기타');