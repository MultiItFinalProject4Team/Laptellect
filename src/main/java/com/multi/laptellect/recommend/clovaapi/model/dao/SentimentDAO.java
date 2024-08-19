package com.multi.laptellect.recommend.clovaapi.model.dao;

import com.multi.laptellect.product.model.dto.ReviewDTO;
import com.multi.laptellect.recommend.clovaapi.model.dto.SentimentDTO;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface SentimentDAO {
    @Insert("INSERT INTO laptop_sentiment(product_no, sentiment_positive, sentiment_denial, sentiment_neutrality) " +
            "VALUES (#{product_no}, #{sentiment_positive}, #{sentiment_denial}, #{sentiment_neutrality}) " +
            "ON DUPLICATE KEY UPDATE " +
            "sentiment_positive = sentiment_positive + #{sentiment_positive}, " +
            "sentiment_denial = sentiment_denial + #{sentiment_denial}, " +
            "sentiment_neutrality = sentiment_neutrality + #{sentiment_neutrality}")
    void insertSentiment(SentimentDTO sentimentDTO);

    @Select("SELECT product_no, content FROM review " +
            "WHERE product_no NOT IN (SELECT product_no FROM laptop_sentiment)")
    List<ReviewDTO> getUnanalyzedReviews();

    @Select("SELECT * FROM laptop_sentiment WHERE product_no = #{productNo}")
    SentimentDTO getSentimentByProductNo(@Param("productNo") int productNo);

}