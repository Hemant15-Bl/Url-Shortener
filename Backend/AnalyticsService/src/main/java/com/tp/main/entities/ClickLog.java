package com.tp.main.entities;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;

@Document(collection = "click_log")
@Data
public class ClickLog {

	@Id
    private String id;
    @Indexed // Crucial for Pageable performance
    private String shortCode;
    private String ipAddress;
    private String browser;
    private String os;
    private String device;
    private LocalDateTime clickedAt;
}
