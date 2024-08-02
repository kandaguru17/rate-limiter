package org.example.service.slidingwindowlog;

import org.example.RateLimiter;
import org.example.models.RequestContext;

import java.util.Deque;

public class SlidingWindowLogRateLimiter implements RateLimiter {

    private final long windowSize;

    private final int requestsPerWindow;

    private final LogRepository repository;

    public SlidingWindowLogRateLimiter(long windowSize, int requestPerWindow, LogRepository repository) {
        this.windowSize = windowSize;
        this.requestsPerWindow = requestPerWindow;
        this.repository = repository;
    }

    @Override
    public boolean allowRequest(RequestContext request) {
        String ipAddress = request.getIpAddress();
        long curTimeStamp = System.currentTimeMillis();
        Deque<Long> timeStamps = repository.getRequestLog(ipAddress);
        while(!timeStamps.isEmpty() && curTimeStamp - timeStamps.peekFirst() > windowSize){
            timeStamps.removeFirst();
        }
        if(timeStamps.size() >= requestsPerWindow ) return false;
        timeStamps.addLast(curTimeStamp);
        repository.save(ipAddress,curTimeStamp);
        return true;
    }

    LogRepository getRepository() {
        return repository;
    }

    long getWindowSize() {
        return windowSize;
    }

    int getRequestsPerWindow() {
        return requestsPerWindow;
    }
}
