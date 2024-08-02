package org.example.service.fixedwindow;

import org.example.RateLimiter;
import org.example.models.RequestContext;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class FixedWindowCounter implements RateLimiter {

    private final long windowSize;

    private final int requestsPerWindow;

    private FixedWindowRepository repository;

    private final Map<String, Lock> locks = new ConcurrentHashMap<>();

    public FixedWindowCounter(long windowSize, int requestsPerWindow) {
        this.windowSize = windowSize;
        this.repository = new InMemoryFixedWindowRepository();
        this.requestsPerWindow = requestsPerWindow;
    }

    @Override
    public boolean allowRequest(RequestContext request) {
        Lock lock = locks.computeIfAbsent(request.getIpAddress(), (k) -> new ReentrantLock());
        try{
            lock.lock();
            Counter counter = repository.getCounter(request.getIpAddress());
            long now = System.currentTimeMillis();
            if (counter == null) {
                counter = new Counter(now, 0);
            }
            // if window is expired
            if (now - counter.getWindowInterval() > windowSize) {
                counter.setCount(0);
                counter.setWindowInterval(now);
            } else if (counter.getCount() >= requestsPerWindow) {
                return false;
            }
            counter.setCount(counter.getCount() + 1);
            repository.save(request.getIpAddress(), counter);
            return true;
        }finally {
            lock.unlock();
        }
    }

    public long getWindowSize() {
        return windowSize;
    }

    public void setRepository(FixedWindowRepository fixedWindowRepository) {
        this.repository = fixedWindowRepository;
    }

    public FixedWindowRepository getRepository() {
        return repository;
    }

    public int getRequestsPerWindow() {
        return requestsPerWindow;
    }
}
