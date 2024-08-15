package com.multi.laptellect.recommend.txttag.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Please explain the class!!
 *
 * @author : 이강석
 * @fileName : CpuConfig
 * @since : 2024-08-14
 */
@Data
@Component
@ConfigurationProperties(prefix = "cpus")
public class CpuConfig {
    private Map<String, Integer> cpuMark;
}
