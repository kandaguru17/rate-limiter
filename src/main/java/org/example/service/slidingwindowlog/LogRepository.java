package org.example.service.slidingwindowlog;

import java.util.Deque;
import java.util.List;

public interface LogRepository {
    Deque<Long> getRequestLog(String ipAddress);

    void save(String ipAddress, Long timeStamp);
}
