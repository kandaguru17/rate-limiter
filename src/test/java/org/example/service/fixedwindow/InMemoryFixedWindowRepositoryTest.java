package org.example.service.fixedwindow;

import org.junit.jupiter.api.Test;

import static org.example.utils.TestUtils.IP_ADDRESS;
import static org.junit.jupiter.api.Assertions.*;

class InMemoryFixedWindowRepositoryTest {

    @Test
    void shouldSaveAndRetrieveCounter(){
        InMemoryFixedWindowRepository repository = new InMemoryFixedWindowRepository();
        long now = System.currentTimeMillis();
        repository.save(IP_ADDRESS,new Counter(now,0));

        Counter counter = repository.getCounter(IP_ADDRESS);
        assertNotNull(counter);
        assertEquals(counter.getCount(),0);
        assertEquals(counter.getWindowInterval(),now);

    }

}