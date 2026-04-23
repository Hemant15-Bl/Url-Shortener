package com.tp.main.externalservices;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import com.tp.main.config.FeignClientConfig;

@FeignClient(name = "KEY-GENERATION", configuration = FeignClientConfig.class)
public interface KgsClient {

	@GetMapping("/api/v1/kgs/range")
    LongRangeResponse getNewRange(@RequestHeader("X-User-Header") String username);
}
