package com.tp.main.controller;

import java.util.HashMap;


import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tp.main.entities.ClickLog;
import com.tp.main.entities.LinkStats;
import com.tp.main.repository.LinkStatRepository;
import com.tp.main.services.AnalyticService;

@RestController
@RequestMapping("/api/v3/analytics")
public class StatsController {

	@Autowired
	private LinkStatRepository linkStatRepo;
	
	@Autowired
	private AnalyticService analyticService;
	
	@GetMapping("/{shortCode}")
	public ResponseEntity<Map<String, Object>> getStats(@PathVariable String shortCode){
		return linkStatRepo.findById(shortCode).map(stats -> {
	        Map<String, Object> response = new HashMap<>();
	        response.put("totalClick", stats.getTotalClick());
	        response.put("lastClickAt", stats.getLastClickAt());
	        // Fetch chart data on the fly from the Log collection
	        List<Map> chartData = analyticService.getBrowserDistribution(shortCode);
	        response.put("chartData", chartData);
	        return ResponseEntity.ok(response);
	    }).orElse(ResponseEntity.notFound().build());
	}
	
	@GetMapping("/my-links")
    public ResponseEntity<List<LinkStats>> getMyLinks(@RequestHeader("X-User-Header") String username) {
        return ResponseEntity.ok(this.linkStatRepo.findByUsername(username));
    }
	
	@GetMapping("/statsall")
	public ResponseEntity<List<LinkStats>> getAllStats(){
		return new ResponseEntity<>(this.linkStatRepo.findAll(), HttpStatus.OK);
	}
	
//	@GetMapping("/{shortCode}/summary")
//	public ResponseEntity<Map<String, Object>> getClickSummary(@PathVariable String shortCode) {
//	    return linkStatRepo.findById(shortCode).map(stats -> {
//	        Map<String, Object> summary = new HashMap<>();
//	        summary.put("totalClicks", stats.getTotalClick());
//	        
//	        // Group by Browser using Java Streams
//	        Map<String, Long> browserStats = stats.getClickHistory().stream()
//	            .collect(Collectors.groupingBy(ClickDetail::getBrowser, Collectors.counting()));
//	        
//	        summary.put("browsers", browserStats);
//	        return ResponseEntity.ok(summary);
//	    }).orElse(ResponseEntity.notFound().build());
//	}
	
	@GetMapping("/{shortCode}/history")
	public ResponseEntity<Page<ClickLog>> getHistory(
	    @PathVariable String shortCode,
	    @RequestParam(defaultValue = "0") int page,
	    @RequestParam(defaultValue = "10") int size) {
	    
	    return ResponseEntity.ok(analyticService.getClickHistory(shortCode, page, size));
	}
	
	@DeleteMapping("/remove/{shortCode}")
	public ResponseEntity<Void> deleteLink(@PathVariable String shortCode) {
	    analyticService.deleteLinkData(shortCode);
	    return ResponseEntity.noContent().build();
	}
}
