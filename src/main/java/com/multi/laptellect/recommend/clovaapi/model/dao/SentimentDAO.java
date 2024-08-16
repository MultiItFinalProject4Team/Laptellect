package com.multi.laptellect.recommend.clovaapi.model.dao;

import com.multi.laptellect.recommend.clovaapi.model.dto.SentimentDTO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SentimentDAO {
    @Insert("INSERT IGNORE INTO laptop_sentiment(product_no, sentiment_positive, sentiment_denial, sentiment_neutrality)" +
            " VALUES (#{product_no}, #{sentiment_positive}, #{sentiment_denial}, #{sentiment_neutrality})")
    void insertSentiment(SentimentDTO sentimentDTO); //감정분석 결과 저장

}
