package com.multi.laptellect.product.service;

import com.multi.laptellect.product.model.dto.ProductDTO;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class CrawlingService {

    private static final String URL = "https://prod.danawa.com/list/ajax/getProductList.ajax.php";


    @Autowired
    private ProductService productService;

    public void crawlAndSaveProducts(int pages) throws IOException {
        List<ProductInfo> productList = crawlProducts(pages);
        saveProductsToDB(productList);
    }

    /**
     * 크롤링하여 제품 정보를 반환하는 메소드
     * @param pages 크롤링할 페이지 수
     * @return 크롤링된 제품 정보 리스트
     * @throws IOException HTTP 요청 실패 시 발생
     */

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
    /**
     * POST 요청을 보내는 메소드
     * @param httpClient HTTP 클라이언트 객체
     * @param page 요청할 페이지 번호
     * @return 응답 문자열
     * @throws IOException 요청 실패 시 발생
     */
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
     * @param html 응답 HTML 문자열
     * @param productList 제품 정보 리스트
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

    /**
     * HTML 요소에서 제품 정보를 추출하는 메소드
     * @param product HTML 요소
     * @return 추출된 제품 정보
     */
    private ProductInfo extractProductInfo(Element product) {
        String productName = product.select(".prod_name a").text().trim();
        String price = product.select(".price_sect strong").text().trim().replace(",", "");
        String productCode  = product.attr("id").replace("productItem", "");
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
        for (ProductInfo productInfo : productList) {
            ProductDTO productDTO = new ProductDTO();
            productDTO.setProductName(productInfo.getProductName());
            productDTO.setPrice(Integer.parseInt(productInfo.getPrice()));
            productDTO.setProductCode(productInfo.getPcode());
            productDTO.setReferenceCode(productInfo.getImageUrl());
            productDTOList.add(productDTO);
        }
        productService.saveProduct(productDTOList);

    }

}


