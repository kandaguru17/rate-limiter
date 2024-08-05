package org.example.service.tokenbucket;


import org.example.models.RequestContext;
import org.junit.jupiter.api.Test;

import static org.example.utils.TestUtils.*;
import static org.junit.jupiter.api.Assertions.*;


public class TokenBucketRateLimiterIntegrationTest {

    private RequestContext request;

    @Test
    void initializeRateLimiterTest(){
        TokenBucketRateLimiter rateLimiter = new TokenBucketRateLimiter(MAX_BUCKET_SIZE, REFILL_RATE);
        request = new RequestContext();
        assertNotNull(rateLimiter);
        assertNotNull(rateLimiter.getTokenRepository());;
        assertEquals(rateLimiter.getBucketSize(), MAX_BUCKET_SIZE);
        assertEquals(rateLimiter.getRefillRate(), REFILL_RATE);
    }

    @Test
    void initializeRateLimiterWithPersistenceContext(){
        TokenBucketRateLimiter rateLimiter = new TokenBucketRateLimiter(MAX_BUCKET_SIZE, REFILL_RATE);
        rateLimiter.setTokenRepository(new MockTokenRepository());
        assertNotNull(rateLimiter);
        assertNotNull(rateLimiter.getTokenRepository());
        assertEquals(rateLimiter.getBucketSize(), MAX_BUCKET_SIZE);
        assertEquals(rateLimiter.getRefillRate(), REFILL_RATE);
    }

    @Test
    void shouldAcceptRequest(){
        TokenBucketRateLimiter rateLimiter = new TokenBucketRateLimiter(MAX_BUCKET_SIZE, REFILL_RATE);
        request = new RequestContext();
        request.setIpAddress(IP_ADDRESS);
        assertTrue(rateLimiter.allowRequest(request));
    }


    @Test
    void shouldNotAcceptRequest(){
        TokenBucketRateLimiter rateLimiter = new TokenBucketRateLimiter(1, REFILL_RATE);
        request = new RequestContext();
        request.setIpAddress(IP_ADDRESS);
        assertTrue(rateLimiter.allowRequest(request));
        assertFalse(rateLimiter.allowRequest(request));
    }

    static class MockTokenRepository implements TokenRepository {

        @Override
        public RateLimiterToken getToken(String ipAddress) {
            return new RateLimiterToken(1, System.nanoTime());
        }

        @Override
        public void save(String ipAddress, RateLimiterToken token) {
        }

        @Override
        public void getAndDecrement(String ipAddress) {

        }
    }
}
