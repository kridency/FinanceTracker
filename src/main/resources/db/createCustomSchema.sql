-- Создание схемы custom
CREATE SCHEMA IF NOT EXISTS "${database.defaultSchemaName}" AUTHORIZATION CURRENT_USER;
CREATE SCHEMA IF NOT EXISTS "${database.liquibaseSchemaName}" AUTHORIZATION CURRENT_USER;
