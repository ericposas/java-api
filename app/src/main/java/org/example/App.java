/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package org.example;

import org.example.controllers.StartController;
import org.example.controllers.UsersController;
import org.example.database.DatabaseConfig;
import org.example.database.SQLConfig;
import org.example.exec.ReadSQL;
import org.example.seeders.Seeder;

import io.undertow.Handlers;
import io.undertow.Undertow;
import io.undertow.server.HttpHandler;
import io.undertow.server.RoutingHandler;
import io.undertow.server.handlers.accesslog.AccessLogHandler;

public class App {

    private static final java.util.logging.Logger log = java.util.logging.Logger.getLogger(App.class.getName());

    public static void main(String[] args) {
        RoutingHandler routingHandler = Handlers.routing()
                .addAll(StartController.handlers())
                .addAll(UsersController.handlers());

        HttpHandler rootHandler = new AccessLogHandler(
                ExceptionHandlers.wrap(routingHandler),
                (message) -> log.info(message),
                "combined",
                App.class.getClassLoader());

        Undertow.builder()
                .addHttpListener(8080, "0.0.0.0")
                .setHandler(rootHandler)
                .build()
                .start();

        // Read SQL tables file
        ReadSQL.readFile(DatabaseConfig.getDbUsername(), DatabaseConfig.getDbPassword(), DatabaseConfig.getDbUrl(),
                SQLConfig.getTableGenFilePath());

        // IMPORTANT: Seeder.seedEntities has logic that needs custom if blocks to work
        // with various Entities!
        Seeder.seedEntities(100, "USERS", new String[] { "firstname", "lastname" });
        Seeder.seedEntities(100, "ADDRESSES",
                new String[] { "line1", "city", "postalcode", "stateprovince", "countryid" });
        Seeder.seedEntities(100, "EMAILS", new String[] { "email" });
        Seeder.seedEntities(100, "PHONENUMBERS", new String[] { "phonenumber", "phonetype" });

        // for random user_id's between 1 to 100,
        // randomly associate none, one, or more addresses by id
        // via the join table "USERSXXXXXXX"
        Seeder.attachUsersTo(100, "USERSADDRESSES", "address_id");
        Seeder.attachUsersTo(100, "USERSEMAILS", "email_id");
        Seeder.attachUsersTo(100, "USERSPHONENUMBERS", "phonenumber_id");
    }

}
