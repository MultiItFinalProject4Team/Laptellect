//package com.multi.laptellect.recommend.reviewtag.service;
//
//
//import com.multi.laptellect.recommend.reviewtag.model.dao.TagDAO;
//import com.multi.laptellect.recommend.reviewtag.model.dto.TagDTO;
//import kr.co.shineware.nlp.komoran.constant.DEFAULT_MODEL;
//import kr.co.shineware.nlp.komoran.core.Komoran;
//import kr.co.shineware.nlp.komoran.model.KomoranResult;
//import org.openqa.selenium.By;
//import org.openqa.selenium.WebDriver;
//import org.openqa.selenium.WebElement;
//import org.openqa.selenium.chrome.ChromeDriver;
//import org.apache.spark.ml.feature.CountVectorizer;
//import org.apache.spark.ml.feature.CountVectorizerModel;
//import org.apache.spark.sql.Dataset;
//import org.apache.spark.sql.Row;
//import org.apache.spark.sql.SparkSession;
//import org.apache.spark.sql.Encoders;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.*;
//import java.util.stream.Collectors;
//
//@Service
//public class ReviewAnalysisService {
//
//    @Autowired
//    private TagDAO tagDAO;
//
//    private final WebDriver driver; // Selenium WebDriver
//    private final Komoran komoran; // 형태소 분석기
//    private Set<String> stopWords; // 불용어 집합
//
//    public ReviewAnalysisService() {
//        System.setProperty("webdriver.chrome.driver", "/path/to/chromedriver");
//        this.driver = new ChromeDriver();
//        this.komoran = new Komoran(DEFAULT_MODEL.FULL);
//        this.stopWords = new HashSet<>(Arrays.asList("은", "는", "이", "가", "을", "를", "에", "에서", "으로", "로", "의")); // 불용어 추가
//    }
//
//    public List<String> crawlReviews(String url) {
//        // 크롤링 코드
//        List<String> reviews = new ArrayList<>();
//        driver.get(url); // 지정한 URL 접속
//
//        List<WebElement> reviewElements = driver.findElements(By.cssSelector(".review-content"));
//        for (WebElement reviewElement : reviewElements) {
//            reviews.add(reviewElement.getText()); // 리뷰 텍스트 추가한 것
//        }
//
//        return reviews;
//
//    }
//
//
//    public List<String> analyzeReviews(List<String> reviews) {
//        List<String> analyzedReviews = new ArrayList<>();
//        for (String review : reviews) {
//            KomoranResult result = komoran.analyze(review); // 형태소 분석 수행
//            List<String> words = result.getNouns().stream()
//                    .filter(word -> !stopWords.contains(word)) // 불용어 제거
//                    .collect(Collectors.toList());
//            analyzedReviews.addAll(words);
//
//
//}
//        return analyzedReviews;
//    }
//
//    public List<TagDTO> generateTags(List<String> analyzedReviews) {
//        // Spark 세션 생성
//        SparkSession spark = SparkSession.builder().appName("TagGeneration").getOrCreate();
//
//        // 분석된 리뷰 단어들을 Spark DataFrame으로 변환
//        Dataset<Row> wordsDF = spark.createDataset(analyzedReviews, Encoders.STRING()).toDF("word");
//
//        // CountVectorizer 설정
//        CountVectorizer vectorizer = new CountVectorizer()
//                .setInputCol("word")
//                .setOutputCol("features")
//                .setVocabSize(10) // 상위 10개 단어만 선택
//                .setMinDF(2); // 최소 2번 이상 등장한 단어만 고려
//
//        // CountVectorizer 모델 생성 및 적용
//        CountVectorizerModel model = vectorizer.fit(wordsDF);
//        String[] vocabulary = model.vocabulary();
//
//        // 추출된 단어들을 TagDTO 객체로 변환
//        List<TagDTO> tags = new ArrayList<>();
//        for (String word : vocabulary) {
//            TagDTO tag = new TagDTO();
//            tag.setTag_data(word);
//            tag.setTag_etc("Generated from CountVectorizer");
//            tags.add(tag);
//        }
//
//        spark.stop(); // Spark 세션 종료
//        return tags;
//    }
//
//    @Transactional // 트랜잭션 처리를 위한 어노테이션
//    public void saveTags(List<TagDTO> tags) {
//        for (TagDTO tag : tags) {
//            tagDAO.insertTag(tag); // 각 태그를 데이터베이스에 저장
//        }
//    }
//
//    public void performFullAnalysis(String url) {
//        List<String> reviews = crawlReviews(url); // 리뷰 크롤링
//        List<String> analyzedReviews = analyzeReviews(reviews); // 리뷰 분석
//        List<TagDTO> tags = generateTags(analyzedReviews); // 태그 생성
//        saveTags(tags); // 태그 저장
//    }
//}