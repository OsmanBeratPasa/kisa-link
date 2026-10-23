package dev.kisalink.kisa_link.service;



import dev.kisalink.kisa_link.domain.Link;
import dev.kisalink.kisa_link.repository.LinkRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DuplicateKeyException;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class LinkServiceTest {
    @Mock
    LinkRepository depo;

    @InjectMocks
    LinkService servis;

    @Test
    void testdeneme(){
        String  sonuc = servis.kisalt("https://google.com");
        assertThat(sonuc).hasSize(7);

        verify(depo).kaydet(sonuc,"https://google.com");


    }

    // ---- AI tarafından eklendi (2026-10-23) ----

    @Test
    @DisplayName("hedefBul: kod varsa uzun adres döner ve tıklama artırılır")
    void hedefBulKodVarsaAdresDonerVeTiklamaArtar() {
        Link link = new Link(1L, "abc1234", "https://google.com", 0, Instant.now());
        when(depo.kodaGoreBul("abc1234")).thenReturn(Optional.of(link));

        String sonuc = servis.hedefBul("abc1234");

        assertThat(sonuc).isEqualTo("https://google.com");
        verify(depo).tiklamaArtir("abc1234");
    }

    @Test
    @DisplayName("hedefBul: kod yoksa KodBulunamadiException fırlar, tıklama artırılmaz")
    void hedefBulKodYoksaHataFirlarVeTiklamaArtmaz() {
        when(depo.kodaGoreBul("yokboyle")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> servis.hedefBul("yokboyle"))
                .isInstanceOf(KodBulunamadiException.class)
                .hasMessageContaining("yokboyle");
        verify(depo, never()).tiklamaArtir(anyString());
    }

    @Test
    @DisplayName("linkBul: kod varsa Link döner")
    void linkBulKodVarsaLinkDoner() {
        Link link = new Link(1L, "abc1234", "https://google.com", 3, Instant.now());
        when(depo.kodaGoreBul("abc1234")).thenReturn(Optional.of(link));

        Link sonuc = servis.linkBul("abc1234");

        assertThat(sonuc).isEqualTo(link);
    }

    @Test
    @DisplayName("linkBul: kod yoksa KodBulunamadiException fırlar")
    void linkBulKodYoksaHataFirlar() {
        when(depo.kodaGoreBul("yokboyle")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> servis.linkBul("yokboyle"))
                .isInstanceOf(KodBulunamadiException.class);
    }

    @Test
    @DisplayName("kisalt: 1. denemede çakışma, 2. denemede başarı → ikinci kod döner, kaydet 2 kez çağrılır")
    void kisaltIlkDenemeCakisirsaIkinciKodDoner() {
        doThrow(new DuplicateKeyException("kod zaten var"))
                .doNothing()
                .when(depo).kaydet(anyString(), eq("https://google.com"));

        String sonuc = servis.kisalt("https://google.com");

        ArgumentCaptor<String> denenenKodlar = ArgumentCaptor.forClass(String.class);
        verify(depo, times(2)).kaydet(denenenKodlar.capture(), eq("https://google.com"));
        assertThat(sonuc).isEqualTo(denenenKodlar.getAllValues().get(1));
    }

    @Test
    @DisplayName("kisalt: 5 denemede de çakışma → IllegalStateException fırlar")
    void kisaltBesDenemedeCakisirsaHataFirlar() {
        doThrow(new DuplicateKeyException("kod zaten var"))
                .when(depo).kaydet(anyString(), anyString());

        assertThatThrownBy(() -> servis.kisalt("https://google.com"))
                .isInstanceOf(IllegalStateException.class);
    }

}
