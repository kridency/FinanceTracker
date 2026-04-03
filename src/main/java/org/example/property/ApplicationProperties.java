package org.example.property;

import org.yaml.snakeyaml.Yaml;

public class ApplicationProperties extends AbstractProperties {
    private static ApplicationProperties INSTANCE;

    private ApplicationProperties() {
        try {
            properties = new Yaml().load(getClass().getResourceAsStream("/application.yml"));
        } catch (NullPointerException e) {
            LOGGER.info("Could not load application.yml");
        }
    }

    public static ApplicationProperties getInstance() {
        if(INSTANCE == null) {
            INSTANCE = new ApplicationProperties();
        }
        return INSTANCE;
    }
}
