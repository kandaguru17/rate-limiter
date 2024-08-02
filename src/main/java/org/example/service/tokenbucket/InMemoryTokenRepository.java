package org.example.service.tokenbucket;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryTokenRepository implements TokenRepository {

    Map<String, RateLimiterToken> tokenStore = new ConcurrentHashMap<>();

    @Override
    public RateLimiterToken getToken(String ipAddress) {
        return tokenStore.get(ipAddress);
    }

    @Override
    public void save(String ipAddress ,RateLimiterToken token) {
       tokenStore.put(ipAddress,token);
    }
}
