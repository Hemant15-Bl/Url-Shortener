package com.tp.main.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import com.tp.main.entities.KeyRange;

import jakarta.persistence.LockModeType;

public interface KgsRepository extends JpaRepository<KeyRange, Integer>{

	@Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT k FROM KeyRange k WHERE k.id = :id")
    Optional<KeyRange> findAndLockById(Integer id);
}
