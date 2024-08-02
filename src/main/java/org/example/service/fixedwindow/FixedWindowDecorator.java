package org.example.service.fixedwindow;

import org.example.RateLimiter;
import org.example.models.RequestContext;

public class FixedWindowDecorator implements RateLimiter {
    private final RateLimiter rateLimiter;

    public FixedWindowDecorator(RateLimiter rateLimiter) {
        this.rateLimiter = rateLimiter;
    }

    @Override
    public boolean allowRequest(RequestContext request) {
        return rateLimiter.allowRequest(request);
    }
}
