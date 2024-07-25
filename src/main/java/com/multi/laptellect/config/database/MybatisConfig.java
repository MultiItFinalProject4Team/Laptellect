package com.multi.laptellect.config.database;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.type.JdbcType;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

@Configuration
@EnableTransactionManagement
@MapperScan(basePackages = "com.multi.laptellect", annotationClass = Mapper.class)
public class MybatisConfig {
    @Bean
    public DataSourceTransactionManager transactionManager(DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    @Bean
    public SqlSessionFactory sqlSessoinFactory(DataSource datasource) throws Exception{
        SqlSessionFactoryBean factoryBean  = new SqlSessionFactoryBean();
        Resource[] res = new PathMatchingResourcePatternResolver().getResources("classpath:mappers/**/*.xml"); // 매퍼 경로 설정

        factoryBean.setMapperLocations(res);

        factoryBean.setDataSource(datasource);

        org.apache.ibatis.session.Configuration configuration = new  org.apache.ibatis.session.Configuration(); // xml 파일 대체
        configuration.setJdbcTypeForNull(JdbcType.NULL); // Null처리
        configuration.setMapUnderscoreToCamelCase(true); // 카멜 케이스 자동 변환

        // Type Aliases 설정
//        configuration.getTypeAliasRegistry().registerAlias("memberDTO", com.multi.mini.member.model.dto.MemberDTO.class);
//        configuration.getTypeAliasRegistry().registerAlias("boardDTO", com.multi.mini.community.model.dto.BoardDTO.class);
        configuration.getTypeAliasRegistry().registerAlias("memberDTO", com.multi.laptellect.member.model.dto.MemberDTO.class);
        configuration.getTypeAliasRegistry().registerAlias("kakaoDTO", com.multi.laptellect.member.model.dto.KakaoDTO.class);


        factoryBean.setConfiguration(configuration); // 팩토리 빈에 세팅

        return factoryBean.getObject();


    }


}
