package com.xesnet.runui.yaml;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

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

    public <T> T read(String file, Class<T> clazz) {
        Path path = configPath.resolve(file);

        try {
            String data = Files.readString(path);

            return objectMapper.readValue(data, clazz);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public <T> T read(String file, TypeReference<T> valueTypeRef) {
        Path path = configPath.resolve(file);

        try {
            String data = Files.readString(path);

            return objectMapper.readValue(data, valueTypeRef);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}
