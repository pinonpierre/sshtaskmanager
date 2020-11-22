package com.xesnet.sshtaskmanager.yaml;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.MessageFormat;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * @author Pierre PINON
 */
public class YamlContext {
    private final static Logger LOG = Logger.getLogger(YamlContext.class.getName());

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
        } catch (Exception e) {
            LOG.log(Level.SEVERE, MessageFormat.format("[YAML] Failed to Read file \"{0}\"", file), e);
            throw new YamlContextException(e);
        }
    }

    public synchronized <T> void write(String file, T object) throws YamlContextException {
        Path path = configPath.resolve(file);

        try {
            String data = objectMapper.writeValueAsString(object);

            Files.writeString(path, data);
        } catch (IOException e) {
            LOG.log(Level.SEVERE, MessageFormat.format("[YAML] Failed to Write file \"{0}\"", file), e);
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
