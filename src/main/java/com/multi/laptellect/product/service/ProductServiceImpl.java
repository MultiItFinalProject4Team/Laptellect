package com.multi.laptellect.product.service;

import com.multi.laptellect.product.model.dto.ImageDTO;
import com.multi.laptellect.product.model.dto.LaptopDetailsDTO;
import com.multi.laptellect.product.model.dto.ProductDTO;
import com.multi.laptellect.product.model.dto.WishlistDTO;
import com.multi.laptellect.product.model.dto.laptop.LaptopSpecDTO;
import com.multi.laptellect.product.model.mapper.ProductMapper;
import com.multi.laptellect.util.RedisUtil;
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
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductMapper productMapper;


    private final CrawlingService crawlingService;

    private final RedisUtil redisUtil;

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
            productDTO.setImage(ProductDTO.getImage());
            productDTO.setCreatedAt(Timestamp.valueOf(now));
            productDTO.setTypeNo(typeNo);

            productDTOList.add(productDTO);

            log.info("ProductDTO Created: {}", productDTO);
        }

        // 중복 확인 및 데이터베이스 삽입
        for (ProductDTO productDTO : productDTOList) {

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
                String url = "http:" + productDTO.getImage();
                String filePath = "src/main/resources/static/img/product";

                String uuid = UUID.randomUUID().toString();
                String uploadName = uuid + ".jpg";

                crawlingService.downloadImage(url, filePath, uploadName);



                System.out.println(uploadName);
                log.info(uploadName);


                imageDTO.setOriginName(url);
                imageDTO.setReferenceCode(code);
                imageDTO.setUploadName(uploadName);

                productMapper.inputImage(imageDTO);


            } else {
                log.info("제품코드는: " + productDTO.getProductCode() + " 입니다.");
            }
        }

        } catch (Exception e){
            log.error("제품 저장 중 오류 발생", e);

        }
    }




    @Override
   public void getImgae(String referenceCode) {
         productMapper.getImage(referenceCode);
    }

    @Override
    public List<LaptopDetailsDTO> getLaptopProductDetails(String productCode) {

        return productMapper.laptopProductDetails(productCode);
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
            if(wishList != null) {
                log.info("위시 리스트 중복 = {}", productNo);

                int wishlistNo = wishList.getWishlistNo();

                log.debug("위시리스트 삭제 시작 = {}", wishlistNo);
                result = productMapper.deleteWishlist(wishlistNo);
                if(result > 0) {
                    log.info("위시리스트 삭제 성공 = {}", result);
                    if(productNoList.size() == 1) result = 2;
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
    public int processBasket(int productNo) throws Exception {
        String memberName = SecurityUtil.getUserDetails().getMemberName();
        memberName = memberName + "_basket";
        String productNoString = String.valueOf(productNo);

        List<String> userBasket = redisUtil.getListData(memberName);

        log.debug("Redis 조회 시작 = {}", userBasket);
        if(userBasket != null) {
            // 중복된 값이 저장되어있는지 검증 True일 시 값 추가 False 일 시 값 삭제
            if(!userBasket.contains(productNoString)) {
                redisUtil.addToList(memberName, String.valueOf(productNo));
                log.info("Redis 값 추가 성공 = {} {}", userBasket, memberName);

                return 1;
            } else {
                redisUtil.deleteToList(memberName, 1, productNoString);
                log.info("Redis 값 삭제 성공 = {} {}", userBasket, memberName);

                return 2;
            }

        } else {
            userBasket = new ArrayList<String>();
            userBasket.add(productNoString);

            redisUtil.setListDataExpire(memberName, userBasket, 24 * 60 * 60);
            log.info("Redis insert 성공 = {}", memberName);

            return 1;
        }
    }

    //상품 전체 조회
    @Override
    @Transactional
    public List<ProductDTO> getStoredProducts(int pageNumber, int pageSize) {

        int offset = (pageNumber - 1) * pageSize;
        return productMapper.getAllProducts(pageSize,offset);
    }

    @Override
    public int getTotalProducts(){
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

        log.info("laptopSpecDTO {} :" ,laptopSpecDTO);

        return laptopSpecDTO;
    }










}