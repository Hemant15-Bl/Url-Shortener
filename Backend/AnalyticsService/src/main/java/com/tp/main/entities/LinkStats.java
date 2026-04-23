package com.tp.main.entities;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Document(collection = "link_stats")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LinkStats {

	@Id
	private String shortCode;
	
	private long totalClick;
	
	private String username;
	
	private LocalDateTime lastClickAt;
	
}
