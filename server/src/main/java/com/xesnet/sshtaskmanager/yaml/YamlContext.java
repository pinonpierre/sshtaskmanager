package com.xesnet.sshtaskmanager.yaml;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * @author Pierre PINON
 */
public class YamlContext {

    private final static Logger LOG = Logger.getLogger(YamlContext.class.getName());

    private final Path configPath;
    private final ObjectMapper objectMapper;

    private Map<String, Object> fileToObjectCache;

    public YamlContext(Path configPath) {
        this.configPath = configPath;

        objectMapper = new ObjectMapper(new YAMLFactory());
        fileToObjectCache = new HashMap<>();
    }

    public synchronized <T> T read(String file, Class<T> clazz) throws YamlContextException {
        return read(file, clazz, false);
    }

    public synchronized <T> T read(String file, Class<T> clazz, boolean createIfNotExists) throws YamlContextException {
        T object = (T) fileToObjectCache.get(file);
        if (object != null) {
            return object;
        }

        Path path = configPath.resolve(file);

        if (!Files.exists(path)) {
            try {
                T newObject = clazz.getDeclaredConstructor().newInstance();

                if (createIfNotExists) {
                    write(file, newObject);
                }

                return newObject;
            } catch (InstantiationException | InvocationTargetException | NoSuchMethodException | IllegalAccessException e) {
                throw new YamlContextException(e);
            }
        }

        try {
            String data = Files.readString(path);

            object = objectMapper.readValue(data, clazz);
            fileToObjectCache.put(file, object);
            return object;
        } catch (Exception e) {
            LOG.log(Level.SEVERE, MessageFormat.format("[YAML] Failed to Read file \"{0}\"", file), e);
            throw new YamlContextException(e);
        }
    }

    public synchronized <T> void write(String file, T object) throws YamlContextException {
        Path path = configPath.resolve(file);

        try {
            String data = objectMapper.writeValueAsString(object);

            fileToObjectCache.put(file, object);

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
