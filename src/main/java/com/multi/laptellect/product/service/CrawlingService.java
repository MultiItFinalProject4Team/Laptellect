package com.multi.laptellect.product.service;

import com.multi.laptellect.product.model.dto.ProductCategoryDTO;
import com.multi.laptellect.product.model.dto.ProductDTO;
import com.multi.laptellect.product.model.dto.laptop.*;
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
import java.util.ArrayList;
import java.util.List;

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
            for (int page = 1; page <= 12; page++) {
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
                        "&listCount=90");
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
        String imageUrl = product.select(".thumb_image img").attr("data-original");

        Elements specElements = product.select(".spec_list a");
//        String cate1 = specElements.size() > 2 ? specElements.get(0).text().trim() : "";
//        String cate2 = specElements.size() > 2 ? specElements.get(1).text().trim() : "";
//        String cate3 = specElements.size() > 2 ? specElements.get(2).text().trim() : "";

//        log.info("speccate1{}:", cate1);
//        log.info("speccate2{}:", cate2);
//        log.info("speccate3{}:", cate3);


        if (imageUrl == null || imageUrl.isEmpty()) { // 이미지 URL이 없을 경우 대체 URL 사용
            imageUrl = product.select(".thumb_image img").attr("src");
        }


        if (productName.isEmpty() || price.isEmpty() || productCode.isEmpty()) {
            return null;
        }

        String firstPrice = price.split(" ")[0];

        ProductDTO ProductDTO = new ProductDTO();
        ProductDTO.setProductCode(productCode);
        ProductDTO.setProductName(productName);
        ProductDTO.setPrice(Integer.parseInt(firstPrice));

        ProductDTO.setImage(imageUrl);


        log.info("데이터확인: {}" + ProductDTO);

        return ProductDTO;
    }

    public void processLaptopDetails() {
        //제품 번호의 리스트 가져옴
        List<ProductDTO> productNos = productMapper.findProduct();

        log.info("productNos리스트 {}", productNos);

        //각 제품에 대한 처리
        for (ProductDTO productDTO : productNos) {
            log.info("1. 제품 리스트 단계 확인 작업 = {}", productNos);
            log.info("2. 제품 리스트 단계 확인 작업 = {}", productDTO);

            int productNo = productDTO.getProductNo();
            String productCode = productDTO.getProductCode();

            getLaptopDetails(productNo, productCode);
        }
    }

    public void processMouseDetails() {
        //제품 번호의 리스트 가져옴
        List<ProductDTO> productNos = productMapper.findProduct();

        log.info("productNos리스트 {}", productNos);

        //각 제품에 대한 처리
        for (ProductDTO productDTO : productNos) {
            log.info("1. 제품 리스트 단계 확인 작업 = {}", productNos);
            log.info("2. 제품 리스트 단계 확인 작업 = {}", productDTO);

            int productNo = productDTO.getProductNo();
            String productCode = productDTO.getProductCode();

            getLaptopDetails(productNo, productCode);
        }
    }

    public void processKeyboardDetails() {
        //제품 번호의 리스트 가져옴
        List<ProductDTO> productNos = productMapper.findProduct();

        log.info("productNos리스트 {}", productNos);

        //각 제품에 대한 처리
        for (ProductDTO productDTO : productNos) {
            log.info("1. 제품 리스트 단계 확인 작업 = {}", productNos);
            log.info("2. 제품 리스트 단계 확인 작업 = {}", productDTO);

            int productNo = productDTO.getProductNo();
            String productCode = productDTO.getProductCode();

            getLaptopDetails(productNo, productCode);
        }
    }

    public static void reviewCrawler(ProductDTO productDTO) {
        int totalPages = 1;
        String url =
                    "https://prod.danawa.com/info/dpg/ajax/companyProductReview.ajax.php?" +
                            "t=0.8990118455164167" +
                            "&prodCode=3798967" +
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
        try {


            Document doc = Jsoup.connect(url).get();

            Elements reviews = doc.select("#danawa-prodBlog-companyReview-content-list .danawa-prodBlog-companyReview-clazz-more");

            Elements paginationElements = doc.select(".page_nav_area .page_num");
            if (!paginationElements.isEmpty()) {
                // 마지막 페이지 번호를 추출
                totalPages = Integer.parseInt(paginationElements.last().text());
            }
            log.info("페이지 = {}",paginationElements);

            for(int pageNum =1; pageNum <= totalPages; pageNum++){
                Document doc1 = Jsoup.connect(url + pageNum).get();

                int reviewCount = 0;

                for(Element review : reviews){
                    String ratingStyle = review.select(".star_mask").attr("style");
                    String rating = ratingStyle.replace("width:","").replace("%","").trim();

                    String title = review.select(".tit_W .tit").text();

                    String content = review.select(".atc").text();

                    reviewCount++;

                    log.info("상품 별점 = {}, 상품 리뷰 제목 = {}, 상품 리뷰 내용 = {}", rating, title, content);
                }
                log.info("조회된 리뷰 계수  = {}", reviewCount);

            }




        } catch (Exception e){

        }


    }


    /**
     * 제품 세부 정보를 가져오는 메서드
     *
     * @param productCode 제품의 코드 (url 경로 매핑시키기 위함)
     * @param productNo   제품의 번호
     * @return 제품 세부 정보가 담긴 LaptopSpecDTO 객체
     */
    public LaptopSpecDTO getLaptopDetails(int productNo, String productCode) {
        log.info("크롤링 사전 준비 제품 번호 = {} ", productNo);
        log.info("크롤링 사전 준비 제품 코드 = {} ", productCode);
        LaptopSpecDTO laptopSpecDTO = new LaptopSpecDTO();

        try {


            String referer = "https://prod.danawa.com/info/?pcode=" + productCode + "&cate=112758";
            String bodyData = "pcode=" + productCode +
                    "&cate1=860" +
                    "&cate2=869";

            String responseHtml = sendPostRequest(PRODUCT_DETAILS_URL, referer, bodyData); // 다나와에 Post 요청
            Document doc = Jsoup.parse(responseHtml); // Return 받은 Json 객체


            ArrayList<String> laptopSpecValue = new ArrayList<>();


            laptopSpecValue.add(getSpecValue(doc, "운영체제(OS)"));

            laptopSpecValue.add(getSpecValue(doc, "CPU 종류"));
            laptopSpecValue.add(getSpecValue(doc, "CPU 코드명"));
            laptopSpecValue.add(getSpecValue(doc, "CPU 넘버"));
            laptopSpecValue.add(getSpecValue(doc, "코어 수"));
            laptopSpecValue.add(getSpecValue(doc, "스레드 수"));

            laptopSpecValue.add(getSpecValue(doc, "GPU 종류"));
            laptopSpecValue.add(getSpecValue(doc, "GPU 제조사"));
            laptopSpecValue.add(getSpecValue(doc, "GPU 칩셋"));
            laptopSpecValue.add(getSpecValue(doc, "GPU 코어"));
            laptopSpecValue.add(getSpecValue(doc, "GPU 클럭"));

            laptopSpecValue.add(getSpecValue(doc, "램 타입"));
            laptopSpecValue.add(getSpecValue(doc, "램 용량"));
            laptopSpecValue.add(getSpecValue(doc, "램 슬롯"));
            laptopSpecValue.add(getSpecValue(doc, "램 대역폭"));
            laptopSpecValue.add(getSpecValue(doc, "램 교체"));

            laptopSpecValue.add(getSpecValue(doc, "화면 크기"));
            laptopSpecValue.add(getSpecValue(doc, "해상도"));

            laptopSpecValue.add(getSpecValue(doc, "저장 용량"));
            laptopSpecValue.add(getSpecValue(doc, "저장장치 종류"));
            laptopSpecValue.add(getSpecValue(doc, "저장 슬롯"));
            laptopSpecValue.add(getSpecValue(doc, "패널 표면 처리"));
            laptopSpecValue.add(getSpecValue(doc, "무게"));

            laptopSpecValue.add(getSpecValue(doc, "NPU 종류"));
            laptopSpecValue.add(getSpecValue(doc, "NPU TOPS"));
            laptopSpecValue.add(getSpecValue(doc, "SoC"));

            laptopSpecValue.add(getSpecValue(doc, "무선랜"));
            laptopSpecValue.add(getSpecValue(doc, "USB"));
            laptopSpecValue.add(getSpecValue(doc, "USB-C"));
            laptopSpecValue.add(getSpecValue(doc, "USB-A"));
            laptopSpecValue.add(getSpecValue(doc, "배터리"));
            laptopSpecValue.add(getSpecValue(doc, "어댑터"));
            laptopSpecValue.add(getSpecValue(doc, "전원"));
            laptopSpecValue.add(getSpecValue(doc, "두께"));
            laptopSpecValue.add(getSpecValue(doc, "쿨링팬"));
            laptopSpecValue.add(getSpecValue(doc, "스피커"));

            log.info("전원 = {}", getSpecValue(doc, "전원"));


            ArrayList<String> laptopSpecName = new ArrayList<>();

            laptopSpecName.add("운영체제(OS)");

            laptopSpecName.add("CPU 종류");
            laptopSpecName.add("CPU 코드명");
            laptopSpecName.add("CPU 넘버");
            laptopSpecName.add("코어 수");
            laptopSpecName.add("스레드 수");

            laptopSpecName.add("GPU 종류");
            laptopSpecName.add("GPU 제조사");
            laptopSpecName.add("GPU 칩셋");
            laptopSpecName.add("GPU 코어");
            laptopSpecName.add("GPU 클럭");

            laptopSpecName.add("램 타입");
            laptopSpecName.add("램 용량");
            laptopSpecName.add("램 슬롯");
            laptopSpecName.add("램 대역폭");
            laptopSpecName.add("램 교체");

            laptopSpecName.add("화면 크기");
            laptopSpecName.add("해상도");

            laptopSpecName.add("저장 용량");
            laptopSpecName.add("저장장치 종류");
            laptopSpecName.add("저장 슬롯");
            laptopSpecName.add("패널 표면 처리");
            laptopSpecName.add("무게");

            laptopSpecName.add("NPU 종류");
            laptopSpecName.add("NPU TOPS");
            laptopSpecName.add("SoC");

            laptopSpecName.add("무선랜");
            laptopSpecName.add("USB");
            laptopSpecName.add("USB-C");
            laptopSpecName.add("USB-A");
            laptopSpecName.add("배터리");
            laptopSpecName.add("어댑터");
            laptopSpecName.add("전원");
            laptopSpecName.add("두께");
            laptopSpecName.add("쿨링팬");
            laptopSpecName.add("스피커");

            log.debug("카테고리 추가 시작 = {}", laptopSpecName);
            int insertCount = 0;
            for (int i = 0; i < laptopSpecName.size(); i++) { // 카테고리 추가를 위한 For문
                ProductCategoryDTO specname = productMapper.findByOptions(laptopSpecName.get(i));


                log.info("specname {}:", specname);
                log.info("laptopSpecName {}:", laptopSpecValue.get(i));

                if (specname == null) { // 카테고리가 DB에 존재하지 않을 시 Insert
                    int o = productMapper.insertProductCategory(1, laptopSpecName.get(i));
                    log.info("상품 카테고리 Insert 완료 = {}", o);
                }

            }
            log.debug("상품 스펙 저장 시작 = {}", laptopSpecValue);
            for (int i = 0; i < laptopSpecValue.size(); i++) { // 상품 스펙을 Insert 하기 위한 For문

                String specName = laptopSpecName.get(i); // 옵션 이름
                String specValue = laptopSpecValue.get(i); // 옵션 값
                int options = Integer.parseInt((productMapper.findCategorytNo(specName))); // 옵션의 PK 키 조회


                if (specValue.equals("정보 없음")) {
                    log.info("옵션 정보 없음 = {}", specValue);
                } else {
                    log.info("옵션 키값 = {}, 옵션 이름 = {}, 옵션 값 = {}", options, specName, specValue);

                    //중복 확인 쿼리
                    int exists = productMapper.checkSpecExists(productNo, options, specValue);
                    if (exists == 0) {
                        productMapper.insertProductSpec(productNo, options, specValue);
                        insertCount++;
                        log.info("ProductSpec Insert 완료 (현재 횟수: {})", insertCount);
                    }


                    log.info("ProductSpec Insert 완료");
                }
            }

            log.info("ProductSpec Insert 실행횟수 확인 = {}", insertCount);
        } catch (IOException e) {
            log.error("Error while getting product details", e);
        }
        return laptopSpecDTO;

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

            StringEntity entity = new StringEntity(bodyData);
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

}