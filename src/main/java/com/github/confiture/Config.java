package com.github.confiture;

import com.googlecode.totallylazy.Strings;
import com.googlecode.totallylazy.collections.PersistentMap;
import com.googlecode.totallylazy.functions.Function1;
import com.jayway.jsonpath.DocumentContext;

import java.io.File;
import java.io.InputStream;
import java.io.Reader;
import java.util.List;
import java.util.Map;

import static com.googlecode.totallylazy.Sequences.sequence;
import static com.googlecode.totallylazy.Unchecked.cast;
import static com.googlecode.totallylazy.collections.PersistentMap.constructors.map;
import static com.googlecode.totallylazy.json.Json.map;
import static com.jayway.jsonpath.JsonPath.parse;

public class Config {
    private DocumentContext documentContext;

    private Config(PersistentMap<String, Object> map) {
        documentContext = parse(map);
    }

    public static Config config(Map<String, Object> map) {
        return new Config(map(map));
    }

    public Config asConfig(String key) {
        return config(mapValue(key));
    }

    public Map<String, Object> mapValue(String key) {
        return cast(value(key, Map.class));
    }

    public String stringValue(String key) {
        return value(key, String.class);
    }

    public Integer integerValue(String key) {
        return value(key, Integer.class);
    }

    public Double doubleValue(String key) {
        return value(key, Double.class);
    }

    public Boolean booleanValue(String key) {
        return value(key, Boolean.class);
    }

    public List<Config> asConfigs(String key) {
        return sequence(mapValues(key))
                .map(Config::config)
                .toList();
    }

    public List<Map<String, Object>> mapValues(String key) {
        return cast(values(key, Map.class));
    }

    public List<String> stringValues(String key) {
        return values(key, String.class);
    }

    public List<Integer> integerValues(String key) {
        return values(key, Integer.class);
    }

    public List<Double> doubleValues(String key) {
        return values(key, Double.class);
    }

    public List<Boolean> booleanValues(String key) {
        return values(key, Boolean.class);
    }

    private <T> T value(String key, Class<T> valueClass) throws IllegalArgumentException {
        try {
            return valueClass.cast(documentContext.read(key, valueClass));
        } catch (Exception e) {
            throw new IllegalArgumentException("Couldn't get value", e);
        }
    }

    private <T> List<T> values(String key, Class<T> valueClass) throws IllegalArgumentException {
        try {
            return sequence(documentContext.read(key, List.class))
                    .map(convert(valueClass))
                    .toList();
        } catch (Exception e) {
            throw new IllegalArgumentException("Couldn't get values", e);
        }
    }

    private <T> Function1<Object, T> convert(Class<T> valueClass) {
        return object -> {
            if (valueClass.isAssignableFrom(Integer.class)) return cast(Integer.valueOf(object.toString()));
            if (valueClass.isAssignableFrom(Double.class)) return cast(Double.valueOf(object.toString()));
            if (valueClass.isAssignableFrom(Boolean.class)) return cast(Boolean.valueOf(object.toString()));
            if (valueClass.isAssignableFrom(Map.class)) return cast(valueClass.cast(object));
            if (valueClass.isAssignableFrom(String.class)) return cast(object.toString());
            throw new RuntimeException("Unknown mapping for type " + valueClass.getName());
        };
    }

    public static final class constructors {
        public static Config load(String json) {
            return config(map(json));
        }

        public static Config load(File file) {
            return load(Strings.string(file));
        }

        public static Config load(InputStream inputStream) {
            return load(Strings.string(inputStream));
        }

        public static Config load(byte[] bytes) {
            return load(Strings.string(bytes));
        }

        public static Config load(Reader reader) {
            return load(Strings.string(reader));
        }
    }
}
