package com.multi.laptellect.product;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

public class test2 {

    public static void getProductDetails(test1.ProductInfo productInfo) throws IOException {
        String url = "https://prod.danawa.com/info/ajax/getProductDescription.ajax.php";
        String referer = "https://prod.danawa.com/info/?pcode=" + productInfo.pcode + "&cate=112758";
        String bodyData = "pcode=" + productInfo.pcode + "&cate1=860&cate2=869&cate3=" + productInfo.cate3;

        // HTTP POST 요청 보내기
        String responseHtml = sendPostRequest(url, referer, bodyData);

        // HTML 파싱하기
        Document doc = Jsoup.parse(responseHtml);

        String os = getSpecValue(doc, "운영체제(OS)");
        String cpuManufacturer = getSpecValue(doc, "CPU 제조사");
        String cpuType = getSpecValue(doc, "CPU 종류");
        String cpuCodeName = getSpecValue(doc, "CPU 코드명");
        String cpuNumber = getSpecValue(doc, "CPU 넘버");
        String gpuType = getSpecValue(doc, "GPU 종류");
        String gpuManufacturer = getSpecValue(doc, "GPU 제조사");
        String gpuChipset = getSpecValue(doc, "GPU 칩셋");
        String ramType = getSpecValue(doc, "램 타입");
        String ramSize = getSpecValue(doc, "램 용량");
        String screenSize = getSpecValue(doc, "화면 크기");
        String screenResolution = getSpecValue(doc, "해상도");
        String storageType = getSpecValue(doc, "저장장치 종류");
        String storageCapacity = getSpecValue(doc, "저장 용량");
        String convenienceFeatures = getSpecValue(doc, "패널 표면 처리");
        String additionalFeatures = getSpecValue(doc, "부가 기능");

        System.out.println("pcode: " + productInfo.pcode);
        System.out.println("cate3: " + productInfo.cate3);
        System.out.println("운영체제: " + os);
        System.out.println("CPU 제조사: " + cpuManufacturer);
        System.out.println("CPU 종류: " + cpuType);
        System.out.println("CPU 코드명: " + cpuCodeName);
        System.out.println("CPU 넘버: " + cpuNumber);
        System.out.println("GPU 종류: " + gpuType);
        System.out.println("GPU 제조사: " + gpuManufacturer);
        System.out.println("GPU 칩셋: " + gpuChipset);
        System.out.println("램 타입: " + ramType);
        System.out.println("램 용량: " + ramSize);
        System.out.println("화면 크기: " + screenSize);
        System.out.println("해상도: " + screenResolution);
        System.out.println("저장장치 종류: " + storageType);
        System.out.println("저장 용량: " + storageCapacity);
        System.out.println("패널 표면 처리: " + convenienceFeatures);
        System.out.println("부가기능: " + additionalFeatures);
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

    private static String getAdditionalFeatures(Document doc) {
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