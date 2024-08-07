package com.multi.laptellect.config.database;

import com.multi.laptellect.admin.model.dto.AdminDashboardDTO;
import com.multi.laptellect.admin.model.dto.AdminOrderlistDTO;
import com.multi.laptellect.admin.model.dto.AdminReviewDTO;
import com.multi.laptellect.member.model.dto.AddressDTO;
import com.multi.laptellect.member.model.dto.MemberDTO;
import com.multi.laptellect.member.model.dto.PointLogDTO;
import com.multi.laptellect.member.model.dto.SocialDTO;
import com.multi.laptellect.payment.model.dto.*;
import com.multi.laptellect.product.model.dto.*;
import com.multi.laptellect.product.model.dto.laptop.LaptopSpecDTO;
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
        configuration.getTypeAliasRegistry().registerAlias("pageable", org.springframework.data.domain.Pageable.class);


        configuration.getTypeAliasRegistry().registerAlias("paymentpageDTO", com.multi.laptellect.payment.model.dto.PaymentpageDTO.class);
        configuration.getTypeAliasRegistry().registerAlias("orderlistDTO", com.multi.laptellect.payment.model.dto.OrderlistDTO.class);
        configuration.getTypeAliasRegistry().registerAlias("PaymentDTO", com.multi.laptellect.payment.model.dto.PaymentDTO.class);
        configuration.getTypeAliasRegistry().registerAlias("paymentReviewDTO", com.multi.laptellect.payment.model.dto.PaymentReviewDTO.class);
        configuration.getTypeAliasRegistry().registerAlias("paymentpointDTO", com.multi.laptellect.payment.model.dto.PaymentpointDTO.class);
        configuration.getTypeAliasRegistry().registerAlias("paymentProductDTO", com.multi.laptellect.payment.model.dto.PaymentProductDTO.class);


        configuration.getTypeAliasRegistry().registerAlias("adminReviewDTO", AdminReviewDTO.class);
        configuration.getTypeAliasRegistry().registerAlias("adminOrderlistDTO", AdminOrderlistDTO.class);
        configuration.getTypeAliasRegistry().registerAlias("adminDashboardDTO", AdminDashboardDTO.class);

        configuration.getTypeAliasRegistry().registerAlias("memberDTO", MemberDTO.class);
        configuration.getTypeAliasRegistry().registerAlias("pointLogDTO", PointLogDTO.class);
        configuration.getTypeAliasRegistry().registerAlias("addressDTO", AddressDTO.class);
        configuration.getTypeAliasRegistry().registerAlias("productDTO", ProductDTO.class);
        configuration.getTypeAliasRegistry().registerAlias("productCategoryDTO", ProductCategoryDTO.class);
        configuration.getTypeAliasRegistry().registerAlias("productSpecDTO", ProductSpecDTO.class);
        configuration.getTypeAliasRegistry().registerAlias("imageDTO", ImageDTO.class);
        configuration.getTypeAliasRegistry().registerAlias("laptopSpecDTO", LaptopSpecDTO.class);
        configuration.getTypeAliasRegistry().registerAlias("productSpecDTO", ProductSpecDTO.class);
        configuration.getTypeAliasRegistry().registerAlias("laptopDetailsDTO", LaptopDetailsDTO.class);
        configuration.getTypeAliasRegistry().registerAlias("reviewDTO", ReviewDTO.class);
        configuration.getTypeAliasRegistry().registerAlias("specDTO", SpecDTO.class);


        configuration.getTypeAliasRegistry().registerAlias("socialDTO", SocialDTO.class);
        configuration.getTypeAliasRegistry().registerAlias("wishlistDTO", WishlistDTO.class);




        configuration.getTypeAliasRegistry().registerAlias("sentimentDTO", com.multi.laptellect.recommend.clovaapi.model.dto.SentimentDTO.class);
        configuration.getTypeAliasRegistry().registerAlias("RecommendProductDTO", com.multi.laptellect.recommend.laptop.model.dto.RecommendProductDTO.class);
        configuration.getTypeAliasRegistry().registerAlias("tagDTO", com.multi.laptellect.recommend.reviewtag.model.dto.TagDTO.class);
        configuration.getTypeAliasRegistry().registerAlias("ProductDTO2", com.multi.laptellect.recommend.txttag.model.dto.ProductDTO2.class);
        configuration.getTypeAliasRegistry().registerAlias("TaggDTO", com.multi.laptellect.recommend.txttag.model.dto.TaggDTO.class);




        factoryBean.setConfiguration(configuration); // 팩토리 빈에 세팅

        return factoryBean.getObject();


    }


}
