package com.portfolio.shortener;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class UrlRepository {

    private final JdbcClient jdbc;

    public  UrlRepository(JdbcClient jdbc) {
        this.jdbc = jdbc;
    }

    public long save(String longUrl) {
        return jdbc.sql("insert into urls (long_url) values (?) returning id")
            .param(longUrl)
            .query(Long.class)
            .single();
    }

    public Optional<String> findLongUrlById(long id) {
        return jdbc.sql("select long_url from urls where id = ?")
            .param(id)
            .query(String.class)
            .optional();
    }
}
