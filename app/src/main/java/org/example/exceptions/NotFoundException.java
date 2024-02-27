package org.example.exceptions;

import static io.undertow.util.StatusCodes.NOT_FOUND;

import io.undertow.server.HttpServerExchange;

public class NotFoundException extends RuntimeException {

    public static void handle(HttpServerExchange exchange) {
        exchange.setStatusCode(NOT_FOUND);
        exchange.setReasonPhrase("Resource not found");
    }
}