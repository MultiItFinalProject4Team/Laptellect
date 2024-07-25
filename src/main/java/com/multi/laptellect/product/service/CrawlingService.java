package com.multi.laptellect.product.service;

import com.multi.laptellect.product.model.dto.KeyBoardSpecDTO;
import com.multi.laptellect.product.model.dto.LaptopSpecDTO;
import com.multi.laptellect.product.model.dto.ProductInfo;
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

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class CrawlingService {

    private final String PRODUCT_LIST_URL = "https://prod.danawa.com/list/ajax/getProductList.ajax.php";
    private final String PRODUCT_DETAILS_URL = "https://prod.danawa.com/info/ajax/getProductDescription.ajax.php";


    private String sendPostRequest(CloseableHttpClient httpClient, int page, String productType) throws IOException {
        HttpPost post = new HttpPost(PRODUCT_LIST_URL);

        String referer;
        StringEntity params;

        switch (productType) {
            case "laptop":
                referer = "https://prod.danawa.com/list/?cate=112758&15main_11_02=";
                params = new StringEntity("page=" + page +
                        "&listCategoryCode=758" +
                        "&categoryCode=758" +
                        "&physicsCate1=860" +
                        "&physicsCate2=869" +
                        "&sortMethod=BoardCount" +
                        "&viewMethod=LIST" +
                        "&listCount=30");
                break;
            case "mouse":
                referer = "https://prod.danawa.com/list/?cate=112787";
                params = new StringEntity("page=" + page +
                        "&listCategoryCode=787" +
                        "&categoryCode=787" +
                        "&physicsCate1=861" +
                        "&physicsCate2=902" +
                        "&sortMethod=BoardCount" +
                        "&viewMethod=LIST" +
                        "&listCount=10");
                break;
            case "keyboard":
                referer = "https://prod.danawa.com/list/?cate=112782&15main_11_02";
                params = new StringEntity("page=" + page +
                        "&listCategoryCode=782" +
                        "&categoryCode=782" +
                        "&physicsCate1=861" +
                        "&physicsCate2=861" +
                        "&sortMethod=BoardCount" +
                        "&viewMethod=LIST" +
                        "&listCount=10");
                break;
            default:
                throw new IllegalArgumentException("Invalid product type: " + productType);
        }
        post.setHeader("Referer", referer);
        post.setHeader("Content-type", "application/x-www-form-urlencoded");
        post.setEntity(params);


        try (CloseableHttpResponse response = httpClient.execute(post)) {
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
     * HTML을 파싱하여 제품 정보를 추출하는 메소드
     */
    private void parseHtml(String html, List<ProductInfo> productList) {
        Document doc = Jsoup.parse(html);
        Elements productElements = doc.select(".prod_item.prod_layer");

        for (Element product : productElements) {
            ProductInfo productInfo = extractProductInfo(product);
            if (productInfo != null) {
                productList.add(productInfo);
            }
        }
    }

    public List<ProductInfo> crawlProducts(int pages, String type) throws IOException {
        List<ProductInfo> productList = new ArrayList<>();
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            for (int page = 1; page <= pages; page++) {
                String responseString = sendPostRequest(httpClient, page, type);
                parseHtml(responseString, productList);
            }
        }
        return productList;
    }


    private ProductInfo extractProductInfo(Element product) {
        String productName = product.select(".prod_name a").text().trim();
        String price = product.select(".price_sect strong").text().trim().replace(",", "");
        String productCode = product.attr("id").replace("productItem", "");
        String imageUrl = product.select(".thumb_image img").attr("data-original");

        Elements specElements = product.select(".spec_list a");
        String cate3 = specElements.size() > 2 ? specElements.get(2).text().trim() : "";

        if (imageUrl == null || imageUrl.isEmpty()) {
            imageUrl = product.select(".thumb_image img").attr("src");
        }

        if (productName.isEmpty() || price.isEmpty() || productCode.isEmpty() || cate3.isEmpty()) {
            return null;
        }

        String firstPrice = price.split(" ")[0];

        ProductInfo productInfo = new ProductInfo();
        productInfo.setProductCode(productCode);
        productInfo.setProductName(productName);
        productInfo.setPrice(firstPrice);
        productInfo.setImageUrl(imageUrl);
        productInfo.setCate3(cate3);

        return productInfo;
    }


    public LaptopSpecDTO getLaptopDetails(ProductInfo productInfo) {

        LaptopSpecDTO laptopSpecDTO = new LaptopSpecDTO();

        try {
            String url = PRODUCT_DETAILS_URL;
            String referer = "https://prod.danawa.com/info/?pcode=" + productInfo.getProductCode() + "&cate=112758";
            String bodyData = "pcode=" + productInfo.getProductCode() + "&cate1=860&cate2=869&cate3=" + productInfo.getCate3();

            String responseHtml = sendPostRequest(url, referer, bodyData);
            Document doc = Jsoup.parse(responseHtml);

            laptopSpecDTO.setProductName(productInfo.getProductName());
            laptopSpecDTO.setPrice(productInfo.getPrice());
            laptopSpecDTO.setImageUrl(productInfo.getImageUrl());
            laptopSpecDTO.setOs(getSpecValue(doc, "운영체제(OS)"));
            laptopSpecDTO.setCpuManufacturer(getSpecValue(doc, "CPU 제조사"));
            laptopSpecDTO.setCpuType(getSpecValue(doc, "CPU 종류"));
            laptopSpecDTO.setCpuCodeName(getSpecValue(doc, "CPU 코드명"));
            laptopSpecDTO.setCpuNumber(getSpecValue(doc, "CPU 넘버"));
            laptopSpecDTO.setGpuType(getSpecValue(doc, "GPU 종류"));
            laptopSpecDTO.setGpuManufacturer(getSpecValue(doc, "GPU 제조사"));
            laptopSpecDTO.setGpuChipset(getSpecValue(doc, "GPU 칩셋"));
            laptopSpecDTO.setRamType(getSpecValue(doc, "램 타입"));
            laptopSpecDTO.setRamSize(getSpecValue(doc, "램 용량"));
            laptopSpecDTO.setScreenSize(getSpecValue(doc, "화면 크기"));
            laptopSpecDTO.setScreenResolution(getSpecValue(doc, "해상도"));
            laptopSpecDTO.setStorageType(getSpecValue(doc, "저장장치 종류"));
            laptopSpecDTO.setStorageCapacity(getSpecValue(doc, "저장 용량"));
            laptopSpecDTO.setConvenienceFeatures(getSpecValue(doc, "패널 표면 처리"));
            laptopSpecDTO.setWeight(getSpecValue(doc, "무게"));


        } catch (
                IOException e) {
            log.error("Error while getting product details", e);
        }
        return laptopSpecDTO;


    }

    public KeyBoardSpecDTO getKeyBoardDetails(ProductInfo productInfo){

        KeyBoardSpecDTO dto = new KeyBoardSpecDTO();
        try {
            String url = PRODUCT_DETAILS_URL;
            String referer = "https://prod.danawa.com/info/?pcode=" + productInfo.getProductCode() + "&cate=112758";
            String bodyData = "pcode=" + productInfo.getProductCode() + "&cate1=860&cate2=869&cate3=" + productInfo.getCate3();

            String responseHtml = sendPostRequest(url, referer, bodyData);
            Document doc = Jsoup.parse(responseHtml);


            dto.setManufactureCompany(getSpecValue(doc, "제조회사"));
            dto.setSize(getSpecValue(doc, "사이즈"));
            dto.setConnectionMethod(getSpecValue(doc, "연결 방식"));
            dto.setWirelessConnection(getSpecValue(doc, "무선 연결"));
            dto.setBattery(getSpecValue(doc, "배터리"));
            dto.setKeyArrangement(getSpecValue(doc, "키 배열"));
            dto.setInterfaceKeyBoard(getSpecValue(doc, "인터페이스"));
            dto.setContactMethod(getSpecValue(doc, "접점 방식"));
            dto.setKeyBoardSwitch(getSpecValue(doc, "스위치"));
            dto.setKeyBoardType(getSpecValues(doc,"키보드형태"));
            dto.setKeySwitch(getSpecValue(doc, "키 스위치"));
            dto.setKeyPressure(getSpecValue(doc, "키압"));
            dto.setSimultaneousInput(getSpecValue(doc, "동시입력"));
            dto.setResponseSpeed(getSpecValue(doc, "응답속도"));
            dto.setKeycapMaterial(getSpecValue(doc, "키캡 재질"));
            dto.setKeycapEngraving(getSpecValue(doc,"키캡 각인방식"));
            dto.setEngravingLocation(getSpecValue(doc, "각인 위치"));
            dto.setAddOns(getSpecValues(doc,"부가 기능"));
            dto.setWidth(getSpecValue(doc, "가로"));
            dto.setLength(getSpecValue(doc, "세로"));
            dto.setHeight(getSpecValue(doc, "높이"));
            dto.setWeight(getSpecValue(doc, "무게"));
            dto.setCableLength(getSpecValue(doc, "케이블 길이"));

            log.info("키보드 스펙: " + dto.toString());

            


        } catch (
                IOException e) {
            log.error("Error while getting product details", e);
        }
        return dto;

    }

    private static String sendPostRequest(String url, String referer, String bodyData) throws IOException {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpPost post = new HttpPost(url);
            post.setHeader("Referer", referer);
            post.setHeader("Content-Type", "application/x-www-form-urlencoded");

            StringEntity entity = new StringEntity(bodyData);
            post.setEntity(entity);


            try (CloseableHttpResponse response = client.execute(post)) {
                HttpEntity responseEntity = response.getEntity();
                return responseEntity != null ? new String(responseEntity.getContent().readAllBytes()) : "";
            }
        }
    }


    private String getSpecValue(Document doc, String specName) {
        Elements rows = doc.select("table.spec_tbl tr");
        for (Element row : rows) {
            Elements th = row.select("th");
            Elements td = row.select("td");
            for (int i = 0; i < th.size(); i++) {
                if (th.get(i).text().contains(specName)) {
                    if (td.size() > i) {
                        return td.get(i).text().trim();
                    }
                }
            }
        }
        return "정보 없음";
    }

    private List<String> getSpecValues(Document doc, String specName) {
        List<String> values = new ArrayList<>();
        Elements rows = doc.select("table.spec_tbl tr");
        for (Element row : rows) {
            Elements thElements = row.select("th");
            for (Element thElement : thElements) {
                if (thElement.text().contains(specName)) {
                    Elements tdElements = row.select("td");
                    for (Element tdElement : tdElements) {
                        values.add(tdElement.text().trim());
                    }
                    break;
                }
            }

        }
        return values.isEmpty() ? null : values;
    }

    public void downloadImage(String imageUrl, String destinationFile) throws IOException {

        URL url = new URL(imageUrl);
        try (InputStream in = url.openStream()) {

            Files.copy(in, Paths.get(destinationFile));
        }
    }

}

