package org.example.service.tokenbucket;

import org.junit.jupiter.api.Test;

import static org.example.utils.TestUtils.IP_ADDRESS;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class InMemoryTokenRepositoryTest {

    @Test
    void shouldSaveAndRetrieveToken(){
        RateLimiterToken rateLimiterToken = new RateLimiterToken();
        rateLimiterToken.setCount(2);
        InMemoryTokenRepository repository = new InMemoryTokenRepository();

        repository.save(IP_ADDRESS,rateLimiterToken);

        RateLimiterToken token = repository.getToken(IP_ADDRESS);
        assertNotNull(token);
    }

}