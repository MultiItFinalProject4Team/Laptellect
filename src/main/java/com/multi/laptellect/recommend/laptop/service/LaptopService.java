package com.multi.laptellect.recommend.laptop.service;

import com.multi.laptellect.recommend.laptop.model.dao.LaptopDAO;
import com.multi.laptellect.recommend.laptop.model.dto.LaptopDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class LaptopService {

    @Autowired
    private LaptopDAO laptopDAO;

    public List<String> getCategories() { //설문 페이지에 필요한 정보들을 model에 담아서 전달
        return Arrays.asList("게이밍", "프로그래밍", "그래픽/디자인", "학생용");
    }

    public List<String> getCpuBrands() {
        return Arrays.asList("인텔", "라이젠");
    }

    public List<String> getGpuBrands() {
        return Arrays.asList("엔비디아", "AMD");
    }

    public List<String> getPriceRanges() {
        return Arrays.asList("50만원 이하", "100~200만원", "200만원 이상");
    }

    public List<LaptopDTO> getRecommendedLaptops(Map<String, Object> surveyResults) { //설문 결과를 받아서 추천 노트북 목록을 반환
        List<String> tags = new ArrayList<>();

        addTagsFromList(tags, (List<String>) surveyResults.get("categories"));
        addTag(tags, (String) surveyResults.get("priceRange"));
        addTag(tags, (String) surveyResults.get("cpuBrand"));
        addTag(tags, (String) surveyResults.get("gpuBrand")); //설문 결과에 따라 태그 목록을 만들어서 노트북 검색

        if (Boolean.TRUE.equals(surveyResults.get("asImportant"))) { //AS 중요 체크박스가 체크되어 있으면
            tags.add("AS 중요");
        }

        return laptopDAO.findLaptopsByTags(tags); //태그 목록으로 노트북 검색
    }
    // 리스트에서 태그를 추가
    private void addTagsFromList(List<String> tags, List<String> newTags) {
        if (newTags != null) {
            tags.addAll(newTags);
        }
    }
    // 단일 태그를 추가
    private void addTag(List<String> tags, String tag) {
        if (tag != null && !tag.isEmpty()) {
            tags.add(tag);
        }
    }

//    public Map<String, Object> getLaptopDetail(Long id) { //나중에 상세 정보도 넣을 것 같아서 일단 넣어둠
//        return laptopDAO.findLaptopDetailById(id);
//    }
}

//getRecommendedLaptops(Map<String, Object> surveyResults) 를 사용한 이유 : 사용자가 선택한 조건들을 받아서 그 조건들에 맞는 노트북을 추천해주기 위함

//addTag(); 란 메소드는 사용자가 선택한 조건들을 태그로 만들어서 리스트에 추가해주는 메소드이다. 사용자가 선택한 조건이 없다면 태그를 추가하지 않는다.