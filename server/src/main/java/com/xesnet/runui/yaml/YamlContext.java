package com.xesnet.runui.yaml;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
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
        return read(file, clazz, false);
    }

    public synchronized <T> T read(String file, Class<T> clazz, boolean createIfNotExists) throws YamlContextException {
        Path path = configPath.resolve(file);

        if (!Files.exists(path)) {
            try {
                T object = clazz.getDeclaredConstructor().newInstance();

                if (createIfNotExists) {
                    write(file, object);
                }

                return object;
            } catch (InstantiationException | InvocationTargetException | NoSuchMethodException | IllegalAccessException e) {
                throw new YamlContextException(e);
            }
        }

        try {
            String data = Files.readString(path);

            return objectMapper.readValue(data, clazz);
        } catch (Exception ex) {
            throw new YamlContextException(ex);
        }
    }

    public synchronized <T> void write(String file, T object) throws YamlContextException {
        Path path = configPath.resolve(file);

        try {
            String data = objectMapper.writeValueAsString(object);

            Files.writeString(path, data);
        } catch (IOException e) {
            throw new YamlContextException(e);
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
