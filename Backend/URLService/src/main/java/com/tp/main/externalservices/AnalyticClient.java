package com.tp.main.externalservices;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "ANALYTICS-SERVICE")
public interface AnalyticClient {

	@DeleteMapping("/api/v3/analytics/remove/{shortCode}")
	public void removeAnalytics(@PathVariable String shortCode);
}
