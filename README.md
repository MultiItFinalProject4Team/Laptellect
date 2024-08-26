# Notebook
# Java Spring Boot를 사용한 노트북 추천 및 구매 쇼핑몰 개발

- **1. 프로젝트 개요**
    
    **1-1) 프로젝트 개요** 
    
      추천 시스템은 인터넷의 발전에 따라 상품 구매 및 선호에 대한 사용자의 피드백을 얻기 쉬워지면서 이러한 피드백을 바탕으로 사용자의 상품 구매를 유도하여 사용자와 기업 모두 이득을 볼 수 있게 만들어 준다. 이 프로젝트는 사용자의 정보와 리뷰 데이터를 사용해 노트북을 추천하는 웹사이트를 설계 및 구현하는 프로젝트다. 추천 기능은 사용자가 질의 응답을 통하여 입력한 정보를 사용해 추천하는 방법을 사용한다. 또한 상품의 태그는 실제 상품의 정보를 내부적으로 처리하여 생성하는 방식과 쇼핑몰의 리뷰를 통하여 생성하며 상품 추천에 사용된다.
    
    **1-2) 개발 기능**
    
    - == AUTH, MyPage ==
        - 로그인
            - User Login과 Social User Login(KaKao, Naver)의 두 가지 방식을 지원
            - JWT (JSON Web Token)를 사용하여 인증 및 인가.
            - Refresh Token을 Redis에서 관리
            - 쿠키를 통한 아이디 저장 기능 제공
        - 아이디 관리
            - 아이디 저장 : 쿠키(30일)를 통한 아이디 저장 기능 제공
            - 아이디 찾기 : 아이디 찾기 기능 제공
            - 임시 비밀번호 발급 : 회원의 이메일을 입력 받은 뒤, SMTP를 사용해 해당 이메일로 임시 비밀번호를 발급
                - UUID 또는 배열을 사용해 임시 비밀번호 생성
                - 임시 비밀번호는 Redis 또는 DB에 저장하여 관리
                - Social 회원은 정책 상 비밀번호를 추가로 기입 받지 못하도록 하였기에 불가능
        - 회원 가입
            - 회원 가입 시 필수적으로 입력 받아야 할 정보는 [Email], [Password], [닉네임], [성별], [폰 번호]을 입력 받아야 함
                - [Email], [닉네임], [폰 번호]는 검증 과정 필요
                - 일반 회원 가입 이메일과 소셜 회원 가입 이메일은 중복 허용, 회원 가입 타입 컬럼을 통해 구분
                - Password는 정규식을 사용하여 형태 검증
            - 일반 회원 가입과 Social 회원 가입 시 동일한 이메일로 가입 할 경우 별도의 회원으로 처리
        - 마이 페이지
            - 회원 정보 변경
                - [닉네임], [폰 번호] 변경
                    - [닉네임], [폰 번호]는 중복 검증 과정 필요
            - 비밀번호 변경
                - 현재 비밀번호 또는 임시 비밀번호를 입력 받은 뒤, 변경할 비밀번호를 입력하여 비밀번호 변경
                - Social 화원은 비밀번호가 없으므로 변경 불가
            - 구매 내역 확인
                - 사용자가 구매한 상품의 내역을 확인 할 수 있어야 함
            - 리뷰 내역 확인
                - 사용자가 남긴 리뷰를 확인 할 수 있어야 함
            - 포인트 확인
                - 현재 사용자가 보유 중인 포인트와 포인트 변동 내역 출력
            - 방문자 수 카운트
                - 쿠키를 사용해 접속한 사용자를 카운트 ( 바뀔 가능성 있음 )
        - 판매자 페이지
            - 상품 관리
                - 판매 상품 리스트 조회
                - 상품 등록 / 수정 / 삭제
                - 상품 태그 조회 / 제한 / 삭제
            - 주문 관리
                - 주문 내역 확인
                - 주문 처리 상태 변경
            - 고객 관리
                - 상품 문의 관리
                    - 상품 문의 답변 / 삭제
                - 리뷰 관리
                    - 부적절한 리뷰 삭제
            - 상품 통계 출력
                - 판매 통계
    - == 추천 서비스 ==
        - **큐레이션을 사용한 사용자 맞춤형 추천 서비스**
            - 사용자가 입력한 정보를 기반으로 원하는 노트북을 추천
            - 사용 목적(태그) → 가격대 → 주요 특징 → 부가사항(편의성, 휴대성) → AS
                - 결과가 없을 시 결과 없음 출력
        - **노트북 부품 성능을 기반으로 점수 / 태그화**
            - 노트북의 정보를 내부적으로 분류하여 CPU/GPU/RAM/Storage / 무게/휴대성 등 노트북의 스펙을 점수로 환산
                - CPU / GPU는 PassMark Benchmark Charts 참고, 다른 사항은 내부적인 기준에 맞춰 수치로 환산
            
            ![Untitled](https://prod-files-secure.s3.us-west-2.amazonaws.com/9dd2ce73-4c67-42f2-9101-93034af92f16/999a10dc-d75e-4d86-9b6e-0c1dcbbe1619/Untitled.png)
            
            - 태그화
                - 점수를 기반으로 사용 목적 및 특성 태그 생성
                - 특정 부가 기능에 맞추어 태그 생성
        - **리뷰 데이터를 사용한 키보드, 마우스 정보 태그화**
            - 리뷰 데이터에 형태소 분석기를 사용하여 품사 태깅을 한 뒤, TF-IDF를 사용해 키워드의 중요도를 추출 하여 태그를 생성해 상품에 부여한다.
        - **Naver Clova Sentiment API를 사용한 리뷰 감성 분석**
            - Clova Sentiment API를 사용하여 리뷰에서 긍/부정 감성 분석 후 통계를 내어 해당 상품의 긍 / 부정 정도를 출력한다.
        - **연관 상품 추천**
            - 사용자가 상품을 구매한 후 구매 상품의 특징에 맞는 주변 기기를 추천한다.
                - 상품의 정보를 Server에 전송한 뒤, 사용 목적을 구분하여 해당 조건에 맞는 주변 기기 리스트를 받아온다.
                    - 사용목적(태그) → IF or Switch → 해당 조건에 맞는 주변 기기의 태그를 Select → 반환
    - == 상품 관리 ==
        - **쇼핑몰 크롤러**
            - 에누리에서 노트북 및 주변기기 정보 크롤링
            - 리뷰 데이터 크롤링
            - **노트북 에누리 API URL 및 body 필수 값, Postman으로 확인 완료(마우스, 키보드도 동일)**
                - Robot 크롤링 접근 규약
                    
                    https://www.enuri.com/robots.txt
                    
                    ```jsx
                    User-agent: *
                    Disallow: /move/
                    Disallow: /member/
                    Disallow: /my/
                    Disallow: /emoney/
                    Disallow: /estore/
                    Disallow: /member/
                    Disallow: /tour2012/
                    Disallow: /lsv2016/ajax/detail/
                    Disallow: /include/
                    Disallow: /m/include/
                    Disallow: /mobilefirst/emoney/
                    Disallow: /mobilefirst/login/
                    
                    User-agent: Amazonbot
                    Disallow: /
                    
                    User-Agent: MJ12bot
                    Disallow: /
                    
                    User-agent: dotbot
                    Disallow: /
                    
                    User-agent: AhrefsBot
                    Disallow: /
                    
                    User-agent: Baiduspider
                    Disallow: /
                    
                    User-agent: SemrushBot
                    Disallow: /
                    
                    User-agent: PetalBot
                    Disallow: /
                    
                    User-agent: Applebot
                    Disallow: /
                    
                    User-agent: BomboraBot
                    Disallow: /
                    
                    User-agent: Buck
                    Disallow: /
                    
                    User-agent: PetalBot
                    Disallow: /
                    
                    User-agent: SemrushBot
                    Disallow: /
                    
                    User-agent: barkrowler
                    Disallow: /
                    
                    User-agent: DataForSeoBot
                    Disallow: /
                    
                    User-agent: Mediapartners-Google
                    User-agent: Googlebot
                    User-agent: Googlebot-image
                    User-agent: NaverBot
                    User-agent: Yeti
                    User-agent: Daumoa
                    User-agent: Twitterbot
                    User-agent: bingbot
                    
                    Crawl-delay: 1
                    
                    Sitemap: https://www.enuri.com/sitemap_index.xml
                    ```
                    
                    /list/ 없으므로 크롤링 가능?
                    
                
                상품 목록 리스트를 먼저 가져온 뒤
                
                얻어온 modelno를 기반으로 상세 정보, 리뷰 데이터 크롤링
                
                상세 정보를 크롤링 할 시 상품의 성능 정보는 고유 id 값을 기준으로 가져와야 함
                
                <aside>
                👨🏻‍💻 상품 목록 LIST URL
                
                </aside>
                
                POST 요청
                https://www.enuri.com/wide/api/listGoods_v2.jsp
                
                from : list
                device : pc
                category : 0404 ( 노트북(0404), 키보드(071712), 마우스(071025))
                
                tab : 0
                
                isDelivery : 0
                
                isRental : 0
                
                pageNum : 페이지 번호
                
                pageGap : 한번에 나타낼 상품 개수
                
                sort : 6 ( sort은 6 상품평 많은 순 )
                
                <aside>
                👨🏻‍💻 상품 상세 정보 URL
                
                </aside>
                
                GET 요청
                
                [http://www.enuri.com/wide/api/product/prodDesc.jsp?type=S&modelno=](http://www.enuri.com/wide/api/product/prodDesc.jsp?type=S&modelno=91807945)(상품 번호)
                
                <aside>
                👨🏻‍💻 리뷰 URL
                
                </aside>
                
                POST 요청
                
                https://www.enuri.com/wide/api/product/prodReviewList.jsp
                
                body 값
                modelno : 상품 id값
                page : 페이지 번호
                pagesize : 한번에 나타낼 사이즈
                isphoto : 사진유무 (Y/N) N 할 시 이미지 URL NULL로 나옴
                point : 평점에 해당 (0 - 전체, 1, 2, 3, 4, 5)
                
        - **상품 등록(CRUD)**
            - 관리자 계정이 크롤링 데이터 갱신
            - 상품 삭제
        - **상품 리스트 조회**
            - 카테고리 선택하여 검색 또는 검색어를 이용해 검색 지원(가격, 브랜드, 사양 등)
            - 태그를 이용한 검색
            - 제품에 붙여진 태그를 사용해 상품 정보를 나타냄
        - **상품 상세 조회**
            - 상품의 상세 정보를 포함해, 점수, 태그를 출력
            - 선택한 상품과 유사한 상품을 리스트 형태로 출력
        - **장바구니**
            - 장바구니를 통해 상품을 담을 수 있으며, 장바구니 페이지에서 결제 페이지로 이동
        - **위시리스트 (찜 목록)**
            - 찜 목록에 상품 추가(DB저장)
        - **결제 전 페이지 상품 정보 표시**
            - 제품 사진, 상품 명, 가격, 수량
    - == 관리자 페이지 ==
        - 통계 및 분석 대시 보드
        구글 차트 API 사용하여 통계 시각화
        - 회원 관리 / 권한 관리
            - 가입한 모든 회원 목록을 출력
            - 회원의 이름 클릭으로 단일 회원 조회 및 권한 수정 가능
        - 상품 관리
        상품 수정 / 삭제(상품 관리 파트랑 연계)
            - 부적절한 내용의 상품을 관리자는 수정 / 삭제 가능
        - 주문 관리
        **사용자 주문 내역 확인
            - 주문 리스트 출력
        - 공지사항 및 이벤트
            - CK Editor를 사용하여 공지사항, 이벤트 작성 / 수정 / 삭제
        - 고객센터 관리
        고객 문의 및 답변 관리
            - 고객의 문의에 답변 가능, 부적절한 문의는 삭제 가능
    - == 고객 센터 ==
        - **FAQ**
            - Naver Clova Chat Bot을 사용한 문의 응답
            - 최초 진입 시 객관식으로 사용 가능한 기능 소개
                
                1. 객관식 답변을 통해 자주 묻는 질문 응답
                2. 기본 답변을 통해 사용 설명 응답
                3. 링크를 통해 공지사항 페이지 이동
                4. 링크를 통해 1:1 문의 페이지 이동
                
        - **공지사항**
            - 고객센터 페이지에서 바로가기와 챗봇을 통한 진입 제공
        - **1:1 문의 페이지**
            - CK Editor 사용하여 입력 폼 생성
            - 고객센터 페이지에서 바로가기와 챗봇을 통한 진입 제공
            - 사용자가 문의글을 올릴 시
                - 관리자 페이지에서 미답변 목록 확인
                - 댓글 형식으로 답변
                - 답변 완료 시 SMTP를 통한 이메일 알림
        - **상품 문의**
            - 상품 상세 페이지에서 상품 문의 가능
            - 사용자가 문의글을 올릴 시
                - 관리자 페이지에서 미답변 목록 확인
                - 댓글 형식으로 답변
                - 답변 완료 시 SMTP를 통한 이메일 알림
    - == 결제 ==
        - **결제 페이지 구현**
            - 회원 정보 및 배송지를 입력 받을 수 있어야 함
            배송지는 사용자가 등록이 가능하며 필요 시 불러올 수 있어야 함
        - **결제 기능 구현**
            - PortOne API 사용(식별코드,rest api key, res api secret key) , 액세스 토큰
            - 결제창html - > <script>iamport.pament.js  // 결제 정보(비회원이면 결제 불가)
            - 결제 정보 검증(html 위변조 방지) - 중복 결제 및 결제 금액 비교를 통한 검증
            - 결제 취소 정보 검증 - 이미 결제가 취소된 건인지 / 지불한 금액과 취소하려는 금액이 같은지
        - **포인트 및 할인 관리**
            - 기존 가지고 있던 포인트 및 할인 쿠폰 등을 확인하여 총 금액에서 할인해주는 방식
        - **구매 내역 관리**
            - 결제가 완료된 정보들을 바탕으로 구매 내역 리스트 조회
        - **구매 상품 리뷰**
            - 구매 된 상품에 대한 리뷰 작성 / 수정/ 삭제
        - **포인트 적립**
            - AOP를 사용해 포인트 적립(총 결제 금액에 대한 비율로 포인트 적립)
## 2. 기존 사례 분석

**2-1) 다나와**

https://prod.danawa.com/list/?cate=112758&15main_11_02=

![Untitled](https://prod-files-secure.s3.us-west-2.amazonaws.com/9dd2ce73-4c67-42f2-9101-93034af92f16/c317fe68-c23f-4820-9d54-e86ffc4fb839/Untitled.png)

![Untitled](https://prod-files-secure.s3.us-west-2.amazonaws.com/9dd2ce73-4c67-42f2-9101-93034af92f16/e5669b22-6bdf-44bc-9465-3637b495715e/Untitled.png)

- 상품의 성능 정보를 카테고리 별로 나누어 한 줄로 보여준다.
- 통계적으로 사용자가 많이 선택한 옵션을 그래프를 이용해 보여준다.
- 비교할 상품을 선택하여 상품의 정보를 나열해 차이점을 비교하는 VS검색 서비스를 제공한다.
- 노트북의 사용 목적, 브랜드 ,성능으로 카테고리를 나누어 해당하는 상품 리스트를 구매자에게 보여준다.
- 구매자가 상품을 선택하여 찾아 볼 경우 해당 노트북을 찾아 본 다른 고객들의 정보를 이용해 “다른 고객이 함께 본 상품” 과 같은 협업 필터링 추천 방식을 사용한다.

**2-1) 에누리**

https://www.enuri.com/list.jsp?cate=0404

![Untitled](https://prod-files-secure.s3.us-west-2.amazonaws.com/9dd2ce73-4c67-42f2-9101-93034af92f16/1212d2a6-e2c5-4ead-a9c5-ec217d680127/Untitled.png)

- 최근 가격이 하락한 상품을 UI를 통해 보여준다.
- 상품 리스트와 함께 별점을 보여줘 구매한 사용자가 내린 평가를 한눈에 볼 수 있다.
- 상품을 구매자가 고를 시 해당 상품과 유사한 속성을 지닌 상품을 콘텐츠 기반 필터링 방식을 사용하여 추천 상품 항목에서 리스트 형태로 보여준다.

**2-1) 노써치**

https://nosearch.com/

![Untitled](https://prod-files-secure.s3.us-west-2.amazonaws.com/9dd2ce73-4c67-42f2-9101-93034af92f16/c5b256de-bd0e-43bc-8ba7-45aee9d33d1b/Untitled.png)

![Untitled](https://prod-files-secure.s3.us-west-2.amazonaws.com/9dd2ce73-4c67-42f2-9101-93034af92f16/3fdac04e-8013-4c31-8d15-9085fcf5f4f6/Untitled.png)

![Untitled](https://prod-files-secure.s3.us-west-2.amazonaws.com/9dd2ce73-4c67-42f2-9101-93034af92f16/1b957ec7-714d-44b9-a672-e9ccaba14e94/Untitled.png)

- 사용자가 몇 가지 질문에 답하면 개인 맞춤형 노트북 추천하는 맞춤 추천 기능을 제공한다.
- 각 노트북에 대한 정보를 수치로 환산하여 노트북의 부품을 알지 못하더라도 한 눈에 어느 정도의 성능을 가졌는지 알 수 있다.

**2-2) 기존 서비스와의 차별성**

- 상품 정보에 사용자 리뷰에서 추출한 태그를 추가로 부착함으로써 사용자들이 평가한 상품의 특징과 상품 자체의 성능을 한눈에 볼 수 있음
- 리뷰의 긍/부정 감성을 분석하여 사용자에게 제공

## 3. 서비스 타겟

- IT 지식이 부족한 노트북 구매 예정자

## 4. 요구사항 정의서

- 요구사항 정의서
    
    https://docs.google.com/spreadsheets/d/1z_dfM0xr6ijwnJYpWhuQ9Zqjk_-2GSwFGJWtp4Q96MY/edit?usp=sharing
    
    ![Untitled](https://prod-files-secure.s3.us-west-2.amazonaws.com/9dd2ce73-4c67-42f2-9101-93034af92f16/48174394-c240-4914-97d3-0857651a055d/Untitled.png)
    

## 5. 유스케이스 다이어그램

- 유스케이스
    
    https://drive.google.com/file/d/1k3egyKRNcih0w58eBtuUM6-oKvkB-F4g/view?usp=sharing
    
    ![Untitled](https://prod-files-secure.s3.us-west-2.amazonaws.com/9dd2ce73-4c67-42f2-9101-93034af92f16/dc02c1a0-1027-4b5c-b65b-998a7a6cee90/Untitled.png)
    

## 6. UI 설계

- UI
    
    [‣ 피그마 링크](https://www.figma.com/design/E1a94pPHQrppwFpgQtUrRd/4%EC%A1%B0_%ED%94%84%EB%A1%9C%EC%A0%9D%ED%8A%B8-%EC%8B%A4%ED%8C%A8%ED%95%98%EB%A9%B4-%EC%A4%91%EC%9E%A5%EB%B9%84-%ED%83%80%EB%9F%AC%EA%B0%90?node-id=0-1&t=l6Q2XzuRd5jCWjpa-0)
    
    - 이강석(Auth/MyPage)
        
        [UI 설계](https://www.notion.so/22ca6ba2511f49899bfafd7a15245a28?pvs=21)
        
    - 안진원(고객센터)
        
        [UI 설계](https://www.notion.so/a9c87e15577c4b1ebcac77e28dcf6cf9?pvs=21)
        
    - 박재민(결제 시스템)
        
        [UI 설계](https://www.notion.so/316d975480844a2b9ba76f9fe7db313c?pvs=21)
        
    - 이우석(상품 관리)
        
        [UI 설계](https://www.notion.so/86c7c42e0ca441f7aa3060331a0daab9?pvs=21)
        
    - 윤상현(추천 서비스)
        
        [UI 설계](https://www.notion.so/5f5ac3cf6c9048a099d4b3f5921f1684?pvs=21)
        
    - 문종관(관리자 페이지)
        
        [UI 설계](https://www.notion.so/6c6597877a7d4166a3b8d66a5fbc0b6f?pvs=21)
        

## 7. DB 설계

https://docs.google.com/spreadsheets/d/1z_dfM0xr6ijwnJYpWhuQ9Zqjk_-2GSwFGJWtp4Q96MY/edit?usp=sharing

- DB
    
    https://www.erdcloud.com/d/7p56mqH6KjiZGuGQw
    
    ![Untitled](https://prod-files-secure.s3.us-west-2.amazonaws.com/9dd2ce73-4c67-42f2-9101-93034af92f16/56628136-c0d7-4d10-b385-59eb1a6a6093/Untitled.png)
    

## 8. 전체 흐름도(미완성)

- 흐름도

## 9. 개발 환경

- 개발 환경
    - Front : HTML, CSS, JavaScript, JQuery, Bootstrap
    - Back-End : Java 17, Java Spring Boot
    - Library : Thymeleaf-layout-dialect, Lombok, Spring security6, Mybatis,
    - Server : Naver Server(Centos 7.3)
    - DataBase : MySQL(Naver Cloud DB for MySQL : MySQL 8.0.25), Redis
    - PM  :  Discord, Notion, Figma, Google Docs
    - VCS : Github, GitHub Desktop
    - Tool : IntelliJ java, DBeaver
    - API : Naver Clova ChatBot, Naver Clova Sentiment, Google Chart API, PortOne API, KaKao Address API, coolsms API
    
    ![Untitled](https://prod-files-secure.s3.us-west-2.amazonaws.com/9dd2ce73-4c67-42f2-9101-93034af92f16/1071144f-907e-4d99-b1bf-b2854d95a081/Untitled.png)
    

## 10. WBS

- WBS
    
    [WBS](https://www.notion.so/WBS-5ff2c3e8da1a43f0a8a14e3607cef6ea?pvs=21)
    
    ![Untitled](https://prod-files-secure.s3.us-west-2.amazonaws.com/9dd2ce73-4c67-42f2-9101-93034af92f16/336de14a-94aa-47e3-b1ce-16fb11f80e1d/Untitled.png)
    

## 11. 각 팀원의 역할과 작업 분담 사항

- 각 팀원의 역할과 작업 분담 사항
    
    [각 팀원의 역할과 작업 분담 사항](https://www.notion.so/881d487e9e544668adde04829be0568a?pvs=21)
    

## 12. 구글드라이브 주소

 https://drive.google.com/drive/folders/1n50n6hz5mmfrVkMJPNPJB_RxHFW410Kg?usp=sharing
