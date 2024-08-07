package com.multi.laptellect.product.service;

import com.multi.laptellect.product.model.dto.ProductCategoryDTO;
import com.multi.laptellect.product.model.dto.ProductDTO;
import com.multi.laptellect.product.model.dto.ReviewDTO;
import com.multi.laptellect.product.model.dto.laptop.LaptopSpecDTO;
import com.multi.laptellect.product.model.mapper.ProductMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * The type Crawling service.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CrawlingService {

    private final ProductMapper productMapper;

    private final String PRODUCT_LIST_URL = "https://prod.danawa.com/list/ajax/getProductList.ajax.php";
    private final String PRODUCT_DETAILS_URL = "https://prod.danawa.com/info/ajax/getProductDescription.ajax.php";

    /**
     * 지정된 타입의 제품을 크롤링하는 메서드
     *
     * @param typeNo 크롤링할 제품의 유형(laptop, mouse, keyboard)
     * @return 제품 정보를 담은 ProductDTO 리스트
     * @throws IOException HTTP 요청 중 발생 할 수있는 예외 처리
     */
    public List<ProductDTO> crawlProducts(int typeNo) throws IOException {
        List<ProductDTO> productList = new ArrayList<>();
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            for (int page = 1; page <= 5; page++) {
                String responseString = sendPostRequest(httpClient, page, typeNo);
                parseHtml(responseString, productList);
                log.info("productList확인{}", productList);
            }

        }
        return productList;
    }

    /**
     * HTTP POST 요청을 보내는 메서드
     *
     * @param httpClient  HTTP 클라이언트
     * @param page        요청할 페이지 번호
     * @param productType 요청할 제품의 유형
     * @return 응답으로 받은 HTML 문자열
     * @throws IOException HTTP 요청 중 발생할 수 있는 예외
     */
    private String sendPostRequest(CloseableHttpClient httpClient, int page, int productType) throws IOException {
        HttpPost post = new HttpPost(PRODUCT_LIST_URL); // 요청 보낼 url 설정
        String referer;
        StringEntity params;

        switch (productType) {
            case 1:
                referer = "https://prod.danawa.com/list/?cate=112758&15main_11_02=";
                params = new StringEntity("page=" + page +
                        "&listCategoryCode=758" +
                        "&categoryCode=758" +
                        "&physicsCate1=860" +
                        "&physicsCate2=869" +
                        "&sortMethod=NEW" +
                        "&viewMethod=LIST" +
                        "&listCount=10");
                log.info("laptopType {}", productType);
                break;

            case 2:
                referer = "https://prod.danawa.com/list/?cate=112787";
                params = new StringEntity("page=" + page +
                        "&listCategoryCode=787" +
                        "&categoryCode=787" +
                        "&physicsCate1=861" +
                        "&physicsCate2=902" +
                        "&sortMethod=BoardCount" +
                        "&viewMethod=LIST" +
                        "&listCount=10");
                log.info("mouseType {}", productType);
                break;

            case 3:
                referer = "https://prod.danawa.com/list/?cate=112782&15main_11_02";
                params = new StringEntity("page=" + page +
                        "&listCategoryCode=782" +
                        "&categoryCode=782" +
                        "&physicsCate1=861" +
                        "&physicsCate2=861" +
                        "&sortMethod=BoardCount" +
                        "&viewMethod=LIST" +
                        "&listCount=10");
                log.info("keyboardType {}", productType);
                break;

            default:
                throw new IllegalArgumentException("Invalid product type: " + productType);
        }


        //요청 헤더
        post.setHeader("Referer", referer);
        post.setHeader("Content-type", "application/x-www-form-urlencoded");
        //요청 본문 설정
        post.setEntity(params);


        try (CloseableHttpResponse response = httpClient.execute(post)) { //요청 실행 및 응답 수신
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                String responseString = EntityUtils.toString(entity);
                log.info("Page: " + page);
                return responseString;
            }
        }
        return "";
    }

    /**
     * HTML을 파싱하여 제품 정보를 추출하는 메서드
     *
     * @param html        크롤링한 HTML 문자열
     * @param productList 추출한 제품 정보를 저장할 리스트
     */
    private void parseHtml(String html, List<ProductDTO> productList) {

        //HTML 문자열 파싱 (웹 페이지를 읽기 쉬운 구조로 변환하는 과정)
        Document doc = Jsoup.parse(html);
        //제품 요소 선택
        Elements productElements = doc.select(".prod_item.prod_layer");

        log.info("제품요소 {}", productElements);

        for (Element product : productElements) {
            ProductDTO ProductDTO = extractProductDTO(product); //제품 정보 DTO로 추출
            log.info("parseHtml제품정보 {}", product);
            if (ProductDTO != null) {
                productList.add(ProductDTO);
            }
        }
    }


    /**
     * HTML 요소에서 제품 정보를 추출하는 메서드
     *
     * @param product 제품 정보를 포함한 HTML 요소
     * @return 추출한 제품 정보를 담은 ProductDTO 객체
     */
    private ProductDTO extractProductDTO(Element product) {
        String productName = product.select(".prod_name a").text().trim();
        String price = product.select(".price_sect strong").text().trim().replace(",", "");
        String productCode = product.attr("id").replace("productItem", "");

        // String imageUrl = product.select(".thumb_image img").attr("data-original");

//        if (imageUrl == null || imageUrl.isEmpty()) { // 이미지 URL이 없을 경우 대체 URL 사용
//            imageUrl = product.select(".thumb_image img").attr("src");
//        }

        if (productName.isEmpty() || price.isEmpty() || productCode.isEmpty()) {
            return null;
        }

        int firstPrice = Integer.parseInt(price.split(" ")[0]);

        log.info("가격 데이터 확인 = {}", firstPrice);

        ProductDTO ProductDTO = new ProductDTO();
        ProductDTO.setProductCode(productCode);
        ProductDTO.setProductName(productName);
        ProductDTO.setPrice(firstPrice);

        //   ProductDTO.setImage(imageUrl);

        log.info("데이터확인: {}" + ProductDTO);

        return ProductDTO;
    }

    public void processLaptopDetails(int productType) {
        //제품 번호의 리스트 가져옴
        List<ProductDTO> products = productMapper.findProduct();
        for (ProductDTO product : products) {
            int productNo = product.getProductNo();
            String productCode = product.getProductCode();
            log.info("processLaptopDetails = {}", productType);
            getLaptopDetails(productNo, productCode, productType);
        }
    }

//    public void processMouseDetails() {
//        //제품 번호의 리스트 가져옴
//        List<ProductDTO> productNos = productMapper.findProduct();
//
//        log.info("productNos리스트 {}", productNos);
//
//        //각 제품에 대한 처리
//        for (ProductDTO productDTO : productNos) {
//            log.info("1. 제품 리스트 단계 확인 작업 = {}", productNos);
//            log.info("2. 제품 리스트 단계 확인 작업 = {}", productDTO);
//
//            int productNo = productDTO.getProductNo();
//            String productCode = productDTO.getProductCode();
//
//            getLaptopDetails(productNo, productCode);
//        }
//    }
//
//    public void processKeyboardDetails() {
//        //제품 번호의 리스트 가져옴
//        List<ProductDTO> productNos = productMapper.findProduct();
//
//        log.info("productNos리스트 {}", productNos);
//
//        //각 제품에 대한 처리
//        for (ProductDTO productDTO : productNos) {
//            log.info("1. 제품 리스트 단계 확인 작업 = {}", productNos);
//            log.info("2. 제품 리스트 단계 확인 작업 = {}", productDTO);
//
//            int productNo = productDTO.getProductNo();
//            String productCode = productDTO.getProductCode();
//
//            getLaptopDetails(productNo, productCode);
//        }
//    }


    /**
     * 제품 세부 정보를 가져오는 메서드
     *
     * @param productCode 제품의 코드 (url 경로 매핑시키기 위함)
     * @param productNo   제품의 번호
     * @return 제품 세부 정보가 담긴 LaptopSpecDTO 객체
     */
    public LaptopSpecDTO getLaptopDetails(int productNo, String productCode, int productType) {
        log.info("크롤링 사전 준비 제품 번호 = {} ", productNo);
        log.info("크롤링 사전 준비 제품 코드 = {} ", productCode);
        LaptopSpecDTO laptopSpecDTO = new LaptopSpecDTO();

        try {
            String referer = "https://prod.danawa.com/info/?pcode=" + productCode + "&cate=112758";
            String bodyData = "pcode=" + productCode +
                    "&cate1=860" +
                    "&cate2=869";

            String responseHtml = sendPostRequest(PRODUCT_DETAILS_URL, referer, bodyData); // 다나와에 Post 요청
            log.info("responseHtml = {}", responseHtml);

            Document doc = Jsoup.parse(responseHtml); // Return 받은 Json 객체

            log.info("doc 확인 = {}", doc);

            Map<String, List<String>> categoryMap = createLaptopCategory(productType);
            log.info("getLaptopDetails = {}", productType);


            ProductDTO productDTO = getProductDetails1(productCode);
            log.info("ProductDTO 확인 = {}", productDTO);


            createLaptopSpec(productNo, categoryMap, doc,productDTO);
        } catch (IOException e) {
            log.error("Error while getting product details", e);
        }
        return laptopSpecDTO;

    }

    /**
     *
     * @param productCode 제품의 코드
     * @return 조건이 아닐시 반환
     * @throws IOException 예외처리
     */
    public String getProductImageUrl(String productCode) throws IOException {


        String url = "https://prod.danawa.com/info/?pcode=" + productCode + "&cate=112758";
        Document doc = Jsoup.connect(url).get();

        Elements imageElements = doc.select("div.photo_w img#baseImage");
        return !imageElements.isEmpty() ? imageElements.first().attr("src") : "이미지 URL을 찾을 수 없습니다.";
    }



    public ProductDTO getProductDetails1(String productCode) throws IOException {
        ProductDTO productDTO = new ProductDTO();
        String url = "https://prod.danawa.com/info/?pcode=" + productCode + "&cate=112758";
        Document doc = Jsoup.connect(url).get();

        Elements registrationMonthElements = doc.select("span.txt");
        for (Element element : registrationMonthElements) {
            if (element.text().contains("등록월")) {
                String[] parts = element.text().split(": ");
                if (parts.length == 2) {
                    productDTO.setRegistrationMonth(parts[1].trim());
                }
                break;
            }
        }

        boolean manufacturerFound = false;

        Elements manufacturerElements = doc.select("span#makerTxtArea");
        for (Element element : manufacturerElements) {
            String[] parts = element.text().split(": ");

            log.info("parts 값 확인 합니다. = {}", parts);
            if (parts.length == 2) {
                productDTO.setManufacturer(parts[1].trim());
                manufacturerFound = true;
                break;
            }

        }
        if (!manufacturerFound) {
            Elements alternativeManufacturerElements = doc.select("span#alternativeMakerTxtArea");
            for (Element element : alternativeManufacturerElements) {
                String[] parts = element.text().split(": ");

                log.info("parts 값 확인 합니다. = {}", parts);
                if (parts.length == 2) {
                    productDTO.setManufacturer(parts[1].trim());
                    break;
                }
            }
        }
        // id가 imageFrom인 span 요소를 선택하고 텍스트를 가져옴
        Elements imageFromElements = doc.select("span#imageFrom");
        for (Element element : imageFromElements) {
            String text = element.text();
            if (text.contains("이미지출처:")) {
                String imageSource = text.split("이미지출처:")[1].trim();
                productDTO.setManufacturer(imageSource);
                break;
            }
        }

        return productDTO;
    }




    /**
     * HTTP POST 요청을 보내는 정적 메서드
     *
     * @param url      요청을 보낼 URL
     * @param referer  리퍼러 URL
     * @param bodyData 요청 본문 데이터
     * @return 응답으로 받은 문자열
     * @throws IOException HTTP 요청 중 발생할 수 있는 예외
     */
    private static String sendPostRequest(String url, String referer, String bodyData) throws IOException {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpPost post = new HttpPost(url);
            log.info("url 확인 {} =", url);
            post.setHeader("Referer", referer);
            log.info("리퍼러 확인 {} =", referer);
            post.setHeader("Content-Type", "application/x-www-form-urlencoded");

            StringEntity entity = new StringEntity(bodyData, StandardCharsets.UTF_8);
            post.setEntity(entity);


            try (CloseableHttpResponse response = client.execute(post)) {
                HttpEntity responseEntity = response.getEntity();
                return responseEntity != null ? new String(responseEntity.getContent().readAllBytes()) : "";
            }
        }
    }


    /**
     * HTML 문서에서 특정 스펙의 값을 추출하는 메서드
     *
     * @param doc      HTML 문서
     * @param specName 추출할 스펙 이름
     * @return 스펙 값
     */
    private String getSpecValue(Document doc, String specName) {
        Elements rows = doc.select("table.spec_tbl tr");
        for (Element row : rows) {
            Elements th = row.select("th");
            Elements td = row.select("td");
            for (int i = 0; i < th.size(); i++) {
                if (th.get(i).text().equals(specName)) {
                    if (td.size() > i) {
                        return td.get(i).text().trim();
                    }
                }
            }
        }
        return "정보 없음";
    }


    /**
     * 이미지 파일을 다운로드하는 메서드
     *
     * @param imageUrl      이미지 URL
     * @param saveDirectory 저장할 디렉토리 경로
     * @param imageName     저장할 이미지 파일명
     */
    public static void downloadImage(String imageUrl, String saveDirectory, String imageName) {
        // 디렉토리 경로에 이미지 파일명을 추가
        String savePath = saveDirectory + "/" + imageName;

        // 디렉토리 생성 (존재하지 않는 경우)
        File directory = new File(saveDirectory);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        try (BufferedInputStream in = new BufferedInputStream(new URL(imageUrl).openStream());
             FileOutputStream fileOutputStream = new FileOutputStream(savePath)) {
            byte dataBuffer[] = new byte[1024];
            int bytesRead;
            while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
                fileOutputStream.write(dataBuffer, 0, bytesRead);
            }
            System.out.println("Image successfully downloaded: " + savePath);
        } catch (IOException e) {
            System.out.println("Error downloading image: " + e.getMessage());
        }
    }

    public Map<String, List<String>> createLaptopCategory(int productType) {
        ArrayList<String> laptopSpecNames = new ArrayList<>();
        Map<String, List<String>> categoryMap = new LinkedHashMap<>();

        categoryMap.put("LBI", Arrays.asList("운영체제(OS)", "제조사", "등록월", "무게", "두께", "스피커", "쿨링팬"));
        categoryMap.put("LR", Arrays.asList("램 타입", "램 용량", "램 슬롯", "램 대역폭", "램 교체"));
        categoryMap.put("LC", Arrays.asList( "CPU 종류", "CPU 코드명", "CPU 넘버", "코어 수", "스레드 수", "NPU 종류", "NPU TOPS","CPU 제조사"));
        categoryMap.put("LS", Arrays.asList("저장 용량", "저장장치 종류", "저장 슬롯"));
        categoryMap.put("LD", Arrays.asList("화면 크기", "해상도", "패널 표면 처리", "주사율", "화면 밝기", "패널 종류"));
        categoryMap.put("LWC", Arrays.asList("무선랜", "USB", "USB-C", "USB-A", "블루투스", "썬더볼트4"));
        categoryMap.put("LG", Arrays.asList("GPU 종류", "GPU 제조사", "GPU 칩셋", "GPU 코어", "GPU 클럭"));
        categoryMap.put("LPA", Arrays.asList("배터리", "어댑터", "전원"));

        int categoryCount = 0;

        for (Map.Entry<String, List<String>> entry : categoryMap.entrySet()) {
            String categoryPrefix = entry.getKey();
            List<String> specs = entry.getValue();

            log.info("categoryPrefix 데이터 확인 = {}", categoryPrefix);
            log.info("specs 데이터 확인 = {}", specs);
            log.info("createLaptopCategory = {}", productType);


            for (String specName : specs) {

                categoryCount++;
                String categoryNo = categoryPrefix + categoryCount;

                ProductCategoryDTO spec = productMapper.findByOptions(specName);
                log.info("specname {}:", specName);
                if (spec == null) {

                    productMapper.insertProductCategory(categoryNo, productType, specName);


                }
            }
        }

        return categoryMap;
    }




    public void createLaptopSpec(int productNo, Map<String, List<String>> categoryMap, Document doc,ProductDTO productDTO) {
        ArrayList<String> laptopSpecValue = new ArrayList<>();

        // 노트북 기본 정보 (LBI)
        laptopSpecValue.add(getSpecValue(doc, "운영체제(OS)"));
        laptopSpecValue.add(productDTO.getManufacturer()); //제조사
        laptopSpecValue.add(productDTO.getRegistrationMonth()); //등록월
        laptopSpecValue.add(getSpecValue(doc, "무게"));
        laptopSpecValue.add(getSpecValue(doc, "두께"));
        laptopSpecValue.add(getSpecValue(doc, "스피커"));
        laptopSpecValue.add(getSpecValue(doc, "쿨링팬"));

        // 노트북 RAM (LR)
        laptopSpecValue.add(getSpecValue(doc, "램 타입"));
        laptopSpecValue.add(getSpecValue(doc, "램 용량"));
        laptopSpecValue.add(getSpecValue(doc, "램 슬롯"));
        laptopSpecValue.add(getSpecValue(doc, "램 대역폭"));
        laptopSpecValue.add(getSpecValue(doc, "램 교체"));

        // 노트북 CPU (LC)

        laptopSpecValue.add(getSpecValue(doc, "CPU 종류"));
        laptopSpecValue.add(getSpecValue(doc, "CPU 코드명"));
        laptopSpecValue.add(getSpecValue(doc, "CPU 넘버"));
        laptopSpecValue.add(getSpecValue(doc, "코어 수"));
        laptopSpecValue.add(getSpecValue(doc, "스레드 수"));
        laptopSpecValue.add(getSpecValue(doc, "NPU 종류"));
        laptopSpecValue.add(getSpecValue(doc, "NPU TOPS"));
        laptopSpecValue.add(getSpecValue(doc, "CPU 제조사"));
        // 노트북 저장관련 (LS)
        laptopSpecValue.add(getSpecValue(doc, "저장 용량"));
        laptopSpecValue.add(getSpecValue(doc, "저장장치 종류"));
        laptopSpecValue.add(getSpecValue(doc, "저장 슬롯"));

        // 노트북 디스플레이 (LD)
        laptopSpecValue.add(getSpecValue(doc, "화면 크기"));
        laptopSpecValue.add(getSpecValue(doc, "해상도"));
        laptopSpecValue.add(getSpecValue(doc, "패널 표면 처리"));
        laptopSpecValue.add(getSpecValue(doc, "주사율"));
        laptopSpecValue.add(getSpecValue(doc, "화면 밝기"));
        laptopSpecValue.add(getSpecValue(doc, "패널 종류"));

        // 무선랜 및 연결 장치 (LWC)
        laptopSpecValue.add(getSpecValue(doc, "무선랜"));
        laptopSpecValue.add(getSpecValue(doc, "USB"));
        laptopSpecValue.add(getSpecValue(doc, "USB-C"));
        laptopSpecValue.add(getSpecValue(doc, "USB-A"));
        laptopSpecValue.add(getSpecValue(doc, "블루투스"));
        laptopSpecValue.add(getSpecValue(doc, "썬더볼트4"));

        // 노트북 GPU (LG)
        laptopSpecValue.add(getSpecValue(doc, "GPU 종류"));
        laptopSpecValue.add(getSpecValue(doc, "GPU 제조사"));
        laptopSpecValue.add(getSpecValue(doc, "GPU 칩셋"));
        laptopSpecValue.add(getSpecValue(doc, "GPU 코어"));
        laptopSpecValue.add(getSpecValue(doc, "GPU 클럭"));

        // 노트북 전원 (LPA)
        laptopSpecValue.add(getSpecValue(doc, "배터리"));
        laptopSpecValue.add(getSpecValue(doc, "어댑터"));
        laptopSpecValue.add(getSpecValue(doc, "전원"));


        log.debug("상품 스펙 저장 시작 = {}", laptopSpecValue);

        int index = 0;
        for (Map.Entry<String, List<String>> entry : categoryMap.entrySet()) {
            List<String> specs = entry.getValue();

            for (String specName : specs) {
                if (index >= laptopSpecValue.size()) {
                    log.error("Index out of bounds: index = {}, size = {}", index, laptopSpecValue.size());
                    return; // 인덱스가 리스트 크기를 벗어날 때 메서드 종료
                }

                String specValue = laptopSpecValue.get(index);

                if (specValue == null || specValue.equals("정보 없음")) {
                    log.info("옵션 정보 없음 = {}", specValue);
                } else {
                    log.info("Insert 값 = {} : {}", specName, specValue);

                    // 중복 확인 쿼리
                    int exists = productMapper.checkSpecExists(productNo, specValue);
                    if (exists == 0) {
                        log.info("상품 스펙 insert 값 = {} {} {}", productNo, specName, specValue);
                        productMapper.insertProductSpec(productNo, specName, specValue);

                        log.info("ProductSpec Insert 완료 = {}", productNo);
                    }

                    log.info("ProductSpec Insert 완료");
                }
                index++;
            }
        }

    }

    public void reviewCrawler() {

        List<ProductDTO> productNos = productMapper.findProduct();
        int emptyReviewData = 0;


        for (ProductDTO productDTO : productNos) {

            int productNo = productDTO.getProductNo();
            String productCode = productDTO.getProductCode();

            try {
                for (int totalPages = 1; totalPages <= 1000; totalPages++) {
                    String url =
                            "https://prod.danawa.com/info/dpg/ajax/companyProductReview.ajax.php?" +
                                    "t=0.8990118455164167" +
                                    "&prodCode=" + productCode +
                                    "&cate1Code=861" +
                                    "&page=" + totalPages +
                                    "&limit=100" +
                                    "&score=0" +
                                    "&sortType=" +
                                    "&onlyPhotoReview=" +
                                    "&usefullScore=Y" +
                                    "&innerKeyword=" +
                                    "&subjectWord=0" +
                                    "&subjectWordString=" +
                                    "&subjectSimilarWordString=" +
                                    "&_=1722562970811";

                    Document doc = Jsoup.connect(url).get();
                    Elements reviews = doc.select("#danawa-prodBlog-companyReview-content-list .danawa-prodBlog-companyReview-clazz-more");

                    if (reviews.isEmpty()) {
                        emptyReviewData++;
                        log.info("조회된 리뷰가 없습니다. 페이지: {}", totalPages);
                        if (emptyReviewData >= 3) {
                            log.info("데이터 조회를 끝마쳤습니다 크롤링을 종료합니다.");
                            break;
                        }
                        continue;
                    }

                    int reviewCount = 0;
                    for (Element review : reviews) {
                        String ratingStyle = review.select(".star_mask").attr("style");
                        String rating = ratingStyle.replace("width:", "").replace("%", "").trim();

                        int starRating = convertRatingToStars(Integer.parseInt(rating));

                        String title = review.select(".tit_W .tit").text();
                        String content = review.select(".atc").text();

                        reviewCount++;

                        log.info("상품 별점 = {}, 상품 리뷰 제목 = {}, 상품 리뷰 내용 = {}", starRating, title, content);


                        ReviewDTO reviewDTO = new ReviewDTO();
                        reviewDTO.setProductNo(productNo);
                        reviewDTO.setRating(starRating);
                        reviewDTO.setTitle(title);
                        reviewDTO.setContent(content);

                        productMapper.inputReviewDate(reviewDTO);


                    }
                    log.info("페이지 {}에서 조회된 리뷰 계수 = {}", totalPages, reviewCount);
                }
            } catch (Exception e) {
                log.error("Error during crawling reviews", e);
            }
        }


    }

    private static int convertRatingToStars(int rating) {
        return rating / 20;
    }


}
