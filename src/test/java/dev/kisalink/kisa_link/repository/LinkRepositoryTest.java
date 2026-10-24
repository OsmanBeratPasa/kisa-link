package dev.kisalink.kisa_link.repository;

import dev.kisalink.kisa_link.domain.Link;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Testcontainers
class LinkRepositoryTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:18");

    @Autowired
    LinkRepository depo;

    @Test
    void kaydedilenLinkKodlaBulunur(){
        depo.kaydet("abc1234", "https://ornek.com");

        Optional<Link> sonuc = depo.kodaGoreBul("abc1234");

        assertThat(sonuc).isPresent();
        assertThat(sonuc.get().targetUrl()).isEqualTo("https://ornek.com");
    }

    @Test
    void olmayanKodIcinBosOptionalDoner() {
        Optional<Link> sonuc = depo.kodaGoreBul("yok12345");

        assertThat(sonuc).isEmpty();
    }

    @Test
    void tiklamaArtirIkiKezCagrilincaSayac2Olur() {
        depo.kaydet("tik00001", "https://ornek.com");

        depo.tiklamaArtir("tik00001");
        depo.tiklamaArtir("tik00001");

        assertThat(depo.kodaGoreBul("tik00001").get().clickCount()).isEqualTo(2);
    }

    @Test
    void ayniKodIkinciKezKaydedilinceDuplicateKeyExceptionFirlar() {
        depo.kaydet("dup00001", "https://ornek.com");

        assertThatThrownBy(() -> depo.kaydet("dup00001", "https://baska.com"))
                .isInstanceOf(DuplicateKeyException.class);
    }

    @Test
    void yeniLinkinSayaci0VeTarihiDoluGelir() {
        depo.kaydet("yeni0001", "https://ornek.com");

        Link link = depo.kodaGoreBul("yeni0001").get();

        assertThat(link.clickCount()).isEqualTo(0);
        assertThat(link.createdAt()).isNotNull();
    }

}