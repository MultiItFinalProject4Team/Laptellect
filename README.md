# 💻 Laptellect(노트북 추천 웹 서비스)
##### Java Spring Boot를 사용한 노트북 추천 및 구매 쇼핑몰 개발

### 📌PART 1. 프로젝트 소개 <hr>
    
#### ❗ 프로젝트 개요
&nbsp;추천 시스템은 인터넷의 발전에 따라 상품 구매 및 선호에 대한 사용자의 피드백을 얻기 쉬워지면서 이러한 피드백을 바탕으로 사용자의 상품 구매를 유도하여 사용자와 기업 모두 이득을 볼 수 있게 만들어 준다. 이 프로젝트는 사용자의 정보와 리뷰 데이터를 사용해 노트북을 추천하는 웹사이트를 설계 및 구현하는 프로젝트다. 추천 기능은 사용자가 질의 응답을 통하여 입력한 정보를 사용해 추천하는 방법을 사용한다. 또한 상품의 태그는 실제 상품의 정보를 내부적으로 처리하여 생성하는 하며 쇼핑몰의 리뷰를 감성 분석하여 사용자에게 출력한다.

#### 💡 기술적 차별성
- 큐레이션을 통해 사용자에게 맞춤형 추천 서비스를 제공한다
- 노트북에 태그를 부착함으로써 상품의 특징을 쉽게 알 수 있다.
- 리뷰의 감성을 분석하여 상품에 대한 평가를 빠르게 파악할 수 있다.

#### 💹 기대효과
- 정보 탐색에 소요되는 시간 단축 : 다른 구매자들의 평가와 제품 정보를 태그와 감성 분석 결과를 통해 한눈에 확인 가능, 원하는 정보를 빠르게 탐색할 수 있음.
- 과소비 방지 : 사전 지식이 부족한 소비자들에게 필요한 용도와 작업 수준에 맞는 상품을 큐레이션을 통해 추천하여, 불필요한 성능의 제품 구매를 방지할 수 있음.
- 판매자의 매출 증진 기여 : 구매자의 성향에 맞춘 다양한 상품을 노출 시키고 구매로 연결하여, 판매자의 매출 증대에 기여할 수 있음.

#### 🎯 서비스 타겟
- IT 지식이 부족한 노트북 구매 예정자

#### 👤 구성원 및 역할
![구성원_및_역할](https://github.com/user-attachments/assets/0c386b5d-2e97-4e54-b1a9-0f6247aa98cf)

#### 🗄️ WBS
![image](https://github.com/user-attachments/assets/a375f43c-6f1c-44c9-9f40-303cd0204877)

### 📌PART 2. 시스템 설계 <hr>

#### 🛠️ 개발 환경
![시스템 구성요소](https://github.com/user-attachments/assets/b7641481-49a6-4758-aa15-fd66534d3417)

#### ⚙️ 시스템 구성도
![시스템 흐름도](https://github.com/user-attachments/assets/dfa5e030-bc15-4aa3-a158-e1b3d83068c8)

#### 📡 요약 흐름도
![요약 흐름도](https://github.com/user-attachments/assets/add04954-2b4e-45af-82b7-cd93417ba627)

#### 💾 ERD 설계
![4조_Laptellect(public)](https://github.com/user-attachments/assets/28ba6e41-7a22-4fda-b997-216a0e0a2b28)

#### 🗂️ [구현 내용](https://github.com/MultiItFinalProject4Team/Laptellect/wiki)
- **이강석** : AUTH, LOG, UI, Redis, AWS EC2 [(Link)](https://github.com/MultiItFinalProject4Team/Laptellect/wiki)
- **안진원** : customer , Object Storage [(Link)](https://github.com/MultiItFinalProject4Team/Laptellect/wiki)
- **이우석** : Product Management [(Link)](https://github.com/MultiItFinalProject4Team/Laptellect/wiki)
- **박재민** : Payment, Point, Review, Admin dashboard [(Link)](https://github.com/MultiItFinalProject4Team/Laptellect/wiki)
- **윤상현** : Recommend, Laptop Tag [(Link)](https://github.com/MultiItFinalProject4Team/Laptellect/wiki)

### 📌PART 3. 시연영상 <hr>
[여기를 클릭하여 동영상 보기](https://drive.google.com/file/d/1e9GmwZhhw9rhnQg4E1TmejJ2uKH8-_SY/view?usp=sharing)
