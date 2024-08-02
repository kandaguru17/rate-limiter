package org.example.service.slidingwindowlog;

import org.example.models.RequestContext;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

import static org.example.utils.TestUtils.IP_ADDRESS;
import static org.junit.jupiter.api.Assertions.*;

public class SlidingWindowRateLimiterIntgTest {

    @Test
    void shouldInitializeRateLimiter() {
        long windowSize = TimeUnit.SECONDS.toMillis(1L);
        int requestPerWindow = 2;
        LogRepository repo = new SlidingWindowLogRepository();
        SlidingWindowLogRateLimiter rateLimiter = new SlidingWindowLogRateLimiter(windowSize, requestPerWindow, repo);
        assertNotNull(rateLimiter);
        assertNotNull(rateLimiter.getRepository());
        assertEquals(windowSize, rateLimiter.getWindowSize());
        assertEquals(requestPerWindow, rateLimiter.getRequestsPerWindow());
    }


    @Test
    void shouldAllowRequests(){
        long windowSize = TimeUnit.SECONDS.toMillis(1L);
        int requestPerWindow = 1;
        LogRepository repo = new SlidingWindowLogRepository();
        SlidingWindowLogRateLimiter rateLimiter = new SlidingWindowLogRateLimiter(windowSize, requestPerWindow, repo);

        RequestContext requestContext = new RequestContext();
        requestContext.setIpAddress(IP_ADDRESS);
        Boolean result = assertDoesNotThrow(() -> rateLimiter.allowRequest(requestContext));
        assertTrue(result);
    }


    @Test
    void shouldNotAllowRequests(){
        long windowSize = TimeUnit.SECONDS.toMillis(1L);
        int requestPerWindow = 1;
        LogRepository repo = new SlidingWindowLogRepository();
        SlidingWindowLogRateLimiter rateLimiter = new SlidingWindowLogRateLimiter(windowSize, requestPerWindow, repo);

        RequestContext requestContext = new RequestContext();
        requestContext.setIpAddress(IP_ADDRESS);
        Boolean result = assertDoesNotThrow(() -> rateLimiter.allowRequest(requestContext));
        assertTrue(result);

        result = assertDoesNotThrow(() -> rateLimiter.allowRequest(requestContext));
        assertFalse(result);
    }
}
