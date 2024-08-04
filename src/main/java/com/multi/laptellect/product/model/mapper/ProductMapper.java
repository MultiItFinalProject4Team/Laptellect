package com.multi.laptellect.product.model.mapper;

import com.multi.laptellect.product.model.dto.*;
import com.multi.laptellect.product.model.dto.laptop.LaptopSpecDTO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

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

    ProductCategoryDTO findByOptions(String s);

   // String findCategorytNo(String options);

   // String findCategoryNoBySpecName(String specName);

    void inputReviewDate(ReviewDTO reviewDTO);

    int insertProductCategory(@Param("typeNo") int typeNo, @Param("options") String options);


    List<ProductDTO> findProduct();

    @Select("SELECT COUNT(*) FROM product_spec WHERE product_no = #{ productNo } AND option_value = #{ specValue }")
    int checkSpecExists(@Param("productNo") int productNo, @Param("specValue") String specValue);

    void insertProductSpec(@Param("productNo") int productNo, @Param("specName") String specName, @Param("specValue") String specValue);



    List<LaptopDetailsDTO> laptopProductDetails(String productCode);


    @Insert("INSERT INTO wishlist (product_no, member_no) VALUES (#{ productNo }, #{ memberNo });")
    int insertWishlist(WishListDTO wishListDTO);

    @Select("SELECT * FROM wishlist WHERE product_no = #{ productNo } AND member_no = #{ memberNo }")
    WishListDTO findWishlist(WishListDTO wishListDTO);
}
