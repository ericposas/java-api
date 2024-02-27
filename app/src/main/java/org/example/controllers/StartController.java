package org.example.controllers;

import org.example.exceptions.NotFoundException;

import io.undertow.Handlers;
import io.undertow.predicate.Predicate;
import io.undertow.server.HttpHandler;
import io.undertow.server.RoutingHandler;
import io.undertow.server.handlers.ExceptionHandler;
import io.undertow.util.Headers;

public class StartController {

    public static RoutingHandler handlers() {
        Predicate noQueryParameters = (exchange) -> exchange.getQueryParameters().isEmpty();
        Predicate hasNameParam = (exchange) -> exchange.getQueryParameters().containsKey("name");
        StartController controller = new StartController();
        return Handlers.routing()
                .get("/", noQueryParameters, controller.startEndpoint())
                .get("/", hasNameParam, controller.startEndpointWithParam());
    }

    private HttpHandler startEndpoint() {
        return (exchange) -> {
            exchange.getRequestHeaders().put(Headers.CONTENT_TYPE, "text/plain");
            exchange.getResponseSender().send("Hello World");
        };
    }

    private HttpHandler startEndpointWithParam() {
        return (exchange) -> {
            var name = exchange.getQueryParameters().get("name").getFirst();
            exchange.getRequestHeaders().put(Headers.CONTENT_TYPE, "text/plain");
            exchange.getResponseSender().send("Hello " + name);
        };
    }

    public static void exceptionHandlers(ExceptionHandler exceptionHandler) {
        exceptionHandler
                .addExceptionHandler(NotFoundException.class, NotFoundException::handle);
    }

    private StartController() {
    }

}
