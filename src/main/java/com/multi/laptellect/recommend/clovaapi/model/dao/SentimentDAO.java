package com.multi.laptellect.recommend.clovaapi.model.dao;

import com.multi.laptellect.recommend.clovaapi.model.dto.SentimentDTO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SentimentDAO {
    void insertSentiment(SentimentDTO sentimentDTO);
    // 새로운 감정분석 결과를 데이터베이스에 삽입
}
