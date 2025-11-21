package com.carlos.todoapi.config;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;


@Configuration
public class DataSourceConfiguration {
    private final DataSource dataSource;

    @Autowired
    public DataSourceConfiguration(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @PostConstruct
    public void init() throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            System.out.println("Conexão concluida com sucesso");
        } catch (SQLException e) {
            System.err.println("Conexão falhou " + e.getMessage());
        }

        System.out.println("Testando o Banco de Dados");
        System.out.println("Inicializado");

    }
}