package org.acme.service;

import java.util.concurrent.atomic.AtomicLong;

import org.acme.error.BadRequestException;
import org.eclipse.microprofile.faulttolerance.CircuitBreaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.inject.Singleton;

@Singleton
public class GreetingService {
    private Logger logger = LoggerFactory.getLogger(getClass());
    private AtomicLong counter = new AtomicLong(0);

    @CircuitBreaker(
        requestVolumeThreshold = 4,
        delay = 1000,
        skipOn = { BadRequestException.class }
    )
    public String maySayHello(String to) {
        mayNotGreet();
        return "Hello " + to;
    }

    private void mayNotGreet() {
        final long invocationCount = counter.incrementAndGet();
        if(invocationCount % 3 == 0) {
            throw new BadRequestException();
        }
        if (invocationCount % 4 > 1) {
            logger.info("This method will not greet the #{} invocation", invocationCount);
            throw new RuntimeException("... I will not greet you!");
        }
    }
}
