package com.felixseifert.wampclients.autobahnjava;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

public class Main {

    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());

    private static final Properties APPLICATION_PROPERTIES = loadProperties("application.properties");

    public static void main(String[] args) {

        Executor executor = Executors.newSingleThreadExecutor();

        // Address of the crossbar router retrieved from application.properties
        String url = APPLICATION_PROPERTIES.getProperty("connection.url");
        String realm = APPLICATION_PROPERTIES.getProperty("connection.realm");

        Controller controller = new Controller(executor);

        LOGGER.info("Controller started");
        int returnCode = controller.start(url, realm);

        LOGGER.info(String.format(".. ended with return code %s", returnCode));
        System.exit(returnCode);
    }

    private static Properties loadProperties(String fileName) {

        String filePath = "out/production/resources/" + fileName;

        Properties properties = new Properties();
        try {
            properties.load(new FileInputStream(filePath));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return properties;
    }
}
