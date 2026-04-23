package com.tp.main.entities;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Document(collection = "url_mappings")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@CompoundIndex(name = "user_url_idx", def = "{'createdBy': 1, 'shortCode': 1}")
public class UrlMapping {

	@Id
    private String id; // Internal Mongo ID

    @Indexed(unique = true) 
    private String shortCode; // The Base62 string (e.g., "7fGz2")

    private String originalUrl;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime expiresAt;
    
    private String createdBy;
}
