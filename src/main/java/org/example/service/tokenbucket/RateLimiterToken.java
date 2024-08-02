package org.example.service.tokenbucket;

public class RateLimiterToken {

    private int count;

    private long lastUpdatedNano;

    public RateLimiterToken(int i, long nanoTime) {
        this.count = i;
        this.lastUpdatedNano = nanoTime;
    }

    public RateLimiterToken() {

    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public long getLastUpdatedNano() {
        return lastUpdatedNano;
    }

    public void setLastUpdatedNano(long nano) {
        this.lastUpdatedNano = nano;
    }
}
