package com.tp.main.dto;

import java.util.List;

public record SafeBrowsingResponse(List<ThreatMatch> matches) {

}
