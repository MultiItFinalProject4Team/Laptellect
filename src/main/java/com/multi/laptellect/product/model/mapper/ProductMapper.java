package com.multi.laptellect.product.model.mapper;

import com.multi.laptellect.product.model.dto.ImageDTO;
import com.multi.laptellect.product.model.dto.ProductCategoryDTO;
import com.multi.laptellect.product.model.dto.ProductDTO;
import com.multi.laptellect.product.model.dto.laptop.LaptopSpecDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ProductMapper {

    void insertProduct(ProductDTO product); //크롤링 검색 후 상품등록

    int countByProductCode(String productCode); //상품코드 계수

    List<ProductDTO> getAllProducts();

    LaptopSpecDTO getProductByCode(String productCode);

    List<ProductDTO> getTypeByProduct(int typeNo);

    void updateReferenceCode(ProductDTO productDTO);

    void inputImage(ImageDTO imageDTO);

    void getImage(String referenceCode);

    ProductCategoryDTO findByOptions(String s);

    String findCategorytNo(String options);


    int insertProductCategory(@Param("typeNo") int typeNo, @Param("options") String options);

    int insertProductSpec(@Param("productNo") int productNo, @Param("categoryNo") int categoryNo, @Param("optionValue") String optionValue);

    List<ProductDTO> findProduct();

    int checkSpecExists(@Param("productNo") int productNo, @Param("categoryNo") int categoryNo, @Param("options") String options);





}
