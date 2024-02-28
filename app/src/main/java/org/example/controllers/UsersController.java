package org.example.controllers;

import static io.undertow.util.Headers.CONTENT_TYPE;
import static java.nio.charset.StandardCharsets.UTF_8;

import org.example.exceptions.NotFoundException;
import org.example.services.UsersService;

import io.undertow.Handlers;
import io.undertow.io.Receiver.FullStringCallback;
import io.undertow.predicate.Predicate;
import io.undertow.server.HttpHandler;
import io.undertow.server.RoutingHandler;
import io.undertow.server.handlers.ExceptionHandler;

public class UsersController {

    private final UsersService service;

    public static RoutingHandler handlers() {

        Predicate containsId = (exchange) -> exchange.getQueryParameters().containsKey("id");
        Predicate noQueryParameters = (exchange) -> exchange.getQueryParameters().isEmpty();

        UsersController controller = new UsersController();
        return Handlers.routing()
                .get("/users", noQueryParameters, controller.getUsers())
                .get("/users/{id}", containsId, controller.getUser())
                .post("/users", controller.createUser())
                .delete("/users/{id}", containsId, controller.deleteUser());
    }

    public static void exceptionHandlers(ExceptionHandler exceptionHandler) {
        exceptionHandler
                .addExceptionHandler(NotFoundException.class, NotFoundException::handle);
    }

    private UsersController() {
        service = UsersService.getInstance();
    }

    // BlockingHandlers
    private HttpHandler getUsers() {
        return (exchange) -> {
            String users = service.getUsers();
            exchange.getResponseHeaders().put(CONTENT_TYPE, "application/json");
            exchange.getResponseSender().send(users, UTF_8);
        };
    }

    private HttpHandler getUser() {
        return exchange -> {
            String uid = exchange.getQueryParameters()
                    .get("id")
                    .getFirst();
            String user = service.getUser(Integer.parseInt(uid));
            exchange.getResponseHeaders().put(CONTENT_TYPE, "application/json");
            exchange.getResponseSender().send(user, UTF_8);
        };
    }

    private HttpHandler createUser() {
        FullStringCallback callback = (exchange, payload) -> {
            boolean created = service.createUser(payload);
            exchange.setStatusCode(io.undertow.util.StatusCodes.CREATED);
            exchange.getResponseHeaders().put(CONTENT_TYPE, "application/json");
            exchange.getResponseSender().send(created ? "Created new user" : "Could not create new user", UTF_8);
        };
        return (exchange) -> exchange.getRequestReceiver().receiveFullString(callback, UTF_8);
    }

    private HttpHandler deleteUser() {
        return exchange -> {
            String uid = exchange.getQueryParameters()
                    .get("id")
                    .getFirst();
            boolean deleted = service.deleteUser(Integer.parseInt(uid));
            exchange.setStatusCode(io.undertow.util.StatusCodes.NO_CONTENT);
            exchange.getResponseSender().send(deleted ? "Deleted user " + uid : "Could not delete user " + uid);
        };
    }

}
