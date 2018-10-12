package com.felixseifert.wampclients.autobahnjavasimplified;

import io.crossbar.autobahn.wamp.Client;
import io.crossbar.autobahn.wamp.Session;
import io.crossbar.autobahn.wamp.types.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

public class AutobahnJavaSimplified {

    private static final Logger LOGGER = Logger.getLogger(AutobahnJavaSimplified.class.getName());

    private static final String PROCEDURE = "com.felixseifert.wampclients.autobahnjavasimplified.procedure";

    private static final String TOPIC = "com.felixseifert.wampclients.autobahnjavasimplified.publication";

    private static final String URL = "ws://127.0.0.1:8080/ws";

    private static final String REALM = "realm1";

    public static void main(String[] args) {

        AutobahnJavaSimplified object = new AutobahnJavaSimplified();

        Executor executor = Executors.newSingleThreadExecutor();
        Session session = new Session(executor);

        session.addOnJoinListener(object::registerExample);
        session.addOnJoinListener(object::callExample);
        session.addOnJoinListener(object::subscribeExample);
        session.addOnJoinListener(object::publishExample);

        Client client = new Client(session, URL, REALM, executor);
        try {
            client.connect().get();
        } catch (Exception e) {
            e.printStackTrace();
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
