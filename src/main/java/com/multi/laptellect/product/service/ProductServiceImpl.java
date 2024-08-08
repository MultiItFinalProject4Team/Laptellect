package com.multi.laptellect.product.service;

import com.multi.laptellect.product.model.dto.*;
import com.multi.laptellect.product.model.dto.laptop.*;
import com.multi.laptellect.product.model.mapper.ProductMapper;
import com.multi.laptellect.util.RedisUtil;
import com.multi.laptellect.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * The type Product service.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductMapper productMapper;
    private final CrawlingService crawlingService;
    private final RedisUtil redisUtil;
    private final static String CACHE_KEY_PRODUCT = "product:";

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
    @Cacheable(value = "product", key = "#p0", cacheManager = "productCacheManager")
    public LaptopSpecDTO getLaptopProductDetails(int productNo) {
        log.info("프로덕트넘버값 확인 = {}", productNo);

        LaptopSpecDTO laptop = new LaptopSpecDTO();

        List<LaptopDetailsDTO> laptopDetails = productMapper.laptopProductDetails(productNo);
        log.info("상품 스펙 조회 = {}", laptopDetails);

        if (!laptopDetails.isEmpty()) {
            laptop = getLaptopSpec(productNo, laptopDetails);
        } else {
            return null;
        }

        return laptop;
    }

    @Override
    @Transactional
    public int processWishlist(List<Integer> productNoList) throws Exception {
        // 로그인 한 사용자인지 검증 로그인 하지 않았을 시 3 반환
        if(!SecurityUtil.isAuthenticated()) return 3;
        
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

    @Override

    public LaptopSpecDTO getLaptopSpec(int productNo, List<LaptopDetailsDTO> laptopDetails) {
        LaptopDetailsDTO laptopDetailsDTO = laptopDetails.get(0);
        LaptopSpecDTO specDTO = new LaptopSpecDTO();


        // 상품 기본 정보 변수 설정
//        int productNo = laptopDetailsDTO.getProductNo();
        String productName = laptopDetailsDTO.getProductName();
        int price = laptopDetailsDTO.getPrice();
        String image = laptopDetailsDTO.getUploadName();
        String productCode = laptopDetailsDTO.getProductCode();

        // DTO 객체 변수 선언
        AddOn addOn = new AddOn();
        CPU cpu = new CPU();
        Display display = new Display();
        GPU gpu = new GPU();
        Portability portability = new Portability();
        Power power = new Power();
        RAM ram = new RAM();
        Storage storage = new Storage();

        // 상품 정보를 LaptopSpecDTO에 넣기 위해 가공하기 위한 For문
        log.debug("상품 상세 정보 분류 시작 = {}", laptopDetails);
        for(LaptopDetailsDTO laptop : laptopDetails){
            String categoryCode = laptop.getCategoryNo();

            if(categoryCode.contains("LBI")){

                switch (categoryCode){

                    case "LBI1" :
                        specDTO.setOs(laptop.getOptionValue());
                        break;
                    case "LBI2" :
                        specDTO.setCompany(laptop.getOptionValue());
                        break;
                    case "LBI3" :
                        specDTO.setRegistrationDate(laptop.getOptionValue());
                        break;
                    case "LBI4" :
                        portability.setWeight(laptop.getOptionValue());
                        break;
                    case "LBI5" :
                        portability.setThickness(laptop.getOptionValue());
                        break;

                }

            }

            if(categoryCode.contains("LC")) {
                switch (categoryCode) {
                    case "LC13":
                        cpu.setCpuType(laptop.getOptionValue());
                        break;
                    case "LC14":
                        cpu.setCpuCodeName(laptop.getOptionValue());
                        break;
                    case "LC15":
                        cpu.setCpuNumber(laptop.getOptionValue());
                        break;
                    case "LC16":
                        cpu.setCpuCore(laptop.getOptionValue());
                        break;
                    case "LC17":
                        cpu.setCpuThread(laptop.getOptionValue());
                        break;
                    case "LC18":
                        cpu.setNpu(laptop.getOptionValue());
                        break;
                    case "LC19":
                        cpu.setNpuTops(laptop.getOptionValue());
                        break;
                    case "LC20":
                        cpu.setCpuManufacturer(laptop.getOptionValue());
                        break;
                }
            }

            if(categoryCode.contains("LD")) {
                switch (categoryCode) {
                    case "LD23":
                        display.setScreenSize(laptop.getOptionValue());
                        break;
                    case "LD24":
                        display.setResolution(laptop.getOptionValue());
                        break;
                    case "LD25":
                        display.setPanelSurface(laptop.getOptionValue());
                        break;
                    case "LD26":
                        display.setRefreshRate(laptop.getOptionValue());
                        break;
                    case "LD27":
                        display.setBrightness(laptop.getOptionValue());
                        break;
                    case "LD28":
                        display.setPanelType(laptop.getOptionValue());
                        break;
                }
            }

            if(categoryCode.contains("LG")){
                switch (categoryCode){
                    case "LG35" :
                        gpu.setGpuType(laptop.getOptionValue());
                        break;
                    case "LG36" :
                        gpu.setGpuManufacturer(laptop.getOptionValue());
                        break;
                    case "LG37" :
                        gpu.setGpuChipset(laptop.getOptionValue());
                        break;
                }
            }

            if(categoryCode.contains("LPA")) {
                switch (categoryCode) {
                    case "LPA40":
                        power.setBattery(laptop.getOptionValue());
                        break;
                    case "LPA41":
                        power.setAdapter(laptop.getOptionValue());
                        break;
                    case "LPA42":
                        power.setCharging(laptop.getOptionValue());
                        break;
                }
            }

            if(categoryCode.contains("LR")){
                switch (categoryCode){
                    case "LR10" :
                        ram.setRamSlot(laptop.getOptionValue());
                        break;
                    case "LR12" :
                        ram.setRamChange(laptop.getOptionValue());
                        break;
                    case "LR8" :
                       ram.setRamType(laptop.getOptionValue());
                        break;
                    case "LR9" :
                       ram.setRamSize(laptop.getOptionValue());
                        break;
                }
            }

            if(categoryCode.contains("LS")){
                switch (categoryCode){
                    case "LS20" :
                        storage.setStorageCapacity(laptop.getOptionValue());
                        break;
                    case "LS21" :
                        storage.setStorageType(laptop.getOptionValue());
                        break;
                    case "LS22" :
                        storage.setStorageSlots(laptop.getOptionValue());
                        break;
                }
            }

            if(categoryCode.contains("LWC")){
                switch (categoryCode){
                    case "LWC29" :
                        addOn.setWirelessLan(laptop.getOptionValue());
                        break;
                    case "LWC30" :
                        addOn.setUsb(laptop.getOptionValue());
                        break;
                    case "LWC31" :
                        addOn.setUsbC(laptop.getOptionValue());
                        break;
                    case "LWC32" :
                        addOn.setUsbA(laptop.getOptionValue());
                        break;
                    case "LWC33" :
                        addOn.setBluetooth(laptop.getOptionValue());
                        break;
                    case "LWC34" :
                        addOn.setThunderbolt(laptop.getOptionValue());
                        break;
                }
            }
            log.info("상품 상세 정보 분류 = {}", categoryCode);
        }
        log.info("상품 상세 정보 분류 완료");

        specDTO.setProductNo(productNo);
        specDTO.setProductName(productName);
        specDTO.setPrice(String.valueOf(price));
        specDTO.setImage(image);
        specDTO.setProductCode(productCode);

        specDTO.setAddOn(addOn);
        specDTO.setCpu(cpu);
        specDTO.setDisplay(display);
        specDTO.setGpu(gpu);
        specDTO.setPortability(portability);
        specDTO.setPower(power);
        specDTO.setRam(ram);
        specDTO.setStorage(storage);

        log.info("상품 상세 정보 분류 완료 = {}", specDTO);

        return specDTO;
    }



    //상품 전체 조회
    @Override
    @Transactional
    public List<ProductDTO> getStoredProducts(Integer typeNo) {


        return productMapper.getProductsByType(typeNo);
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
        log.info("노트북 스펙 set = {}", neededOptions);
        return productMapper.findProductSpecByProductNo(productNo, neededOptions);
    }


}