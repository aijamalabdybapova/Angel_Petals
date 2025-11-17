package com.flowershop.config;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@Component
public class DatabaseInitializer {

    private final DataSource dataSource;

    @Value("${spring.jpa.hibernate.ddl-auto:create-drop}")
    private String ddlAuto;

    public DatabaseInitializer(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @PostConstruct
    public void initialize() {
        if ("create-drop".equals(ddlAuto) || "create".equals(ddlAuto)) {
            try (Connection connection = dataSource.getConnection()) {
                // Выполняем только schema_updates.sql
                ScriptUtils.executeSqlScript(connection, new ClassPathResource("db/schema_updates.sql"));

                // ВРЕМЕННО ЗАКОММЕНТИРОВАНО - вызывает ошибки с dollar quotes
                // ScriptUtils.executeSqlScript(connection, new ClassPathResource("db/triggers.sql"));
                // ScriptUtils.executeSqlScript(connection, new ClassPathResource("db/stored_procedures.sql"));

                System.out.println("Database initialization completed successfully");
            } catch (SQLException e) {
                System.err.println("Database initialization failed: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
}