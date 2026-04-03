package org.example.property;

import org.yaml.snakeyaml.Yaml;

public class LiquibaseProperties extends AbstractProperties {
    private static LiquibaseProperties INSTANCE;

    private LiquibaseProperties() {
        try {
            properties = new Yaml().load(getClass().getResourceAsStream("/liquibase.yml"));
        } catch (NullPointerException e) {
            LOGGER.info("Could not load liquibase.yml");
        }
    }

    public static LiquibaseProperties getInstance() {
        if(INSTANCE == null) {
            INSTANCE = new LiquibaseProperties();
        }
        return INSTANCE;
    }
}
