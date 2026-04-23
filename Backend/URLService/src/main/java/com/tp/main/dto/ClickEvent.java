package com.tp.main.dto;

import java.time.LocalDateTime;

public record ClickEvent(
		String shortCode,
		String ipAddress,
		String userAgent,
		LocalDateTime timestamp
		) {

}
