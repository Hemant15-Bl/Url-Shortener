package com.tp.main.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.tp.main.entities.UrlMapping;

public interface UrlRepository extends MongoRepository<UrlMapping, String>{

	Optional<UrlMapping> findByShortCode(String shortCode);
	void deleteByShortCode(String shortCode);
	List<UrlMapping> findByCreatedBy(String username);
}
