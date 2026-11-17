package com.portfolio.shortener;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
class UrlRepositoryTest {

    @Autowired
    UrlRepository repository;

    @Test
    void savesAndFindsUrl() {
        long id = repository.save("https://example.com");
        assertThat(repository.findLongUrlById(id)).contains("https://example.com");
    }

    @Test
    void returnsEmptyForUnknownId() {
        assertThat(repository.findLongUrlById(999_999)).isEmpty();
    }
}
