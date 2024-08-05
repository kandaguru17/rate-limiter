package org.example.service.tokenbucket;

public final class RateLimiterToken {

    private final int count;

    private final long lastUpdatedNano;

     RateLimiterToken() {
        this.count = 2;
        this.lastUpdatedNano= System.nanoTime();
    }

    public RateLimiterToken(int count, long lastUpdatedNano) {
        this.count = count;
        this.lastUpdatedNano = lastUpdatedNano;
    }

    public int getCount() {
        return count;
    }

    public long getLastUpdatedNano() {
        return lastUpdatedNano;
    }

}
