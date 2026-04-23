package com.tp.main.externalservices;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.tp.main.dto.SafeBrowsingRequest;
import com.tp.main.dto.SafeBrowsingResponse;

@FeignClient(name = "safe-browsing", url = "https://safebrowsing.googleapis.com/v4")
public interface SafeBrowseringClient {

	@PostMapping("/threatMatches:find?key={apiKey}")
    SafeBrowsingResponse checkUrl(@RequestParam("key") String apiKey, @RequestBody SafeBrowsingRequest request);

}
