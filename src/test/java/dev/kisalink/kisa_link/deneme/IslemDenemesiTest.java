package dev.kisalink.kisa_link.deneme;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

// Dikkat: sınıfta @Transactional YOK. Olsaydı her test kendi işleminde koşup
// sonunda geri alınırdı ve "işlemsiz" deneyin anlamı kalmazdı.
@SpringBootTest
@Testcontainers
class IslemDenemesiTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:18");

    @Autowired
    IslemDenemesi deneme;   // enjekte edilen nesne gerçek sınıf değil, proxy

    @Autowired
    JdbcClient jdbc;

    @BeforeEach
    void temizle() {
        jdbc.sql("DELETE FROM links").update();
    }

    private long satirSayisi() {
        return jdbc.sql("SELECT count(*) FROM links").query(Long.class).single();
    }

    @Test
    void islemYoksaIlkSatirKalir() {
        assertThatThrownBy(() -> deneme.islemsiz())
                .isInstanceOf(IllegalStateException.class);

        assertThat(satirSayisi()).isEqualTo(1);
    }

    @Test
    void islemVarsaUncheckedIstisnadaHepsiGeriAlinir() {
        assertThatThrownBy(() -> deneme.islemli())
                .isInstanceOf(IllegalStateException.class);

        assertThat(satirSayisi()).isZero();
    }

    @Test
    void checkedIstisnadaVarsayilanKuralCommitEder() {
        assertThatThrownBy(() -> deneme.checkedIstisna())
                .isExactlyInstanceOf(Exception.class);

        assertThat(satirSayisi()).isEqualTo(1);
    }

    @Test
    void rollbackForIleCheckedIstisnadaDaGeriAlinir() {
        assertThatThrownBy(() -> deneme.checkedRollbackFor())
                .isExactlyInstanceOf(Exception.class);

        assertThat(satirSayisi()).isZero();
    }

    @Test
    void enjekteEdilenNesneProxyDir() {
        System.out.println(">>> Enjekte edilen sınıf: " + deneme.getClass().getName());

        assertThat(AopUtils.isAopProxy(deneme)).isTrue();
        assertThat(AopUtils.isCglibProxy(deneme)).isTrue();
    }
}
