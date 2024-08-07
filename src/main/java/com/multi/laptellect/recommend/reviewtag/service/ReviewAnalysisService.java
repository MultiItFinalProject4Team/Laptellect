package com.multi.laptellect.recommend.reviewtag.service;

import com.multi.laptellect.recommend.reviewtag.model.dao.TagDAO;
import com.multi.laptellect.recommend.reviewtag.model.dto.TagDTO;
import kr.co.shineware.nlp.komoran.constant.DEFAULT_MODEL;
import kr.co.shineware.nlp.komoran.core.Komoran;
import kr.co.shineware.nlp.komoran.model.KomoranResult;
import org.apache.spark.ml.feature.CountVectorizer;
import org.apache.spark.ml.feature.CountVectorizerModel;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Encoders;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ReviewAnalysisService {

    @Autowired
    private TagDAO tagDAO;

    private final Komoran komoran;
    private Set<String> stopWords;

    public ReviewAnalysisService() {
        this.komoran = new Komoran(DEFAULT_MODEL.FULL);
        this.stopWords = new HashSet<>(Arrays.asList("은", "는", "이", "가", "을", "를", "에", "에서", "으로", "로", "의"));
    }

    public List<String> getReviewsFromDB() {
        return tagDAO.getAllReviews();
    }

    public List<String> analyzeReviews(List<String> reviews) {
        List<String> analyzedReviews = new ArrayList<>();
        for (String review : reviews) {
            KomoranResult result = komoran.analyze(review);
            List<String> words = result.getNouns().stream()
                    .filter(word -> !stopWords.contains(word))
                    .collect(Collectors.toList());
            analyzedReviews.addAll(words);
        }
        return analyzedReviews;
    }

    public List<TagDTO> generateTags(List<String> analyzedReviews) {
        SparkSession spark = SparkSession.builder().appName("TagGeneration").getOrCreate();
        Dataset<Row> wordsDF = spark.createDataset(analyzedReviews, Encoders.STRING()).toDF("word");
        CountVectorizer vectorizer = new CountVectorizer()
                .setInputCol("word")
                .setOutputCol("features")
                .setVocabSize(10)
                .setMinDF(2);
        CountVectorizerModel model = vectorizer.fit(wordsDF);
        String[] vocabulary = model.vocabulary();
        List<TagDTO> tags = new ArrayList<>();
        for (String word : vocabulary) {
            TagDTO tag = new TagDTO();
            tag.setTag_data(word);
            tag.setTag_etc("Generated from CountVectorizer");
            tags.add(tag);
        }
        spark.stop();
        return tags;
    }

    @Transactional
    public void saveTags(List<TagDTO> tags) {
        for (TagDTO tag : tags) {
            tagDAO.insertTag(tag);
        }
    }

    public void performFullAnalysis() {
        List<String> reviews = getReviewsFromDB();
        List<String> analyzedReviews = analyzeReviews(reviews);
        List<TagDTO> tags = generateTags(analyzedReviews);
        saveTags(tags);
    }
}