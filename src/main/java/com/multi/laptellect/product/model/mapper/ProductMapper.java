package com.multi.laptellect.product.model.mapper;

import com.multi.laptellect.product.model.dto.*;
import com.multi.laptellect.product.model.dto.laptop.LaptopSpecDTO;
import org.apache.ibatis.annotations.*;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;

@Mapper
public interface ProductMapper {

    void insertProduct(ProductDTO product); //크롤링 검색 후 상품등록

    int countByProductCode(String productCode); //상품코드 계수

    List<ProductDTO> getAllProducts(@Param("pageSize") int pageSize, @Param("offset") int offset);

    int getTotalProducts();

    LaptopSpecDTO getProductByCode(String productCode);

    List<ProductDTO> getTypeByProduct(int typeNo);

    void updateReferenceCode(ProductDTO productDTO);

    void inputImage(ImageDTO imageDTO);

    void getImage(String referenceCode);

    ProductCategoryDTO findByOptions(@Param("specName") String specName);

   // String findCategorytNo(String options);

   // String findCategoryNoBySpecName(String specName);

    void inputReviewDate(ReviewDTO reviewDTO);

    void insertProductCategory(@Param("categoryCode") String categoryCode, @Param("typeNo") int typeNo, @Param("options") String options);

    int getProductByType(@Param("typeNo") int typeNo);


    List<ProductDTO> findProduct();

    @Select("SELECT COUNT(*) FROM product_spec WHERE product_no = #{ productNo } AND option_value = #{ specValue }")
    int checkSpecExists(@Param("productNo") int productNo, @Param("specValue") String specValue);

    void insertProductSpec(@Param("productNo") int productNo, @Param("specName") String specName, @Param("specValue") String specValue);


   List<SpecDTO> getProductSpec(@Param("productNo") int productNo);

    List<LaptopDetailsDTO> laptopProductDetails(String productNo);


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
}
