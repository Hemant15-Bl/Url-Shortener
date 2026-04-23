package com.tp.main.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.tp.main.entities.LinkStats;

public interface LinkStatRepository extends MongoRepository<LinkStats, String>{

	Optional<LinkStats> findByShortCode(String shortCode);
	void deleteByShortCode(String shortCode);
	List<LinkStats> findByUsername(String username);

}
