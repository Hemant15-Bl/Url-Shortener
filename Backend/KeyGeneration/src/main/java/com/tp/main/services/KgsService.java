package com.tp.main.services;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestHeader;

import com.tp.main.entities.KeyRange;
import com.tp.main.entities.KgsAuditLog;
import com.tp.main.entities.LongRangeResponse;
import com.tp.main.repository.KgsAuditLogRepository;
import com.tp.main.repository.KgsRepository;

@Service
public class KgsService {

	@Autowired
    private KgsRepository repository;

	@Autowired
    private KgsAuditLogRepository auditLogRepository;

    private static final int RANGE_SIZE = 1000;

    @Transactional
    public LongRangeResponse reserveRange(String username) {
        // 1. Lock the row so no other service can read/write it
        KeyRange range = repository.findAndLockById(1)
                .orElseThrow(() -> new RuntimeException("KGS Table not initialized"));

        long startValue = range.getNextStartValue();
        long endValue = startValue + RANGE_SIZE - 1;

        // 2. Increment the start value for the NEXT request
        range.setNextStartValue(startValue + RANGE_SIZE);
        repository.save(range);
        
        KgsAuditLog audit = new KgsAuditLog();
        audit.setUsername(username);
        audit.setRangeStart(startValue);
        audit.setRangeEnd(endValue);
        audit.setReservedAt(LocalDateTime.now());
        auditLogRepository.save(audit);
        // 3. Return the range to the URL Service
        return new LongRangeResponse(startValue, endValue);
    }
}
