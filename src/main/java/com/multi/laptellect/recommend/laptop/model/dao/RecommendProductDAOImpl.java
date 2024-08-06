package com.multi.laptellect.recommend.laptop.model.dao;

import com.multi.laptellect.recommend.laptop.model.dto.RecommendProductDTO;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public class RecommendProductDAOImpl implements RecommendProductDAO {

    @Autowired
    private SqlSession sqlSession;

    @Override
    public List<RecommendProductDTO> getRecommendedProducts(Map<String, Object> criteria) {
        return sqlSession.selectList("recommendProduct.getRecommendedProducts", criteria);
    }
}
