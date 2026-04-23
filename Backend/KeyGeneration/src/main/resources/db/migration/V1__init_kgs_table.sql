CREATE TABLE key_storage (
    id INT PRIMARY KEY,
    next_start_value BIGINT NOT NULL
);

-- Initialize the global counter
INSERT INTO key_storage (id, next_start_value) 
VALUES (1, 1000000);