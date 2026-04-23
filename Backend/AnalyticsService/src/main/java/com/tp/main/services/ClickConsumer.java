package com.tp.main.services;

import java.time.LocalDateTime;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.tp.main.dto.ClickEvent;
import com.tp.main.entities.ClickLog;
import com.tp.main.entities.LinkStats;
import com.tp.main.repository.ClickLogRepository;
import com.tp.main.repository.LinkStatRepository;

import nl.basjes.parse.useragent.UserAgent;
import nl.basjes.parse.useragent.UserAgentAnalyzer;

@Service
public class ClickConsumer {

	private final UserAgentAnalyzer uaa = UserAgentAnalyzer.newBuilder().withField(UserAgent.AGENT_NAME)
			.withField(UserAgent.OPERATING_SYSTEM_NAME_VERSION).withField(UserAgent.DEVICE_CLASS).withCache(10000)
			.build();

	@Autowired
	private LinkStatRepository linkStatRepo;

	@Autowired
	private ClickLogRepository clickLogRepo;
	
	@Autowired
	private AnalyticService analyticService;

	@KafkaListener(topics = "url-clicks", groupId = "analytics-group")
	public void consumerClick(ClickEvent event) {
		System.out.println("Processing click for: " + event.shortCode());
		System.out.println("Browser: " + event.userAgent());
		System.out.println("IP: " + event.ipAddress());

		// 1. Parse User Agent
		UserAgent agent = uaa.parse(event.userAgent());
		String browser = agent.getValue(UserAgent.AGENT_NAME);
		String os = agent.getValue(UserAgent.OPERATING_SYSTEM_NAME_VERSION);
		String device = agent.getValue(UserAgent.DEVICE_CLASS);

		// 2. SAVE INDIVIDUAL LOG (This is the "Scale-Proof" part)
		// This allows for Pageable/Infinite Scroll
		ClickLog log = new ClickLog();
		log.setShortCode(event.shortCode());
		log.setIpAddress(event.ipAddress());
		log.setBrowser(browser);
		log.setOs(os);
		log.setDevice(device);
		log.setClickedAt(event.timestamp());
		clickLogRepo.save(log);

		// 3. This calls your Service which updates Redis AND saves to ClickLog collection
	    analyticService.recordClick(event.shortCode(), log);

	    // 4. (Optional) Update the Summary Table in MongoDB
	    // We update ONLY the number, NO HISTORY ARRAY.
	    updateSummaryCounter(event.shortCode(), event.timestamp());

	    System.out.println("📊 Analytics Processed: " + browser + " | Total Incremented");
	}

	private void updateSummaryCounter(String shortCode, LocalDateTime timestamp) {
	    LinkStats record = linkStatRepo.findById(shortCode).orElseGet(() -> {
	        LinkStats newRecord = new LinkStats();
	        newRecord.setShortCode(shortCode);
	        newRecord.setTotalClick(0L);
	        return newRecord;
	    });

	    record.setTotalClick(record.getTotalClick() + 1);
	    record.setLastClickAt(timestamp);
	    // CRITICAL: Ensure your LinkStats Entity no longer has the clickHistory field!
	    linkStatRepo.save(record);
	}
}
