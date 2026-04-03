package org.example.property;

import org.example.exception.ApplicationException;

import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.logging.Logger;

public abstract class AbstractProperties extends Properties {
    protected Map<String, Object> properties;
    protected final Logger LOGGER = Logger.getLogger(AbstractProperties.class.getName());

    protected Object flatten(Object obj, String prefix) {
        return prefix.contains(".") ? flatten(
                ((Map<?, ?>) obj).get(prefix.substring(0, prefix.indexOf("."))),
                prefix.substring(prefix.indexOf(".") + 1)
        ) : ((Map<?, ?>) obj).get(prefix);
    }

    public String getProperty(String propertyName) {
        return Optional.ofNullable(properties)
                .map(value -> {
                    if (flatten(value, propertyName) instanceof String property) {
                        return property;
                    } else if (flatten(value, propertyName) instanceof Number property) {
                        return property.toString();
                    } else {
                        throw new ApplicationException("Property " + propertyName + " is not a string");
                    }
                }).orElse("");
    }
}
