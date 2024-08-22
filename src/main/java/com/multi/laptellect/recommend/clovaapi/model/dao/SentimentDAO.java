package com.multi.laptellect.recommend.clovaapi.model.dao;

import com.multi.laptellect.payment.model.dto.PaymentReviewDTO;
import com.multi.laptellect.product.model.dto.ReviewDTO;
import com.multi.laptellect.recommend.clovaapi.model.dto.SentimentDTO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SentimentDAO {
    @Insert("INSERT INTO laptop_sentiment(product_no, sentiment_positive, sentiment_denial, sentiment_neutrality) " +
            "VALUES (#{ productNo }, #{ sentimentPositive }, #{ sentimentDenial }, #{ sentimentNeutrality }) " +
            "ON DUPLICATE KEY UPDATE " +
            "sentiment_positive = sentiment_positive + #{sentimentPositive}, " +
            "sentiment_denial = sentiment_denial + #{sentimentDenial}, " +
            "sentiment_neutrality = sentiment_neutrality + #{sentimentNeutrality}")
    void insertSentiment(SentimentDTO sentimentDTO);

    @Select("SELECT product_no, content FROM payment_product_reviews " +
            "WHERE product_no NOT IN (SELECT product_no FROM laptop_sentiment) " +
            "UNION ALL " +
            "SELECT product_no, content FROM review " +
            "WHERE product_no NOT IN (SELECT product_no FROM laptop_sentiment)")
    List<ReviewDTO> getUnanalyzedReviews();

    @Select("SELECT product_no, content FROM payment_product_reviews " +
            "WHERE product_no NOT IN (SELECT product_no FROM laptop_sentiment)")
    List<PaymentReviewDTO> getUnanalyzedPaymentReviews();

    @Select("SELECT * FROM laptop_sentiment WHERE product_no = #{productNo}")
    SentimentDTO getSentimentByProductNo(@Param("productNo") int productNo);

}