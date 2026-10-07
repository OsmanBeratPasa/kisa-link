package dev.kisalink.kisa_link.repository;

import dev.kisalink.kisa_link.domain.Link;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class LinkRepository {

    private final JdbcClient jdbc;

    public LinkRepository(JdbcClient jdbc) {
        this.jdbc = jdbc;
    }

    public void kaydet(String code,String targetUrl){
        jdbc.sql("INSERT INTO links (code, target_url) VALUES (:code, :url)")
                .param("code", code)
                .param("url", targetUrl)
                .update();

    }

    public Optional<Link> kodaGoreBul(String kod) {
        return jdbc.sql("SELECT id, code, target_url, click_count, created_at FROM links WHERE code = :code")
                .param("code", kod)
                .query(Link.class)
                .optional();
    }
}