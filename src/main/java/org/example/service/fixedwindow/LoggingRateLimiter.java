package org.example.service.fixedwindow;

import org.example.RateLimiter;
import org.example.models.RequestContext;

import java.util.logging.Logger;

public class LoggingRateLimiter extends FixedWindowDecorator{


    private static final Logger log = Logger.getLogger(LoggingRateLimiter.class.getName());

    public LoggingRateLimiter(RateLimiter rateLimiter) {
        super(rateLimiter);
    }

    @Override
    public boolean allowRequest(RequestContext request) {
        log.info("Request coming from " + request.getIpAddress());
        return super.allowRequest(request);
    }
}
