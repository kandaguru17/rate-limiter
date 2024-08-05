package org.example.service.tokenbucket;

import org.example.models.RequestContext;
import org.example.utils.TestUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.example.utils.TestUtils.IP_ADDRESS;
import static org.example.utils.TestUtils.MAX_BUCKET_SIZE;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TokenBucketRateLimiterTest {

    @Mock
    private RequestContext request;

//    @Spy
    private InMemoryTokenRepository repository = new InMemoryTokenRepository();

    @Test
    void shouldAllowWhenTokensIsAvailable() {
        when(request.getIpAddress()).thenReturn(IP_ADDRESS);
        TokenBucketRateLimiter rateLimiter = new TokenBucketRateLimiter(MAX_BUCKET_SIZE, TestUtils.REFILL_RATE);
        rateLimiter.setTokenRepository(repository);
        boolean result = rateLimiter.allowRequest(request);
        Assertions.assertTrue(result);
    }


    @Test
    void shouldNotAllowWhenTokensIsNotAvailable() {
        RateLimiterToken spy = Mockito.spy(RateLimiterToken.class);

        long now = System.nanoTime();
        long fiftyMilliSecondLater = now + (50L * 1_000_000);

        when(spy.getLastUpdatedNano())
                .thenReturn(now).thenReturn(fiftyMilliSecondLater);
        when(spy.getCount()).thenReturn(1);

        when(request.getIpAddress()).thenReturn(IP_ADDRESS);
        when(repository.getToken(ArgumentMatchers.eq(IP_ADDRESS))).thenReturn(spy);

        TokenBucketRateLimiter rateLimiter = new TokenBucketRateLimiter(MAX_BUCKET_SIZE, TestUtils.REFILL_RATE);
        rateLimiter.setTokenRepository(repository);

        boolean result = rateLimiter.allowRequest(request);
        Assertions.assertTrue(result);

        result = rateLimiter.allowRequest(request);
        Assertions.assertFalse(result);

        Mockito.verify(repository, Mockito.times(2)).getToken(IP_ADDRESS);
        Mockito.verify(repository, Mockito.times(1)).save(ArgumentMatchers.eq(IP_ADDRESS), ArgumentMatchers.eq(spy));
    }


    @Test
    void shouldRefillBucketWhenTimeElapsed() throws InterruptedException {
        RateLimiterToken spy = Mockito.spy(RateLimiterToken.class);
        when(request.getIpAddress()).thenReturn(IP_ADDRESS);
        when(repository.getToken(ArgumentMatchers.eq(IP_ADDRESS))).thenReturn(spy);
        when(spy.getCount()).thenReturn(1);

        long now = System.nanoTime();
        long oneSecondLater = now + 1_000_000_000L;

        when(spy.getLastUpdatedNano())
                .thenReturn(now).thenReturn(oneSecondLater);

        TokenBucketRateLimiter rateLimiter = new TokenBucketRateLimiter(MAX_BUCKET_SIZE, TestUtils.REFILL_RATE);
        rateLimiter.setTokenRepository(repository);

        Thread.sleep(500L);

        boolean result = rateLimiter.allowRequest(request);
        Assertions.assertTrue(result);

        result = rateLimiter.allowRequest(request);
        Assertions.assertTrue(result);
    }

}