package com.tp.main.services;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tp.main.externalservices.KgsClient;
import com.tp.main.externalservices.LongRangeResponse;

@Service
public class UserBuffer {

	@Autowired
    private KgsClient kgsClient; // Feign Client

    private long currentId = 0;
    private long maxId = 0;
    
    public UserBuffer(KgsClient kgsClient) {
        this.kgsClient = kgsClient;
        this.currentId = 0;
        this.maxId = 0;
    }

    public synchronized long getNextId(String username) {
        if (currentId >= maxId) {
            refreshRange(username);
        }
        return currentId++;
    }

    private void refreshRange(String username) {
        LongRangeResponse response = kgsClient.getNewRange(username);
        this.currentId = response.getStart();
        this.maxId = response.getEnd();
    }
}
