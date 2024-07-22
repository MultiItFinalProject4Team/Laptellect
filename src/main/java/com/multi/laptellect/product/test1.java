package com.multi.laptellect.product;

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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class test1 {
    public static void main(String[] args) {
        String url = "https://prod.danawa.com/list/ajax/getProductList.ajax.php";
        List<ProductInfo> productList = new ArrayList<>();

        // Create HttpClient
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            for (int page = 1; page <= 2; page++) {
                // Create HttpPost request
                HttpPost post = new HttpPost(url);

                // Set headers
                post.setHeader("Referer", "https://prod.danawa.com/list/?cate=112758");

                // Set POST parameters
                StringEntity params = new StringEntity(
                        "page=" + page + "&listCategoryCode=758&categoryCode=758&physicsCate1=860&physicsCate2=869&sortMethod=NEW&viewMethod=LIST&listCount=30");
                post.setEntity(params);
                post.setHeader("Content-type", "application/x-www-form-urlencoded");

                // Execute the request
                try (CloseableHttpResponse response = httpClient.execute(post)) {
                    HttpEntity entity = response.getEntity();
                    if (entity != null) {
                        String responseString = EntityUtils.toString(entity);
                        System.out.println("Page: " + page);
                        parseHtml(responseString, productList);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 추출한 상품 정보 출력
        for (ProductInfo product : productList) {
            System.out.println(product);
        }

        // HtmlParser3 호출
        for (ProductInfo product : productList) {
            try {
                test2.getProductDetails(product);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static void parseHtml(String html, List<ProductInfo> productList) {
        Document doc = Jsoup.parse(html);
        Elements productElements = doc.select(".prod_item.prod_layer");

        for (Element product : productElements) {
            String productName = product.select(".prod_name a").text().trim();
            String registrationMonth = product.select(".mt_date dd").text().trim();
            String price = product.select(".price_sect strong").text().trim().replace(",", "");
            String categoryCode = product.select(".prod_pricelist").attr("data-cate");
            String pcode = product.attr("id").replace("productItem", "");

            // cate3 값 추출
            Elements specElements = product.select(".spec_list a");
            String cate3 = specElements.size() > 2 ? specElements.get(2).text().trim() : "";
            String imageUrl = product.select(".thumb_image img").attr("data-original"); //이미지 추출

            // 필수 값이 없는 항목은 건너뛰기
            if (productName.isEmpty() || price.isEmpty() || pcode.isEmpty() || cate3.isEmpty()) {
                continue;
            }

            productList.add(new ProductInfo(pcode, cate3));
        }
    }

    static class ProductInfo {
        String pcode;
        String cate3;

        public ProductInfo(String pcode, String cate3) {
            this.pcode = pcode;
            this.cate3 = cate3;
        }

        @Override
        public String toString() {
            return "ProductInfo{" +
                    "pcode='" + pcode + '\'' +
                    ", cate3='" + cate3 + '\'' +
                    '}';
        }
    }
}