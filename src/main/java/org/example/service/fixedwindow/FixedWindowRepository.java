package org.example.service.fixedwindow;

public interface FixedWindowRepository {
    Counter getCounter(String ipAddress);

    void save(String ipAddress, Counter counter);
}
