package com.multi.laptellect.product.service;

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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
public class ProductService {

    private static final String URL = "https://prod.danawa.com/list/ajax/getProductList.ajax.php";

    public List<ProductInfo> crawlProducts(int pages) throws IOException {
        List<ProductInfo> productList = new ArrayList<>();
        Set<String> seenProductNames = new HashSet<>();

        //HTTP 요청을 보내기 위해 사용하는 클라이언트
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            for (int page = 1; page <= pages; page++) {
                HttpPost post = new HttpPost(URL);
                post.setHeader("Referer", "https://prod.danawa.com/list/?cate=112758&15main_11_02=");

                StringEntity params = new StringEntity("page=" + page +
                        "&listCategoryCode=758&categoryCode=758&physicsCate1=860&physicsCate2=869&sortMethod=BoardCount&viewMethod=LIST&listCount=10");
                post.setEntity(params);
                post.setHeader("Content-type", "application/x-www-form-urlencoded");

                try (CloseableHttpResponse response = httpClient.execute(post)) {
                    HttpEntity entity = response.getEntity();
                    if (entity != null) {
                        String responseString = EntityUtils.toString(entity);
                        System.out.println("Page: " + page);
                        log.info("Page: "+ page);
                        parseHtml(responseString, productList);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        for (ProductInfo product : productList) {
            System.out.println(product);
        }
        return productList;
    }


    private static void parseHtml(String html, List<ProductInfo> productList) {
        Document doc = Jsoup.parse(html);
        Elements productElements = doc.select(".prod_item.prod_layer");

        for (Element product : productElements) {
            String productName = product.select(".prod_name a").text().trim(); //상품 이름
            String registrationMonth = product.select(".mt_date dd").text().trim(); // 등록일
            String price = product.select(".price_sect strong").text().trim().replace(",", ""); //가격
            String categoryCode = product.select(".prod_pricelist").attr("data-cate"); //카테고리 코드
            String pcode = product.attr("id").replace("productItem", ""); // 상품 코드
            String imageUrl = product.select(".thumb_image img").attr("data-original"); //이미지 추출

            Elements specElements = product.select(".spec_list a");
            String cate3 = specElements.size() > 2 ? specElements.get(2).text().trim() : "";

            if (imageUrl == null || imageUrl.isEmpty()) {
                imageUrl = product.select(".thumb_image img").attr("src");
            }

            if (productName.isEmpty() || price.isEmpty() || pcode.isEmpty() || cate3.isEmpty()) {
                continue;
            }



            String firstPrice = price.split(" ")[0];



            ProductInfo productInfo = new ProductInfo();
            productInfo.setPcode(pcode);
            productInfo.setProductName(productName);
            productInfo.setRegistrationMonth(registrationMonth);
            productInfo.setPrice(firstPrice);
            productInfo.setCategoryCode(categoryCode);
            productInfo.setImageUrl(imageUrl);
            productInfo.setCate3(cate3);

            // productList에 ProductInfo 객체 추가
            productList.add(productInfo);

        }

    }
}


