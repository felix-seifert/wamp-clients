package com.felixseifert.wampclients.autobahnjava;

import io.crossbar.autobahn.wamp.Client;
import io.crossbar.autobahn.wamp.Session;
import io.crossbar.autobahn.wamp.types.ExitInfo;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.logging.Logger;

/**
 * This class shows a template for an Autobahn|Java controller class.
 *
 * It does not call or register a procedure and it does not publish on or subscribe to a topic.
 * Nevertheless, it serves as a template you could use for this.
 *
 * For creating a new object of this class, you have to pass it an Executor of java.util.concurrent.
 * Eventually, you have to pass the url and the realm to the method start().
 */
public class EmptyControllerTemplate {

    private static final Logger LOGGER = Logger.getLogger(EmptyControllerTemplate.class.getName());

    private final Executor executor;

    private final Session session;

    public EmptyControllerTemplate(Executor executor) {
        this.executor = executor;
        this.session = new Session(executor);
    }

    public int start(String url, String realm) {

        LOGGER.info(String.format("EmptyControllerTemplate started with url=%s, realm=%s", url, realm));
        
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
}
