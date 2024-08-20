package com.multi.laptellect.recommend.laptop.model.dto;

import com.multi.laptellect.product.model.dto.laptop.LaptopSpecDTO;

import java.util.ArrayList;

public class CacheData {
    private CurationDTO curationDTO;
    private ArrayList<LaptopSpecDTO> recommendations;

    public CacheData() {}

    public CacheData(CurationDTO curationDTO, ArrayList<LaptopSpecDTO> recommendations) {
        this.curationDTO = curationDTO;
        this.recommendations = recommendations;
    }

    public CurationDTO getCurationDTO() {
        return curationDTO;
    }

    public void setCurationDTO(CurationDTO curationDTO) {
        this.curationDTO = curationDTO;
    }

    public ArrayList<LaptopSpecDTO> getRecommendations() {
        return recommendations;
    }

    public void setRecommendations(ArrayList<LaptopSpecDTO> recommendations) {
        this.recommendations = recommendations;
    }
}