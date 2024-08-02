package org.example.service.fixedwindow;

import org.example.models.RequestContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.concurrent.TimeUnit;

import static org.example.utils.TestUtils.IP_ADDRESS;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FixedWindowCounterTest {

    @Mock
    RequestContext context;

    @Mock
    InMemoryFixedWindowRepository repository;

    @Test
    void shouldAllowRequests() {
        long windowSize = TimeUnit.MINUTES.toMillis(1);
        int requestsPerWindow = 2;

        when(context.getIpAddress()).thenReturn(IP_ADDRESS);
        when(repository.getCounter(IP_ADDRESS)).thenReturn(null);

        FixedWindowCounter fixedWindowCounter = new FixedWindowCounter(windowSize, requestsPerWindow);
        fixedWindowCounter.setRepository(repository);
        LoggingRateLimiter loggingRateLimiter = new LoggingRateLimiter(fixedWindowCounter);

        assertTrue(loggingRateLimiter.allowRequest(context));
        verify(repository, times(1)).getCounter(IP_ADDRESS);
        verify(repository, times(1)).save(eq(IP_ADDRESS), argThat(counter -> counter.getCount() == 1));
    }

    @Test
    void shouldNotAllowRequests() {
        long windowSize = TimeUnit.MINUTES.toMillis(1);
        int requestsPerWindow = 1;
        long now = System.currentTimeMillis();
        Counter counter = new Counter(now, 0);

        when(context.getIpAddress()).thenReturn(IP_ADDRESS);
        when(repository.getCounter(IP_ADDRESS)).thenReturn(counter);

        FixedWindowCounter fixedWindowCounter = new FixedWindowCounter(windowSize, requestsPerWindow);
        fixedWindowCounter.setRepository(repository);
        LoggingRateLimiter loggingRateLimiter = new LoggingRateLimiter(fixedWindowCounter);

        assertTrue(loggingRateLimiter.allowRequest(context));
        assertFalse(loggingRateLimiter.allowRequest(context));
        verify(repository, times(2)).getCounter(IP_ADDRESS);
        verify(repository, times(1)).save(IP_ADDRESS, counter);
    }

    @Test
    void shouldReInitializeCounterWhenCurrentWindowExpires() {
        long windowSize = TimeUnit.MINUTES.toMillis(1);
        int requestsPerWindow = 5;
        long now = System.currentTimeMillis() - windowSize;
        Counter counter = new Counter(now, requestsPerWindow);

        when(context.getIpAddress()).thenReturn(IP_ADDRESS);
        when(repository.getCounter(IP_ADDRESS)).thenReturn(counter);

        FixedWindowCounter fixedWindowCounter = new FixedWindowCounter(windowSize, requestsPerWindow);
        fixedWindowCounter.setRepository(repository);
        LoggingRateLimiter loggingRateLimiter = new LoggingRateLimiter(fixedWindowCounter);


        assertTrue(loggingRateLimiter.allowRequest(context));
        assertEquals(1, counter.getCount());

        assertTrue(loggingRateLimiter.allowRequest(context));
        assertEquals(2, counter.getCount());

    }
}