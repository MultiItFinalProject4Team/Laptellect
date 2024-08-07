package com.multi.laptellect.product.service;

import com.multi.laptellect.product.model.dto.*;
import com.multi.laptellect.product.model.mapper.ProductMapper;
import com.multi.laptellect.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * The type Product service.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductMapper productMapper;


    private final CrawlingService crawlingService;

    @Override
    @Transactional
    public void saveProductsToDB(List<ProductDTO> productList, int typeNo) throws Exception {
        List<ProductDTO> productDTOList = createProductDTOList(productList, typeNo);
        try {
            for (ProductDTO productDTO : productDTOList) {
                processProduct(productDTO);
            }
        } catch (Exception e) {
            log.error("제품 저장 중 오류 발생", e);
        }
    }

    private List<ProductDTO> createProductDTOList(List<ProductDTO> productList, int typeNo) {
        List<ProductDTO> productDTOList = new ArrayList<>();

        for (ProductDTO ProductDTO : productList) {
            ProductDTO productDTO = new ProductDTO();
            productDTO.setProductName(ProductDTO.getProductName());
            productDTO.setPrice(ProductDTO.getPrice());
            productDTO.setProductCode(ProductDTO.getProductCode());
            productDTO.setTypeNo(typeNo);

            productDTOList.add(productDTO);
            log.info("ProductDTO Created: {}", productDTO);
        }
        return productDTOList;
    }

    private void processProduct(ProductDTO productDTO) throws Exception {
        String image = crawlingService.getProductImageUrl(productDTO.getProductCode());
        int count = productMapper.countByProductCode(productDTO.getProductNo());

        if (count == 0) {
            productMapper.insertProduct(productDTO);
            String code = "P" + productDTO.getProductNo();
            productDTO.setReferenceCode(code);
            productMapper.updateReferenceCode(productDTO);

            processImage(productDTO, image);
        } else {
            log.info("제품코드는: " + productDTO.getProductCode() + " 입니다.");
        }
    }

    private void processImage(ProductDTO productDTO, String image) throws Exception {
        ImageDTO imageDTO = new ImageDTO();
        String url = "http:" + image;
        String filePath = "src/main/resources/static/img/product";
        String uuid = UUID.randomUUID().toString();
        String uploadName = uuid + ".jpg";

        crawlingService.downloadImage(url, filePath, uploadName);

        log.info("이미지 명 확인 = {}", uploadName);
        log.info("url 확인 = {}", url);
        log.info("저장위치 확인 = {}", filePath);

        imageDTO.setOriginName(url);
        imageDTO.setReferenceCode(productDTO.getReferenceCode());
        imageDTO.setUploadName(uploadName);

        productMapper.inputImage(imageDTO);
    }


    @Override
    public List<LaptopDetailsDTO> getLaptopProductDetails(int productNo) {

        log.info("프로덕트넘버값 확인 = {}", productNo);

        List<LaptopDetailsDTO> laptopDetailsDTO = productMapper.laptopProductDetails(productNo);
        log.info("laptopDetailsDTO = {} ", laptopDetailsDTO);

        return laptopDetailsDTO;
    }

    @Override
    @Transactional
    public int processWishlist(List<Integer> productNoList) throws Exception {
        WishlistDTO wishListDTO = new WishlistDTO();
        int memberNo = SecurityUtil.getUserNo();
        int result = 0;
        wishListDTO.setMemberNo(memberNo);

        for (int productNo : productNoList) {
            wishListDTO.setProductNo(productNo);
            log.debug("위시 리스트 추가 시작 = {}, {}", productNo, memberNo);

            WishlistDTO wishList = productMapper.findWishlist(wishListDTO);
            if (wishList != null) {
                log.info("위시 리스트 중복 = {}", productNo);

                int wishlistNo = wishList.getWishlistNo();

                log.debug("위시리스트 삭제 시작 = {}", wishlistNo);
                result = productMapper.deleteWishlist(wishlistNo);
                if (result > 0) {
                    log.info("위시리스트 삭제 성공 = {}", result);
                    if (productNoList.size() == 1) result = 2;
                }
            } else {
                productMapper.insertWishlist(wishListDTO);
                log.info("위시 리스트 등록 성공 = {}", productNo);
                result = 1;
            }
        }

        return result;
    }

    @Override
    public Page<WishlistDTO> getWishlist(Pageable pageable) throws Exception {
        int memberNo = SecurityUtil.getUserNo();

        log.debug("위시리스트 조회 시작 = {}", memberNo);
        int total = productMapper.countAllWishlistByMemberNo(memberNo);
        ArrayList<WishlistDTO> wishlist = productMapper.findAllWishlistByMemberNo(memberNo, pageable);

        log.info("위시리스트 조회 성공 = {}", wishlist);

        return new PageImpl<>(wishlist, pageable, total);
    }

    @Override
    public ProductDTO findProductByProductNo(String productNo) throws Exception {
        return productMapper.findProductByProductNo(productNo);
    }



    //상품 전체 조회
    @Override
    @Transactional
    public List<ProductDTO> getStoredProducts(Integer typeNo,int pageNumber, int pageSize) {

        int offset = (pageNumber - 1) * pageSize;
        if (typeNo == null) {
            return productMapper.getAllProducts(pageSize, offset);
        } else {
            return productMapper.getProductsByType(typeNo, pageSize, offset);
        }
    }

    @Override
    public int getTotalProducts() {
        return productMapper.getTotalProducts();
    }

    @Override
    public List<ProductDTO> getTypeByProduct(int typeNo) {

        List<ProductDTO> productDTOList = productMapper.getTypeByProduct(typeNo);


        return productDTOList;
    }

    @Override
    public int getProductByType(int typeNo) {

        return productMapper.getProductByType(typeNo);

    }

    @Override
    public List<SpecDTO> getProductSpec(int productNo) {


        List<SpecDTO> productSpec = productMapper.getProductSpec(productNo);

        log.info("상품스펙 확인합니다. = {}", productSpec);

        return productSpec;

    }
    @Override
    public List<SpecDTO> filterSpecs(int productNo, Set<String> neededOptions) {
        List<SpecDTO> specs = getProductSpec(productNo);
        return specs.stream()
                .filter(spec -> neededOptions.contains(spec.getOptions()))
                .collect(Collectors.toList());
    }

}