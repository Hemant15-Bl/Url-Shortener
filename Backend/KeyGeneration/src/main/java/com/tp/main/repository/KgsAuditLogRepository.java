package com.tp.main.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tp.main.entities.KgsAuditLog;

public interface KgsAuditLogRepository extends JpaRepository<KgsAuditLog, Long>{

}
