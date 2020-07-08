package com.makovetskiy.jsvm.configuration;


import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import liquibase.integration.spring.SpringLiquibase;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.io.IOException;


@Configuration
@Slf4j
public class LiquibasePlugin {

    private static final String DEFAULT_CHANGELOG = "classpath:db/changelog/jpa-plugin-changelog-master.xml";

    @Bean
    public DataSource dataSource(Environment env) {
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setDriverClassName(env.getProperty("spring.datasource.driver-class-name"));
        hikariConfig.setJdbcUrl(env.getProperty("spring.datasource.url"));
        hikariConfig.setUsername(env.getProperty("spring.datasource.username"));
        hikariConfig.setPassword(env.getProperty("spring.datasource.password"));
        return new HikariDataSource(hikariConfig);
    }

    @Bean
    public SpringLiquibase marketplaceLiquibase(DataSource dataSource) throws IOException {
        SpringLiquibase liquibase = new SpringLiquibase();
        liquibase.setDataSource(dataSource);
        liquibase.setChangeLog(DEFAULT_CHANGELOG);
        liquibase.setShouldRun(true);
        boolean dropFirst = false;
        if (dropFirst) {
            log.info("Attention: database schema will be dropped by liquibase as requested in configuration");
        }
        liquibase.setDropFirst(dropFirst);
        return liquibase;
    }

    @Bean
    public JdbcTemplate jdbcTemplate(DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }
}
