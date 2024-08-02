package org.example.service.tokenbucket;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

public class InMemoryTokenRepository implements TokenRepository {

    Map<String, AtomicReference<RateLimiterToken>> tokenStore = new ConcurrentHashMap<>();

    @Override
    public RateLimiterToken getToken(String ipAddress) {
        AtomicReference<RateLimiterToken> atomicReference = tokenStore.get(ipAddress);
        return atomicReference == null ? null: atomicReference.get();
    }

    @Override
    public void save(String ipAddress, RateLimiterToken token) {
        // lock
        tokenStore.put(ipAddress, new AtomicReference<>(token));
        // unlock
    }

    @Override
    public void getAndDecrement(String ipAddress) {
        AtomicReference<RateLimiterToken> tokenAtomicReference;
        RateLimiterToken rateLimiterToken;
        do {
            tokenStore.putIfAbsent(ipAddress,new AtomicReference<>(new RateLimiterToken(1,System.nanoTime())));
            tokenAtomicReference = tokenStore.get(ipAddress);
            rateLimiterToken = new RateLimiterToken();
            rateLimiterToken.setCount(tokenAtomicReference.get().getCount() - 1);
            //actual , newValue
        } while (!tokenAtomicReference.compareAndSet(tokenAtomicReference.get(), rateLimiterToken));
    }
}
