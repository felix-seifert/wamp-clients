package com.felixseifert.wampclients.autobahnjava;

import io.crossbar.autobahn.wamp.Client;
import io.crossbar.autobahn.wamp.Session;
import io.crossbar.autobahn.wamp.types.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.logging.Logger;

public class Controller {

    private static final Logger LOGGER = Logger.getLogger(Controller.class.getName());

    private static final String PROCEDURE = "com.felixseifert.wampclients.autobahnjava.procedure";

    private static final String TOPIC = "com.felixseifert.wampclients.autobahnjava.publication";

    private final Executor executor;

    private final Session session;

    public Controller(Executor executor) {
        this.executor = executor;
        this.session = new Session(executor);

        session.addOnJoinListener(this::registerExample);
        session.addOnJoinListener(this::callExample);
        session.addOnJoinListener(this::subscribeExample);
        session.addOnJoinListener(this::publishExample);
    }

    public int start(String url, String realm) {

        LOGGER.info(String.format("Controller started with url=%s, realm=%s", url, realm));

        Client client = new Client(session, url, realm, executor);
        CompletableFuture<ExitInfo> exitFuture = client.connect();
        try {
            ExitInfo exitInfo = exitFuture.get();
            return exitInfo.code;
        } catch (Exception e) {
            LOGGER.warning(e.getMessage());
            return 1;
        }
    }

    public void registerExample(Session session, SessionDetails details) {
        // Register procedure expecting two integers as argument and returning sum of them
        CompletableFuture<Registration> completableFuture = session.register(PROCEDURE, this::registerHandler);
        completableFuture.thenAccept(registration ->
                LOGGER.info("Procedure registered: " + registration.procedure));
    }

    private int registerHandler(List<Object> args) {
        return (int) args.get(0) + (int) args.get(1);
    }

    public void callExample(Session session, SessionDetails details) {

        int arg1 = 1, arg2 = 2;

        // Call procedure with the arguments arg1 and arg2
        CompletableFuture<CallResult> completableFuture =
                session.call(PROCEDURE, arg1, arg2);
        completableFuture.thenAccept(callResult -> LOGGER.info("Result of call: " + callResult.results));
    }

    public void subscribeExample(Session session, SessionDetails details) {
        //Subscribe to topic
        CompletableFuture<Subscription> completableFuture = session.subscribe(TOPIC, this::subscribeHandler);
        completableFuture.thenAccept(subscription -> LOGGER.info("Subscribed to topic " + subscription.topic));
    }

    private void subscribeHandler(List<Object> args) {
        LOGGER.info("Received via subscription: " + args.get(0));
    }

    public void publishExample(Session session, SessionDetails details) {

        String args = "Hello Subscriber";
        PublishOptions publishOptions = new PublishOptions(true, false);

        // Publish on topic with args and publishOptions
        CompletableFuture<Publication> completableFuture = session.publish(TOPIC, publishOptions, args);
        completableFuture.thenAccept(publication -> LOGGER.info("Published on " + TOPIC));
    }
}