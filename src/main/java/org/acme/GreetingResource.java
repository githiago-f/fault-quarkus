package org.acme;

import java.util.concurrent.atomic.AtomicLong;

import org.acme.error.BadRequestException;
import org.acme.service.GreetingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/hello")
public class GreetingResource {
    private Logger logger = LoggerFactory.getLogger(getClass());
    private AtomicLong requestCounter = new AtomicLong(0);
    @Inject private GreetingService greetingService;

    @GET
    @Path("/{to}")
    @Produces(MediaType.TEXT_PLAIN)
    public Response hello(@PathParam("to") String to) {
        long invocationNumber = requestCounter.incrementAndGet();
        try {
            String message = greetingService.maySayHello(to);
            logger.info("GreetingResource#hello(String to) invocation #{} returning successfully", invocationNumber);
            return Response.ok(message).build();
        } catch(BadRequestException e) {
            logger.info("Just a bad request exception for {}", to);
            return Response.status(Response.Status.BAD_REQUEST)
                .entity(e.getMessage())
                .type(MediaType.TEXT_PLAIN_TYPE)
                .build();
        } catch (RuntimeException e) {
            String message = e.getClass().getSimpleName() + ": " + e.getMessage();
            logger.error("CoffeeResource#availability() invocation #{} failed: {}", invocationNumber, message);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(message)
                    .type(MediaType.TEXT_PLAIN_TYPE)
                    .build();
        }
    }
}
