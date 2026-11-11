package com.portfolio.shortener;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.simple.JdbcClient;

/**
 * Smoke test for the scaffold: the app boots against real Postgres, Flyway ran,
 * and the app's connections use the service's own schema.
 */
@SpringBootTest
@Import(TestcontainersConfiguration.class)
class UrlShortenerApplicationTests {

    @Autowired
    JdbcClient jdbc;

    @Test
    void bootsAgainstPostgresAndUsesServiceSchema() {
        String schema = jdbc.sql("select current_schema()").query(String.class).single();
        assertThat(schema).isEqualTo("shortener");
    }
}
