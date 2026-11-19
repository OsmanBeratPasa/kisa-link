package dev.kisalink.kisa_link.deneme;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * G2.1 deneyi (yalnız deneme/islem dalında, main'e girmez).
 * Her metot aynı şeyi yapar: bir satır ekle, patla, ikinci satırı ekle.
 * Fark yalnız annotation'da ve fırlayan istisnanın türünde.
 */
@Service
public class IslemDenemesi {

    private final JdbcClient jdbc;

    public IslemDenemesi(JdbcClient jdbc) {
        this.jdbc = jdbc;
    }

    // Deney 1: işlem yok → her INSERT autocommit ile anında kalıcı
    public void islemsiz() {
        ekle("islemsiz-1");
        patla();
        ekle("islemsiz-2");
    }

    // Deney 2: işlem var, unchecked istisna → ROLLBACK
    @Transactional
    public void islemli() {
        ekle("islemli-1");
        patla();
        ekle("islemli-2");
    }

    // Deney 3a: işlem var ama istisna checked → varsayılan kural COMMIT
    @Transactional
    public void checkedIstisna() throws Exception {
        ekle("checked-1");
        patlaChecked();
        ekle("checked-2");
    }

    // Deney 3b: aynısı, ama "checked olsa da geri al" dedik
    @Transactional(rollbackFor = Exception.class)
    public void checkedRollbackFor() throws Exception {
        ekle("rollbackfor-1");
        patlaChecked();
        ekle("rollbackfor-2");
    }

    private void ekle(String kod) {
        jdbc.sql("INSERT INTO links (code, target_url) VALUES (:code, :url)")
                .param("code", kod)
                .param("url", "https://ornek.com")
                .update();
    }

    // Ayrı metot: throw'dan sonraki satır derleyiciye "ulaşılamaz" görünmesin diye
    private void patla() {
        throw new IllegalStateException("ikinci INSERT'ten önce patladı (unchecked)");
    }

    private void patlaChecked() throws Exception {
        throw new Exception("ikinci INSERT'ten önce patladı (checked)");
    }
}
