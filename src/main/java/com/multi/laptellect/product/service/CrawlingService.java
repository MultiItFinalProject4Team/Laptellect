package com.multi.laptellect.product.service;

import com.multi.laptellect.product.model.dto.LaptopSpecDTO;
import com.multi.laptellect.product.model.dto.ProductDTO;
import com.multi.laptellect.product.model.dto.ProductInfo;
import com.multi.laptellect.product.model.mapper.ProductMapper;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class CrawlingService {

    private static final String URL = "https://prod.danawa.com/list/ajax/getProductList.ajax.php";
    private static final String PRODUCT_DETAILS_URL = "https://prod.danawa.com/info/ajax/getProductDescription.ajax.php";

    @Autowired
    private ProductMapper productMapper;

    private String sendPostRequest(CloseableHttpClient httpClient, int page) throws IOException {
        HttpPost post = new HttpPost(URL);
        post.setHeader("Referer", "https://prod.danawa.com/list/?cate=112758&15main_11_02=");
        post.setHeader("Content-type", "application/x-www-form-urlencoded");

        StringEntity params = new StringEntity("page=" + page +
                "&listCategoryCode=758&categoryCode=758&physicsCate1=860&physicsCate2=869&sortMethod=BoardCount&viewMethod=LIST&listCount=10");
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


    public List<ProductInfo> crawlProducts(int pages) throws IOException {
        List<ProductInfo> productList = new ArrayList<>();
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            for (int page = 1; page <= pages; page++) {
                String responseString = sendPostRequest(httpClient, page);
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
        productInfo.setPcode(productCode);
        productInfo.setProductName(productName);
        productInfo.setPrice(firstPrice);
        productInfo.setImageUrl(imageUrl);
        productInfo.setCate3(cate3);

        return productInfo;
    }

    public void saveProductsToDB(List<ProductInfo> productList) {
        List<ProductDTO> productDTOList = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        for (ProductInfo productInfo : productList) {
            ProductDTO productDTO = new ProductDTO();
            productDTO.setProductName(productInfo.getProductName());
            productDTO.setPrice(Integer.parseInt(productInfo.getPrice()));
            productDTO.setProductCode(productInfo.getPcode());
            productDTO.setReferenceCode(productInfo.getImageUrl());
            productDTO.setCreatedAt(Timestamp.valueOf(now));
            productDTOList.add(productDTO);
        }
        // 중복 확인 및 데이터베이스 삽입
        for (ProductDTO productDTO : productDTOList) {
            int count = productMapper.countByProductCode(productDTO.getProductCode());
            if (count == 0) {
                productMapper.insertProduct(productDTO);
            } else {
                log.info("Product with code " + productDTO.getProductCode() + " already exists.");
            }
        }

    }

    public List<ProductInfo> getStoredProducts() {

        List<ProductDTO> productDTOList = productMapper.getAllProducts();
        List<ProductInfo> productInfoList = new ArrayList<>();

        for (ProductDTO productDTO : productDTOList) {
            ProductInfo productInfo = new ProductInfo();

            productInfo.setPcode(productDTO.getProductCode());
            productInfo.setProductName(productDTO.getProductName());
            productInfo.setPrice(String.valueOf(productDTO.getPrice()));
            productInfo.setImageUrl(productDTO.getReferenceCode());
            productInfoList.add(productInfo);
        }

        return productInfoList;
    }

    public ProductInfo getProductByCode(String pcode) {
        ProductDTO productDTO = productMapper.getProductByCode(pcode);
        if (productDTO == null) {
            return null;
        }
        ProductInfo productInfo = new ProductInfo();
        productInfo.setPcode(productDTO.getProductCode());
        productInfo.setProductName(productDTO.getProductName());
        productInfo.setPrice(String.valueOf(productDTO.getPrice()));
        productInfo.setImageUrl(productDTO.getReferenceCode());
        return productInfo;
    }


    public LaptopSpecDTO getLaptopDetails(ProductInfo productInfo) {

        LaptopSpecDTO laptopSpecDTO = new LaptopSpecDTO();

        try {
            String url = PRODUCT_DETAILS_URL;
            String referer = "https://prod.danawa.com/info/?pcode=" + productInfo.getPcode() + "&cate=112758";
            String bodyData = "pcode=" + productInfo.getPcode() + "&cate1=860&cate2=869&cate3=" + productInfo.getCate3();

            String responseHtml = sendPostRequest(url, referer, bodyData);
            Document doc = Jsoup.parse(responseHtml);


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
            laptopSpecDTO.setAdditionalFeatures(getSpecValue(doc, "부가 기능"));
            laptopSpecDTO.setUsage(getSpecValue(doc,"용도"));

        } catch (
                IOException e) {
            log.error("Error while getting product details", e);
        }
        return laptopSpecDTO;


    }

    private static String sendPostRequest(String url, String referer, String bodyData) throws IOException{
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpPost post = new HttpPost(url);
            post.setHeader("Referer", referer);
            post.setHeader("Content-Type", "application/x-www-form-urlencoded");

            StringEntity entity = new StringEntity(bodyData);
            post.setEntity(entity);

            try (CloseableHttpResponse response = client.execute(post))  {
                HttpEntity responseEntity = response.getEntity();
                return responseEntity != null ? new String(responseEntity.getContent().readAllBytes()) : "";
            }
        }
    }


    private static String getSpecValue(Document doc, String specName) {
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

}

