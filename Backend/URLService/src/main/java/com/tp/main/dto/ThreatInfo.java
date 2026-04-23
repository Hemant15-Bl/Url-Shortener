package com.tp.main.dto;

import java.util.List;

public record ThreatInfo(List<String> threatTypes, List<String> platformTypes, List<String> threatEntryTypes, List<ThreatEntry> threatEntries) {

}
