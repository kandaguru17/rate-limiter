package org.example.service.tokenbucket;

import org.example.models.RequestContext;
import org.example.utils.TestUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.example.utils.TestUtils.IP_ADDRESS;
import static org.example.utils.TestUtils.MAX_BUCKET_SIZE;

@ExtendWith(MockitoExtension.class)
class TokenBucketRateLimiterTest {

    @Mock
    private RequestContext request;

    @Mock
    private InMemoryTokenRepository repository;

    @Test
    void shouldAllowWhenTokensIsAvailable() {
        RateLimiterToken token = new RateLimiterToken();
        token.setCount(MAX_BUCKET_SIZE);

        Mockito.when(request.getIpAddress()).thenReturn(IP_ADDRESS);
        Mockito.when(repository.getToken(ArgumentMatchers.eq(IP_ADDRESS))).thenReturn(token);

        TokenBucketRateLimiter rateLimiter = new TokenBucketRateLimiter(MAX_BUCKET_SIZE, TestUtils.REFILL_RATE);
        rateLimiter.setTokenRepository(repository);

        boolean result = rateLimiter.allowRequest(request);

        Assertions.assertTrue(result);
        Assertions.assertEquals(MAX_BUCKET_SIZE - 1, token.getCount());

        Mockito.verify(repository, Mockito.times(1)).getToken(IP_ADDRESS);
        Mockito.verify(repository, Mockito.times(1))
                .save(ArgumentMatchers.eq(IP_ADDRESS), ArgumentMatchers.argThat(rateLimiterToken -> rateLimiterToken.getCount() == MAX_BUCKET_SIZE - 1));
    }


    @Test
    void shouldNotAllowWhenTokensIsNotAvailable() {
        RateLimiterToken spy = Mockito.spy(RateLimiterToken.class);
        spy.setCount(1);

        long now = System.nanoTime();
        long fiftyMilliSecondLater = now + (50L * 1_000_000);

        Mockito.when(spy.getLastUpdatedNano())
                .thenReturn(now).thenReturn(fiftyMilliSecondLater);

        Mockito.when(request.getIpAddress()).thenReturn(IP_ADDRESS);
        Mockito.when(repository.getToken(ArgumentMatchers.eq(IP_ADDRESS))).thenReturn(spy);

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
        Mockito.when(request.getIpAddress()).thenReturn(IP_ADDRESS);
        Mockito.when(repository.getToken(ArgumentMatchers.eq(IP_ADDRESS))).thenReturn(spy);
        spy.setCount(1);

        long now = System.nanoTime();
        long oneSecondLater = now + 1_000_000_000L;

        Mockito.when(spy.getLastUpdatedNano())
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