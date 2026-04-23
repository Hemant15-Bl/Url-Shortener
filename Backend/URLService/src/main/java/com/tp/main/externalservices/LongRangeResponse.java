package com.tp.main.externalservices;

import lombok.Data;

@Data
public class LongRangeResponse {

	private long start;
    private long end;
    
    public LongRangeResponse(long start, long end) {
        this.start = start;
        this.end = end;
    }

}
