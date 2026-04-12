package org.example;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import liquibase.Contexts;
import liquibase.Liquibase;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.ClassLoaderResourceAccessor;
import org.example.client.PostgreSQLClient;
import org.example.exception.ApplicationException;
import org.example.property.ApplicationProperties;
import org.example.property.LiquibaseProperties;
import org.example.service.UserService;
import org.postgresql.ds.PGSimpleDataSource;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.utility.DockerImageName;

import static org.example.preset.FinancialTrackerInit.objectMapper;

public abstract class AbstractTest {
    protected static final ApplicationProperties applicationProperties = ApplicationProperties.getInstance();
    protected static final LiquibaseProperties liquibaseProperties = LiquibaseProperties.getInstance();

    protected static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>(
            DockerImageName.parse("postgres:12.20"));
    protected static PGSimpleDataSource datasource;
    protected final static UserService userService;

    static {
        postgreSQLContainer
                .withDatabaseName(applicationProperties.getProperty("datasource.database"))
                .withUsername(applicationProperties.getProperty("datasource.username"))
                .withPassword(applicationProperties.getProperty("datasource.password"))
                .withLogConsumer(new Slf4jLogConsumer(LoggerFactory.getLogger("🐳 " + "postgres")))
                .withEnv("TZ", "UTC")
                .withEnv("PGTZ", "UTC")
                .withUrlParam("connectionTimeZone", "UTC")
                .start();
        datasource = new PostgreSQLClient().getDataSource();
        datasource.setPortNumbers(new int[]{
                postgreSQLContainer
                        .getMappedPort(Integer.parseInt(applicationProperties.getProperty("datasource.port")))
        });
        userService = new UserService();

        try(var connection = datasource.getConnection()) {
            var database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(connection));
            database.setDefaultSchemaName(liquibaseProperties.getProperty("defaultSchemaName"));
            database.setLiquibaseSchemaName("public");
            var liquibase  = new Liquibase(liquibaseProperties.getProperty("changeLogFile"), new ClassLoaderResourceAccessor(), database);
            liquibase.setChangeLogParameter("schemaName", liquibaseProperties.getProperty("defaultSchemaName"));
            liquibase.dropAll();
            liquibase.update(new Contexts("test"));
            objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            objectMapper.registerModule(new JavaTimeModule());
        } catch (Exception e) {
            throw new ApplicationException(e.getMessage());
        }
    }
}
