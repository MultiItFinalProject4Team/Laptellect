package com.multi.laptellect.product.model.mapper;

import com.multi.laptellect.product.model.dto.*;
import org.apache.ibatis.annotations.*;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Mapper
public interface ProductMapper {

    void insertProduct(ProductDTO product); //크롤링 검색 후 상품등록

    int countByProductCode(int productNo); //상품코드 계수


    List<ProductDTO> getProductsByType(@Param("typeNo")Integer typeNo);


    int getTotalProducts();

    List<ProductDTO> getTypeByProduct(int typeNo);

    void updateReferenceCode(ProductDTO productDTO);

    void inputImage(ImageDTO imageDTO);

    ProductCategoryDTO findByOptions(@Param("specName") String specName);

    void inputReviewDate(ReviewDTO reviewDTO);

    void insertProductCategory(@Param("categoryCode") String categoryCode, @Param("typeNo") int typeNo, @Param("options") String options);

    int getProductByType(@Param("typeNo") int typeNo);

    @Select("SELECT * FROM product WHERE type_no = #{ typeNo }")
    List<ProductDTO> findProductsByType(@Param("typeNo") int typeNo);

    List<ProductDTO> getReviewRequired ();


    int checkSpecExists(@Param("productNo") int productNo,@Param("category")String category,@Param("specValue") String specValue);

    void insertProductSpec(@Param("productNo") int productNo, @Param("specName") String specName, @Param("specValue") String specValue);


   List<SpecDTO> getProductSpec(@Param("productNo") int productNo);

    List<LaptopDetailsDTO> productDetails(int productNo);



    @Insert("INSERT INTO wishlist (product_no, member_no) VALUES (#{ productNo }, #{ memberNo });")
    int insertWishlist(WishlistDTO wishListDTO);

    @Select("SELECT * FROM wishlist WHERE product_no = #{ productNo } AND member_no = #{ memberNo }")
    WishlistDTO findWishlist(WishlistDTO wishListDTO);

    @Delete("DELETE FROM wishlist WHERE wishlist_no = #{ wishlistNo }")
    int deleteWishlist(int wishlistNo);

    ArrayList<WishlistDTO> findAllWishlistByMemberNo(@Param("memberNo") int memberNo, @Param("pageable") Pageable pageable);

    @Select("SELECT COUNT(*) FROM wishlist WHERE member_no = #{ memberNo }")
    int countAllWishlistByMemberNo(int memberNo);
    
    ProductDTO findProductByProductNo(String productNo);

    List<SpecDTO> findProductSpecByProductNo(@Param("productNo") int productNo, @Param("neededOptions") Set<String> neededOptions);

    List<ProductDTO> findByNameSearch(@Param("keyword") String keyword, @Param("typeNo") int typeNo);

    @Select("SELECT product_no FROM wishlist WHERE member_no = #{ member_no }")
    ArrayList<Integer> findAllWishlistString(int memberNo);
}
