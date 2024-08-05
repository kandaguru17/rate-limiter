package org.example.service.tokenbucket;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

public class InMemoryTokenRepository implements TokenRepository {

    Map<String, AtomicReference<RateLimiterToken>> tokenStore = new ConcurrentHashMap<>();

    @Override
    public RateLimiterToken getToken(String ipAddress) {
        AtomicReference<RateLimiterToken> atomicReference = tokenStore.get(ipAddress);
        return atomicReference == null ? null : atomicReference.get();
    }

    @Override
    public void save(String ipAddress, RateLimiterToken token) {
        if (tokenStore.containsKey(ipAddress)) {
            tokenStore.get(ipAddress).set(token);
        } else {
            tokenStore.put(ipAddress, new AtomicReference<>(token));
        }
    }

    @Override
    public void getAndDecrement(String ipAddress) {
        AtomicReference<RateLimiterToken> tokenAtomicReference;
        RateLimiterToken rateLimiterToken;

        while (true) {
            tokenStore.putIfAbsent(ipAddress, new AtomicReference<>(new RateLimiterToken(2, System.nanoTime())));
            tokenAtomicReference = tokenStore.get(ipAddress);
            rateLimiterToken = new RateLimiterToken(tokenAtomicReference.get().getCount() - 1, System.nanoTime());
            if (tokenAtomicReference.compareAndSet(tokenAtomicReference.get(), rateLimiterToken)) {
                break;
            }
        }
    }
}
