package com.xesnet.sshtaskmanager;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Logger;


public class ApplicationProperties {

    private static final Logger LOG = Logger.getLogger(ApplicationProperties.class.getName());

    public static final String PROPERTY_APPLICATION_VERSION = "application.version";
    public static final String PROPERTY_APPLICATION_VERSIONTIMESTAMP = "application.versionTimestamp";

    private static final String APPLICATION_FILE = "/application.properties";

    private final Properties properties;

    public ApplicationProperties() {
        properties = new Properties();
    }

    public void init() throws ApplicationPropertiesException {
        properties.clear();

        InputStream inputStream = ApplicationProperties.class.getResourceAsStream(APPLICATION_FILE);
        if (inputStream == null) {
            throw new ApplicationPropertiesException("Application properties file does not exists");
        }

        try {
            properties.load(inputStream);
        } catch (IOException e) {
            throw new ApplicationPropertiesException("Can not get properties", e);
        } finally {
            try {
                inputStream.close();
            } catch (IOException ex) {
                //Do nothing
            }
        }
    }

    public String getProperty(String key) {
        return properties.getProperty(key);
    }

    public static class ApplicationPropertiesException extends Exception {

        private static final long serialVersionUID = 1L;

        public ApplicationPropertiesException() {
            super();
        }

        public ApplicationPropertiesException(String message, Throwable cause) {
            super(message, cause);
        }

        public ApplicationPropertiesException(String message) {
            super(message);
        }

        public ApplicationPropertiesException(Throwable cause) {
            super(cause);
        }
    }
}
