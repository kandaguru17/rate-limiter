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
            // same instance aana vera thread
            lock.lock();
            return rateLimit(request);
        } finally {
            lock.unlock();
        }
    }


    private boolean rateLimit(RequestContext request) {
        RateLimiterToken token = tokenRepository.getToken(request.getIpAddress()); //123
        long now = System.nanoTime();
        if (token == null) {
            token = new RateLimiterToken(bucketSize, now);
        }
        // refill token
        refillToken(token, now, request);
        if (token.getCount() <= 0) return false;
        System.err.println(tokenRepository.getToken(request.getIpAddress()));
        tokenRepository.getAndDecrement(request.getIpAddress());
        return true;
    }


    // t1 -- > x - 0
    // t2 -->  x - 0

    // t2 --> t2 ==> x - 1 --> true
    // t1 --> ==> x - 1    --> false


    private void refillToken(RateLimiterToken token, long now, RequestContext context) {
        long elapsedTime = now - token.getLastUpdatedNano();
        double tokensToAdd = (refillRate * (elapsedTime / 1e9));
        int tokenCount = Math.min(token.getCount() + (int) tokensToAdd, bucketSize);
        RateLimiterToken refilledToken = new RateLimiterToken(tokenCount, now);
        tokenRepository.save(context.getIpAddress(), refilledToken);
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
