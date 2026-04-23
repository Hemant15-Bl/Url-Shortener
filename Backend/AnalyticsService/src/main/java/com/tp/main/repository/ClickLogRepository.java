package com.tp.main.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.tp.main.entities.ClickLog;

public interface ClickLogRepository extends MongoRepository<ClickLog, String>{

	Page<ClickLog> findByShortCode(String shortCode, Pageable pageable);
}
