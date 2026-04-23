package com.tp.main.dto;


public record SafeBrowsingRequest(ClientInfo client, ThreatInfo threatInfo) { }