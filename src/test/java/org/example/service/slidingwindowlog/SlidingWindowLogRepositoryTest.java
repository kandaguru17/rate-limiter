package org.example.service.slidingwindowlog;

import org.junit.jupiter.api.Test;

import java.util.Deque;

import static org.example.utils.TestUtils.IP_ADDRESS;
import static org.junit.jupiter.api.Assertions.assertEquals;

class SlidingWindowLogRepositoryTest {

    @Test
    void saveAndRetrieveRequestLog(){
        Long now = System.currentTimeMillis();
        SlidingWindowLogRepository slidingWindowLogRepository = new SlidingWindowLogRepository();
        slidingWindowLogRepository.save(IP_ADDRESS, now);

        Deque<Long> requestLog = slidingWindowLogRepository.getRequestLog(IP_ADDRESS);
        assertEquals(1, requestLog.size());
        assertEquals(now,requestLog.getFirst());
    }

}