package org.example.service.tokenbucket;


public interface TokenRepository {

    RateLimiterToken getToken(String ipAddress);

    void save(String ipAddress, RateLimiterToken token);

    void getAndDecrement(String ipAddress);
}
