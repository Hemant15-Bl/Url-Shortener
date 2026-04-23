package com.tp.main.services;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.tp.main.entities.ClickLog;
import com.tp.main.repository.ClickLogRepository;
import com.tp.main.repository.LinkStatRepository;

@Service
public class AnalyticService {

	@Autowired
	private RedisTemplate<String, String> redisTemplate;
	
	@Autowired
	private LinkStatRepository linkStatRepo;
	
	@Autowired
	private ClickLogRepository clickLogRepo;

	@Autowired
	private MongoTemplate mongoTemplate;
	
	private static final String CLICK_KEY = "click";
	
	

	public List<Map> getBrowserDistribution(String shortCode) {
	    Aggregation aggregation = Aggregation.newAggregation(
	        Aggregation.match(Criteria.where("shortCode").is(shortCode)),
	        Aggregation.group("browser").count().as("value"),
	        Aggregation.project("value").and("_id").as("name")
	    );

	    return mongoTemplate.aggregate(aggregation, "click_log", Map.class).getMappedResults();
	}
	
	public void recordClick(String shortCode, ClickLog rawData) {
		// Increment in Redis (Instant)
        redisTemplate.opsForValue().increment(CLICK_KEY + shortCode);
        
        this.clickLogRepo.save(rawData);
	}
	
	public Page<ClickLog> getClickHistory(String shortCode, int page, int size) {
		Pageable pageable = PageRequest.of(page, size, Sort.by("clickedAt").descending());
	    return clickLogRepo.findByShortCode(shortCode, pageable);
	}
	
	public void deleteLinkData(String shortCode) {
	    // 1. Delete the Link summary
	    linkStatRepo.deleteByShortCode(shortCode);
	    
//	    // 2. Delete all historical click logs (Cleanup)
	    Query query = new Query(Criteria.where("shortCode").is(shortCode));
	    mongoTemplate.remove(query, "click_log");
	    
	    // 3. Optional: Clear Redis cache if you store redirect mappings there
	    redisTemplate.delete("click" + shortCode);
	}
	
}
