package org.example.service.fixedwindow;

import java.util.concurrent.*;

public class InMemoryFixedWindowRepository implements FixedWindowRepository {

    ConcurrentHashMap<String, Counter> counterStore = new ConcurrentHashMap<>();

    ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();

    public InMemoryFixedWindowRepository() {
        scheduleCleanUp(1_000L, 10_000L);
    }

    private void scheduleCleanUp(long initialDelay, long fixedInterval) {
        scheduledExecutorService.scheduleAtFixedRate(this::cleanup, initialDelay, fixedInterval, TimeUnit.MILLISECONDS);
    }

    private void cleanup() {
        this.counterStore.entrySet().removeIf(entry ->
                System.currentTimeMillis() - entry.getValue().getWindowInterval() > TimeUnit.MINUTES.toMillis(5L));
    }

    @Override
    public Counter getCounter(String ipAddress) {
        return counterStore.get(ipAddress);
    }

    @Override
    public void save(String ipAddress, Counter counter) {
        counterStore.put(ipAddress, counter);
    }
}
