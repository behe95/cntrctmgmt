package com.example.cntrctmgmt.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class DatabaseConfig {
    @Bean
    public DataSource dataSource(
            @Value("${spring.application.db.driverClassname}") String driverClassName
            , @Value("${spring.application.db.dataSourceURL}") String url
    ) {
        DataSourceBuilder<?> datasource = DataSourceBuilder.create();
        datasource.driverClassName(driverClassName);
        datasource.url(url);

        return datasource.build();
    }
}
