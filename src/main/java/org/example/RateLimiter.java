package org.example;

import org.example.models.RequestContext;

public interface RateLimiter {
     boolean allowRequest(RequestContext request);
}
