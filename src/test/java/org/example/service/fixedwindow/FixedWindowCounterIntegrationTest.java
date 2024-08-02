package org.example.service.fixedwindow;

import org.example.models.RequestContext;
import org.junit.jupiter.api.Test;

import static org.example.utils.TestUtils.IP_ADDRESS;
import static org.junit.jupiter.api.Assertions.*;


class FixedWindowCounterIntegrationTest {


    @Test
    void initializeFixedWindowRateLimiter(){
        long windowSize = 300000L;
        int requestsPerWindow = 1000;
        FixedWindowRepository repository = new InMemoryFixedWindowRepository();
        FixedWindowCounter fixedWindowCounter = new FixedWindowCounter(windowSize,requestsPerWindow);
        fixedWindowCounter.setRepository(repository);
        assertNotNull(fixedWindowCounter);
        assertInstanceOf(InMemoryFixedWindowRepository.class,fixedWindowCounter.getRepository());
        assertEquals(windowSize, fixedWindowCounter.getWindowSize());
        assertEquals(requestsPerWindow, fixedWindowCounter.getRequestsPerWindow());
    }

    @Test
    void shouldAllowRequest(){
        long windowSize = 300000L;
        int requestsPerWindow = 1000;
        RequestContext context = new RequestContext();
        context.setIpAddress(IP_ADDRESS);
        FixedWindowCounter fixedWindowCounter = new FixedWindowCounter(windowSize,requestsPerWindow);
        Boolean result = assertDoesNotThrow(() -> fixedWindowCounter.allowRequest(context));
        assertTrue(result);
    }


    @Test
    void shouldNotAllowRequest(){
        long windowSize = 300000L;
        int requestsPerWindow = 1;
        RequestContext context = new RequestContext();
        context.setIpAddress(IP_ADDRESS);
        FixedWindowCounter fixedWindowCounter = new FixedWindowCounter(windowSize,requestsPerWindow);

        Boolean result = assertDoesNotThrow(() -> fixedWindowCounter.allowRequest(context));
        assertTrue(result);

        result = assertDoesNotThrow(() -> fixedWindowCounter.allowRequest(context));
        assertFalse(result);
    }
}