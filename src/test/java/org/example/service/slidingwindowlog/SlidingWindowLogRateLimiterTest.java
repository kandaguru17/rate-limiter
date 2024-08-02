package org.example.service.slidingwindowlog;

import org.example.models.RequestContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.LinkedList;
import java.util.concurrent.TimeUnit;

import static org.example.utils.TestUtils.IP_ADDRESS;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SlidingWindowLogRateLimiterTest {

    @Mock
    RequestContext context;

    @Mock
    SlidingWindowLogRepository repository;

    @Test
    void shouldAllowRequests() {
        long windowSize = TimeUnit.SECONDS.toMillis(1L);
        int requestsPerWindow = 1;
        SlidingWindowLogRateLimiter rateLimiter = new SlidingWindowLogRateLimiter(windowSize, requestsPerWindow, repository);
        when(context.getIpAddress()).thenReturn(IP_ADDRESS);

        LinkedList<Long> timeStamps = new LinkedList<>();
        when(repository.getRequestLog(IP_ADDRESS)).thenReturn(timeStamps);

        // should allow
        assertTrue(rateLimiter.allowRequest(context));
        verify(repository,times(1)).save(eq(IP_ADDRESS), any(Long.class));
        verify(repository,times(1)).getRequestLog(IP_ADDRESS);
    }


    @Test
    void shouldNotAllowRequests() {
        long windowSize = TimeUnit.SECONDS.toMillis(1L);
        int requestsPerWindow = 1;
        SlidingWindowLogRateLimiter rateLimiter = new SlidingWindowLogRateLimiter(windowSize, requestsPerWindow, repository);
        when(context.getIpAddress()).thenReturn(IP_ADDRESS);

        LinkedList<Long> timeStamps = new LinkedList<>();
        when(repository.getRequestLog(IP_ADDRESS)).thenReturn(timeStamps);

        // should allow
        assertTrue(rateLimiter.allowRequest(context));

        // should not allow cause window is full
        assertFalse(rateLimiter.allowRequest(context));
        verify(repository,times(1)).save(eq(IP_ADDRESS), any(Long.class));
        verify(repository,times(2)).getRequestLog(IP_ADDRESS);
    }

}