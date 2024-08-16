package com.multi.laptellect.config.api;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * 공공 데이터포털 API Config
 *
 * @author : 이강석
 * @fileName : DataPortalConfig
 * @since : 2024-07-30
 */
@Configuration
@Getter
public class DataPortalConfig {
    @Value("${spring.data.base-url}")
    private String dataPortalURL;

    @Value("${spring.data.api.business-registration-number.api-key}")
    private String dataPortalBusinessApiKey;

    public String getApiUrl() {
        return dataPortalURL + dataPortalBusinessApiKey;
    }
}
