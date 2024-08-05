package com.multi.laptellect.product.service;

import com.multi.laptellect.product.model.dto.*;
import com.multi.laptellect.product.model.dto.laptop.LaptopSpecDTO;
import com.multi.laptellect.product.model.mapper.ProductMapper;
import com.multi.laptellect.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductMapper productMapper;


    private final CrawlingService crawlingService;

    @Override
    @Transactional
    public void saveProductsToDB(List<ProductDTO> productList, int typeNo) throws Exception {

        List<ProductDTO> productDTOList = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();

        try {


            for (ProductDTO ProductDTO : productList) {
                ProductDTO productDTO = new ProductDTO();
                productDTO.setProductName(ProductDTO.getProductName());
                productDTO.setPrice(ProductDTO.getPrice());
                productDTO.setProductCode(ProductDTO.getProductCode());
                productDTO.setCreatedAt(Timestamp.valueOf(now));
                productDTO.setTypeNo(typeNo);

                productDTOList.add(productDTO);

                log.info("ProductDTO Created: {}", productDTO);
            }

            // 중복 확인 및 데이터베이스 삽입
            for (ProductDTO productDTO : productDTOList) {

                String image = crawlingService.getProductImageUrl(productDTO.getProductCode());

                //코드 계수 확인 쿼리 count에 담아주기
                int count = productMapper.countByProductCode(productDTO.getProductCode());

                //해당 코드 가 0(없을시) 코드 실행
                if (count == 0) {

                    //크롤링데이터 저장 코드
                    productMapper.insertProduct(productDTO);

                    //자동으로 들어갈 레퍼런트코드 작성 ex P1, P2 ...
                    String code = "P" + productDTO.getProductNo();

                    //DTO에 담기
                    productDTO.setReferenceCode(code);
                    // 레퍼런트코드 업데이트
                    productMapper.updateReferenceCode(productDTO);

                    //이미지 처리
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
                    imageDTO.setReferenceCode(code);
                    imageDTO.setUploadName(uploadName);

                    productMapper.inputImage(imageDTO);


                } else {
                    log.info("제품코드는: " + productDTO.getProductCode() + " 입니다.");
                }
            }

        } catch (Exception e) {
            log.error("제품 저장 중 오류 발생", e);

        }
    }


    @Override
    public void getImgae(String referenceCode) {
        productMapper.getImage(referenceCode);
    }

    @Override
    public List<LaptopDetailsDTO> getLaptopProductDetails(String productNo) {

        List<LaptopDetailsDTO> laptopDetailsDTO = new ArrayList<>();


        log.info("laptopDetailsDTO 12 = {} ", laptopDetailsDTO);
        log.info("프로덕트넘버값 확인 = {}", productNo);


        for (LaptopDetailsDTO detailsDTO : laptopDetailsDTO) {

            String code = "PD" + productNo;


            log.info("detailsDTO 12 = {}", detailsDTO);

        }


        laptopDetailsDTO = productMapper.laptopProductDetails(productNo);
        log.info("laptopDetailsDTO 12 = {} ", laptopDetailsDTO);


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

    //상품 전체 조회
    @Override
    @Transactional
    public List<ProductDTO> getStoredProducts(int pageNumber, int pageSize) {

        int offset = (pageNumber - 1) * pageSize;
        return productMapper.getAllProducts(pageSize, offset);
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
    public LaptopSpecDTO getProductByCode(String pcode) {

        LaptopSpecDTO laptopSpecDTO = productMapper.getProductByCode(pcode);

        log.info("laptopSpecDTO {} :", laptopSpecDTO);

        return laptopSpecDTO;
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