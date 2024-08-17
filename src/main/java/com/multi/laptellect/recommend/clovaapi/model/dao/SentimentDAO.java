package com.multi.laptellect.recommend.clovaapi.model.dao;

import com.multi.laptellect.product.model.dto.ReviewDTO;
import com.multi.laptellect.recommend.clovaapi.model.dto.SentimentDTO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SentimentDAO {
    @Insert("INSERT IGNORE INTO laptop_sentiment(product_no, sentiment_positive, sentiment_denial, sentiment_neutrality)" +
            " VALUES (#{product_no}, #{sentiment_positive}, #{sentiment_denial}, #{sentiment_neutrality})")
    void insertSentiment(SentimentDTO sentimentDTO); //감정분석 결과 저장
    @Select("SELECT product_no, content FROM review " +
            "WHERE product_no NOT IN (SELECT product_no FROM laptop_sentiment)")
    List<ReviewDTO> getUnanalyzedReviews();
}
