package com.multi.laptellect.product.service;

import com.multi.laptellect.product.model.dto.*;
import com.multi.laptellect.product.model.dto.keyboard.*;
import com.multi.laptellect.product.model.dto.laptop.*;
import com.multi.laptellect.product.model.mapper.ProductMapper;
import com.multi.laptellect.util.RedisUtil;
import com.multi.laptellect.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

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

        List<LaptopDetailsDTO> laptopDetails = productMapper.productDetails(productNo);
        log.info("상품 스펙 조회 = {}", laptopDetails);

        if (!laptopDetails.isEmpty()) {
            laptop = getLaptopSpec(productNo, laptopDetails);
        } else {
            return null;
        }

        return laptop;
    }

    @Override
    @Cacheable(value = "product", key = "#p0", cacheManager = "productCacheManager")
    public KeyBoardSpecDTO getKeyboardProductDetails(int productNo) {
        log.info("프로덕트넘버값1 확인 = {}", productNo);

        KeyBoardSpecDTO keyboard ;

        List<LaptopDetailsDTO> keyBoardSpec = productMapper.productDetails(productNo);
        log.info("상품 스펙 조회1 = {}", keyBoardSpec);


        if (!keyBoardSpec.isEmpty()) {
            keyboard = getKeyboardSpec(productNo, keyBoardSpec);
        } else {
            return null;
        }

        return keyboard;
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
                    case "LBI6" :
                        addOn.setSpeaker(laptop.getOptionValue());
                        break;
                    case "LBI7" :
                        addOn.setCoolingfan(laptop.getOptionValue());
                        break;

                }

            }

            if(categoryCode.contains("LC")) {
                switch (categoryCode) {
                    case "LC13":
                        cpu.setCpuType(laptop.getOptionValue()); //CPU 종류
                        break;
                    case "LC14":
                        cpu.setCpuCodeName(laptop.getOptionValue());// CPU 코드명
                        break;
                    case "LC15":
                        cpu.setCpuNumber(laptop.getOptionValue());// CPU 넘버
                        break;
                    case "LC16":
                        cpu.setCpuCore(laptop.getOptionValue());//코어 수
                        break;
                    case "LC17":
                        cpu.setCpuThread(laptop.getOptionValue());//스레드 수
                        break;
                    case "LC18":
                        cpu.setNpu(laptop.getOptionValue());//NPU 종류
                        break;
                    case "LC19":
                        cpu.setNpuTops(laptop.getOptionValue());//NPU TOPS
                        break;
                    case "LC20":
                        cpu.setCpuManufacturer(laptop.getOptionValue());//CPU 제조사
                        break;
                }
            }

            if(categoryCode.contains("LD")) {
                switch (categoryCode) {
                    case "LD24":
                        display.setScreenSize(laptop.getOptionValue());//화면 크기
                        break;
                    case "LD25":
                        display.setResolution(laptop.getOptionValue());//해상도
                        break;
                    case "LD26":
                        display.setPanelSurface(laptop.getOptionValue());//패널 표면 처리
                        break;
                    case "LD27":
                        display.setRefreshRate(laptop.getOptionValue());//주사율
                        break;
                    case "LD28":
                        display.setBrightness(laptop.getOptionValue());//화면 밝기
                        break;
                    case "LD29":
                        display.setPanelType(laptop.getOptionValue());//패널 종류
                        break;
                }
            }

            if(categoryCode.contains("LG")){
                switch (categoryCode){
                    case "LG36" :
                        gpu.setGpuType(laptop.getOptionValue());
                        break;
                    case "LG37" :
                        gpu.setGpuManufacturer(laptop.getOptionValue());
                        break;
                    case "LG38" :
                        gpu.setGpuChipset(laptop.getOptionValue());
                        break;
                    case "LG39" :
                        gpu.setGpuCore(laptop.getOptionValue());
                        break;
                    case "LG40" :
                        gpu.setGpuClock(laptop.getOptionValue());
                        break;
                }
            }

            if(categoryCode.contains("LPA")) {
                switch (categoryCode) {
                    case "LPA41":
                        power.setBattery(laptop.getOptionValue());
                        break;
                    case "LPA42":
                        power.setAdapter(laptop.getOptionValue());
                        break;
                    case "LPA43":
                        power.setCharging(laptop.getOptionValue());
                        break;
                }
            }

            if(categoryCode.contains("LR")){
                switch (categoryCode){
                    case "LR10" :
                        ram.setRamSlot(laptop.getOptionValue());
                        break;
                    case "LR11" :
                        ram.setRamBandwidth(laptop.getOptionValue());
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
                    case "LS21" :
                        storage.setStorageCapacity(laptop.getOptionValue());
                        break;
                    case "LS22" :
                        storage.setStorageType(laptop.getOptionValue());
                        break;
                    case "LS23" :
                        storage.setStorageSlots(laptop.getOptionValue());
                        break;
                }
            }

            if(categoryCode.contains("LWC")){
                switch (categoryCode){
                    case "LWC30" :
                        addOn.setWirelessLan(laptop.getOptionValue());
                        break;
                    case "LWC31" :
                        addOn.setUsb(laptop.getOptionValue());
                        break;
                    case "LWC32" :
                        addOn.setUsbC(laptop.getOptionValue());
                        break;
                    case "LWC33" :
                        addOn.setUsbA(laptop.getOptionValue());
                        break;
                    case "LWC34" :
                        addOn.setBluetooth(laptop.getOptionValue());
                        break;
                    case "LWC35" :
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

    @Override
    public KeyBoardSpecDTO getKeyboardSpec(int productNo, List<LaptopDetailsDTO> keyBoardSpec) {
        LaptopDetailsDTO keyboardDetailsDTO = keyBoardSpec.get(0);
        KeyBoardSpecDTO specDTO = new KeyBoardSpecDTO();


        String productName = keyboardDetailsDTO.getProductName();
        int price = keyboardDetailsDTO.getPrice();
        String image = keyboardDetailsDTO.getUploadName();
        String productCode = keyboardDetailsDTO.getProductCode();
        String manufacturer = specDTO.getManufacturer();
        String registrationDate = specDTO.getRegistrationDate();

        KeyAccessory accessory = new KeyAccessory();
        KeyBuild build = new KeyBuild();
        KeyDesign design = new KeyDesign();
        KeyDimensions dimensions = new KeyDimensions();
        KeyFeature feature = new KeyFeature();


        log.debug("상품 상세 정보 분류 시작 = {}", keyBoardSpec);
        for(LaptopDetailsDTO keyboard : keyBoardSpec){
            String categoryCode = keyboard.getCategoryNo();



            if(categoryCode.contains("LBI")){

                switch (categoryCode) {

                    case "LBI2":
                        specDTO.setManufacturer(keyboard.getOptionValue());
                        break;
                    case "LBI3":
                         specDTO.setRegistrationDate(keyboard.getOptionValue());
                        break;

                }
            }


            if(categoryCode.contains("KBI")){

                switch (categoryCode){

                    case "KBI3" :
                        specDTO.setSize(keyboard.getOptionValue());
                        break;
                    case "KBI4" :
                        specDTO.setConnectionType(keyboard.getOptionValue());
                        break;
                    case "KBI5" :
                        specDTO.setRegistrationDate(keyboard.getOptionValue());
                        break;
                    case "KBI6" :
                        specDTO.setInterfaceType(keyboard.getOptionValue());
                        break;
                }

            }

            if(categoryCode.contains("KB")) {
                switch (categoryCode) {
                    case "KB10":
                        build.setKeySwitch(keyboard.getOptionValue());
                        break;
                    case "KB7":
                        build.setKeyLayout(keyboard.getOptionValue());
                        break;
                    case "KB8":
                        build.setSwitchType(keyboard.getOptionValue());
                        break;
                    case "KB9":
                        build.setKeySwitch(keyboard.getOptionValue());
                        break;

                }
            }

            if(categoryCode.contains("KD")) {
                switch (categoryCode) {
                    case "KD12":
                        design.setRainbowBacklight(keyboard.getOptionValue());
                        break;
                    case "KD13":
                        design.setStepSculpture2(keyboard.getOptionValue());
                        break;
                    case "KD14":
                        design.setMetalHousing(keyboard.getOptionValue());
                        break;
                    case "KD15":
                        design.setWaterResistant(keyboard.getOptionValue());
                        break;
                    case "KD16":
                        design.setRgbBacklight(keyboard.getOptionValue());
                        break;
                    case "KD17":
                        design.setStabilizer(keyboard.getOptionValue());
                        break;
                    case "KD18":
                        design.setSingleColorBacklight(keyboard.getOptionValue());
                        break;
                }
            }

            if(categoryCode.contains("KDW")){
                switch (categoryCode){
                    case "KDW24" :
                        dimensions.setWidth(keyboard.getOptionValue());
                        break;
                    case "KDW25" :
                        dimensions.setHeight(keyboard.getOptionValue());
                        break;
                    case "KDW26" :
                        dimensions.setDepth(keyboard.getOptionValue());
                        break;
                    case "KDW28" :
                        dimensions.setCableLength(keyboard.getOptionValue());
                        break;
                }
            }

            if(categoryCode.contains("KF")) {
                switch (categoryCode) {
                    case "KF19":
                        feature.setNKeyRollover(keyboard.getOptionValue());
                        break;
                    case "KF20":
                        feature.setKeycapMaterial(keyboard.getOptionValue());
                        break;
                    case "KF21":
                        feature.setResponseRate(keyboard.getOptionValue());
                        break;
                    case "KF22":
                        feature.setKeycapEngravingMethod(keyboard.getOptionValue());
                        break;
                    case "KF23":
                        feature.setEngravingPosition(keyboard.getOptionValue());
                        break;
                }
            }

            if(categoryCode.contains("KC")){
                switch (categoryCode){
                    case "KC29" :
                        accessory.setKeycapRemover(keyboard.getOptionValue());
                        break;
                    case "KC30" :
                        accessory.setCleaningBrush(keyboard.getOptionValue());
                        break;
                    case "KC31" :
                        accessory.setDeskPad(keyboard.getOptionValue());
                        break;
                    case "KC32" :
                        accessory.setKeySkin(keyboard.getOptionValue());
                        break;
                    case "KC33" :
                        accessory.setLoop(keyboard.getOptionValue());
                        break;
                    case "KC34" :
                        accessory.setWristRest(keyboard.getOptionValue());
                        break;
                }
            }



            log.info("상품 상세 정보 분류 = {}", categoryCode);
        }
        log.info("상품 상세 정보 분류 완료");

        specDTO.setProductNo(productNo);
        specDTO.setProductName(productName);
        specDTO.setPrice(price);
        specDTO.setImage(image);
        specDTO.setProductCode(productCode);
        specDTO.setManufacturer(manufacturer);
        specDTO.setRegistrationDate(registrationDate);

        specDTO.setKeyAccessory(accessory);
        specDTO.setKeyBuild(build);
        specDTO.setKeyDesign(design);
        specDTO.setKeyDimensions(dimensions);
        specDTO.setKeyFeature(feature);


        log.info("상품 상세 정보 분류 완료 = {}", specDTO);

        return specDTO;
    }



    @Override
    public Page<ProductDTO> searchProducts(ProductSearchDTO searchDTO) {

        Pageable pageable = PageRequest.of(searchDTO.getPage(),searchDTO.getSize());

        log.info("서비스 로직 pageble 확인 = {}, {}", searchDTO.getPage(),searchDTO.getSize());

        ArrayList<ProductDTO> productList = productMapper.findByNameSearch(searchDTO);

        long total = productMapper.countBySearchCriteria(searchDTO);

        log.info("PageImpl searchProducts 확인 = {}, {}, \n 총 수량 : {}", pageable, productList, total );


        return new PageImpl<>(productList,pageable,total);



    }



    @Override
    public ArrayList<Integer> getWishlistString() throws Exception {
        int memberNo = SecurityUtil.getUserNo();
        ArrayList<Integer> wishList = productMapper.findAllWishlistString(memberNo);

        if (wishList == null) {
            return null;
        } else {
            return wishList;
        }
    }

    @Override
    public Map<String, List<String>> productFilterSearch() {

       List<SpecDTO> specDTOS =  productMapper.productFilterSearch();

       Map<String, List<String>> specMap = new HashMap<>();


        Set<String> keysToKeep = new HashSet<>(Arrays.asList(
                "LBI1", "LBI2", "LBI4", "LBI5", "LR9",
                "LC13", "LG38", "LS22", "LS21",
                "LD25"
        ));


       for(SpecDTO spec : specDTOS){

           log.info("필터링 과정 = {}",spec.getCategoryNo());

           String key = spec.getCategoryNo();
           String value = spec.getOptionValue();

           //-contains()함수는 대상 문자열에 특정 문자열이 포함되어 있는지 확인하는 함수이다.
           //- 대/소문자를 구분한다.

           if (keysToKeep.contains(key)) {
               List<String> values = specMap.get(key);

               if (values == null) {
                   values = new ArrayList<>();
                   specMap.put(key, values);  // 생성한 리스트를 다시 Map에 추가
               }

               // 값이 리스트에 이미 포함되어 있는지 확인하고, 포함되어 있지 않으면 추가
               if (!values.contains(value)) {
                   values.add(value);
               }
           }
       }

        log.info("필터링 과정3 = {}",specMap);


       return specMap;
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