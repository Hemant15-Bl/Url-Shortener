CREATE TABLE kgs_audit_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(255),
    range_start BIGINT,
    range_end BIGINT,
    reserved_at DATETIME
);