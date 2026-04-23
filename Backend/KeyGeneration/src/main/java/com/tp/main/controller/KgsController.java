package com.tp.main.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tp.main.entities.LongRangeResponse;
import com.tp.main.services.KgsService;

@RestController
@RequestMapping("/api/v1/kgs")
public class KgsController {

	@Autowired
    private KgsService kgsService;

    @GetMapping("/range")
    public ResponseEntity<LongRangeResponse> getNextRange(@RequestHeader("X-User-Header") String username) {
        // This calls the service logic that uses the KeyRange entity
        LongRangeResponse response = kgsService.reserveRange(username);
        return ResponseEntity.ok(response);
    }
}
