package org.example.client;

import org.example.property.ApplicationProperties;
import org.postgresql.ds.PGConnectionPoolDataSource;


public class PostgreSQLClient {
    private static PostgreSQLClient INSTANCE;

    private static PGConnectionPoolDataSource datasource;

    private PostgreSQLClient() {
        ApplicationProperties applicationProperties = ApplicationProperties.getInstance();
        var url = System.getenv("POSTGRES_DATASOURCE_URL");
        datasource = new PGConnectionPoolDataSource();
        datasource.setURL(url == null ? applicationProperties.getProperty("datasource.url") : url);
        datasource.setPortNumbers(new int[]{Integer.parseInt(applicationProperties.getProperty("datasource.port"))});
        datasource.setDatabaseName(applicationProperties.getProperty("datasource.database"));
        datasource.setStringType("unspecified");
        datasource.setUser(applicationProperties.getProperty("datasource.username"));
        datasource.setPassword(applicationProperties.getProperty("datasource.password"));
        datasource.setOptions(applicationProperties.getProperty("datasource.options"));
        datasource.setCurrentSchema(applicationProperties.getProperty("datasource.currentSchema"));
    }

    public static PostgreSQLClient getInstance() {
        if(INSTANCE == null) {
            INSTANCE = new PostgreSQLClient();
        }
        return INSTANCE;
    }

    public PGConnectionPoolDataSource getDataSource() {
        return datasource;
    }
}
