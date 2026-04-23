package com.tp.main.entities;


public class LongRangeResponse {

	private long start;
    private long end;
    
    public LongRangeResponse() {}

    public LongRangeResponse(long start, long end) {
        this.start = start;
        this.end = end;
    }

    // Getters so Jackson can convert this to JSON
    public long getStart() { return start; }
    public long getEnd() { return end; }
}
