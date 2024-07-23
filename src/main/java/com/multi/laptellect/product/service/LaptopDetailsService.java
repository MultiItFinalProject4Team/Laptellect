package com.multi.laptellect.product.service;

import com.multi.laptellect.product.model.dto.LaptopSpecDTO;
import com.multi.laptellect.product.model.dto.ProductInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.IOException;


@Slf4j
@Service
public class LaptopDetailsService {

    public LaptopSpecDTO getProductDetails(ProductInfo productInfo) {

        LaptopSpecDTO prodcutDTO = new LaptopSpecDTO();

        try {
            String url = "https://prod.danawa.com/info/ajax/getProductDescription.ajax.php";
            String referer = "https://prod.danawa.com/info/?pcode=" + productInfo.getPcode() + "&cate=112758";
            String bodyData = "pcode=" + productInfo.getPcode() + "&cate1=860&cate2=869&cate3=" + productInfo.getCate3();

            String responseHtml = sendPostRequest(url, referer, bodyData);

            Document doc = Jsoup.parse(responseHtml);


            prodcutDTO.setOs(getSpecValue(doc, "운영체제(OS)"));
            prodcutDTO.setCpuManufacturer(getSpecValue(doc, "CPU 제조사"));
            prodcutDTO.setCpuType(getSpecValue(doc, "CPU 종류"));
            prodcutDTO.setCpuCodeName(getSpecValue(doc, "CPU 코드명"));
            prodcutDTO.setCpuNumber(getSpecValue(doc, "CPU 넘버"));
            prodcutDTO.setGpuType(getSpecValue(doc, "GPU 종류"));
            prodcutDTO.setGpuManufacturer(getSpecValue(doc, "GPU 제조사"));
            prodcutDTO.setGpuChipset(getSpecValue(doc, "GPU 칩셋"));
            prodcutDTO.setRamType(getSpecValue(doc, "램 타입"));
            prodcutDTO.setRamSize(getSpecValue(doc, "램 용량"));
            prodcutDTO.setScreenSize(getSpecValue(doc, "화면 크기"));
            prodcutDTO.setScreenResolution(getSpecValue(doc, "해상도"));
            prodcutDTO.setStorageType(getSpecValue(doc, "저장장치 종류"));
            prodcutDTO.setStorageCapacity(getSpecValue(doc, "저장 용량"));
            prodcutDTO.setConvenienceFeatures(getSpecValue(doc, "패널 표면 처리"));
            prodcutDTO.setAdditionalFeatures(getSpecValue(doc, "부가 기능"));

        } catch (
                IOException e) {
            log.error("Error while getting product details", e);
        }
        return prodcutDTO;
    }

    private static String sendPostRequest(String url, String referer, String bodyData) throws IOException{
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

    private String getAdditionalFeatures(Document doc) {
        StringBuilder features = new StringBuilder();
        Elements rows = doc.select("table.spec_tbl tr");
        boolean additionalFeaturesStarted = false;

        for (Element row : rows) {
            Elements th = row.select("th");
            Elements td = row.select("td");

            if (additionalFeaturesStarted) {
                if (th.hasText() && !td.hasText()) {
                    break;
                }
                features.append(th.text()).append(": ").append(td.text()).append("\n");
            }

            if (th.text().equals("부가기능")) {
                additionalFeaturesStarted = true;
            }
        }
        return features.toString().trim();
    }

}
