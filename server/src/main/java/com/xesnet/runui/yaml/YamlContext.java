package com.xesnet.runui.yaml;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;


/**
 * @author Pierre PINON
 */
public class YamlContext {

    private final Path configPath;
    private final ObjectMapper objectMapper;

    public YamlContext(Path configPath) {
        this.configPath = configPath;

        objectMapper = new ObjectMapper(new YAMLFactory());
    }

    public synchronized <T> T read(String file, Class<T> clazz) throws YamlContextException {
        Path path = configPath.resolve(file);

        try {
            String data = Files.readString(path);
            
            return objectMapper.readValue(data, clazz);
        } catch (IOException e) {
            throw new YamlContextException(e);
        }
    }

    public synchronized <T> T read(String file, TypeReference<T> valueTypeRef) throws YamlContextException {
        Path path = configPath.resolve(file);

        try {
            String data = Files.readString(path);

            return objectMapper.readValue(data, valueTypeRef);
        } catch (Exception ex) {
            throw new YamlContextException(ex);
        }
    }

    public static class YamlContextException extends Exception {

        public YamlContextException() {
            super();
        }

        public YamlContextException(String message, Throwable cause) {
            super(message, cause);
        }

        public YamlContextException(String message) {
            super(message);
        }

        public YamlContextException(Throwable cause) {
            super(cause);
        }
    }
}
