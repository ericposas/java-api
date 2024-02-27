package org.example;

import static io.undertow.server.handlers.ExceptionHandler.THROWABLE;
import static io.undertow.util.StatusCodes.INTERNAL_SERVER_ERROR;

import org.example.controllers.StartController;
import org.example.controllers.UsersController;

import io.undertow.Handlers;
import io.undertow.server.HttpHandler;
import io.undertow.server.handlers.ExceptionHandler;

public final class ExceptionHandlers {

    public static ExceptionHandler wrap(HttpHandler httpHandler) {
        ExceptionHandler exceptionHandler = Handlers.exceptionHandler(httpHandler);

        StartController.exceptionHandlers(exceptionHandler);
        UsersController.exceptionHandlers(exceptionHandler);

        return exceptionHandler
                .addExceptionHandler(Throwable.class, (exchange) -> {
                    Throwable exception = exchange.getAttachment(THROWABLE);
                    exception.printStackTrace();
                    exchange.setStatusCode(INTERNAL_SERVER_ERROR);
                    exchange.setReasonPhrase("Oooops...");
                });
    }

    private ExceptionHandlers() {
    }
}
