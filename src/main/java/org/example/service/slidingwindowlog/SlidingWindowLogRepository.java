package org.example.service.slidingwindowlog;

import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class SlidingWindowLogRepository implements LogRepository {

    Map<String, Deque<Long>> logStore = new HashMap<>();

    @Override
    public Deque<Long> getRequestLog(String ipAddress) {
        Deque<Long> timeStamps = logStore.get(ipAddress);
        return timeStamps == null ? new LinkedList<>() : timeStamps;
    }

    @Override
    public void save(String ipAddress, Long timeStamp) {
        logStore.putIfAbsent(ipAddress, new LinkedList<>());
        logStore.get(ipAddress).add(timeStamp);
    }


}
