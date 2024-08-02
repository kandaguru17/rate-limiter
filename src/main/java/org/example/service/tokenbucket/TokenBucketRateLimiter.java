package org.example.service.tokenbucket;

import org.example.RateLimiter;
import org.example.models.RequestContext;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class TokenBucketRateLimiter implements RateLimiter {

    private final int bucketSize;

    private final int refillRate;

    private TokenRepository tokenRepository;

    private final Map<String, Lock> locks = new ConcurrentHashMap<>();

    public TokenBucketRateLimiter(int bucketSize, int refillRate) {
        this.bucketSize = bucketSize;
        this.refillRate = refillRate;
        this.tokenRepository = new InMemoryTokenRepository();
    }

    @Override
    public boolean allowRequest(RequestContext request) {
        Objects.requireNonNull(request);
        Lock lock = locks.computeIfAbsent(request.getIpAddress(), (k) -> new ReentrantLock());
        try {
            lock.lock();
            return rateLimit(request);
        } finally {
            lock.unlock();
        }
    }

    private boolean rateLimit(RequestContext request) {
        RateLimiterToken token = tokenRepository.getToken(request.getIpAddress());
        long now = System.nanoTime();
        if (token == null) {
            token = new RateLimiterToken();
            token.setCount(bucketSize);
        }
        // refill token
        refillToken(token, now);
        if (token.getCount() <= 0) return false;
        token.setCount(token.getCount() - 1);
        tokenRepository.save(request.getIpAddress(), token);
        return true;
    }

    private void refillToken(RateLimiterToken token, long now) {
        long elapsedTime = now - token.getLastUpdatedNano();
        double tokensToAdd = (refillRate * (elapsedTime / 1e9));
        token.setCount(Math.min(token.getCount() + (int) tokensToAdd, bucketSize));
        token.setLastUpdatedNano(now);
    }


    public int getBucketSize() {
        return bucketSize;
    }

    public int getRefillRate() {
        return refillRate;
    }

    public void setTokenRepository(TokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
    }

    TokenRepository getTokenRepository() {
        return tokenRepository;
    }
}
