package dev.kisalink.kisa_link.web;

import dev.kisalink.kisa_link.service.KodBulunamadiException;
import dev.kisalink.kisa_link.service.LinkService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@WebMvcTest(LinkController.class)
public class LinkControllerTest {

    @MockitoBean
    LinkService linkService;

    @Autowired
    MockMvc mockMvc;

    @Test
    void olmayanKodIcin404() throws Exception {

        when(linkService.hedefBul("yok")).thenThrow(new KodBulunamadiException("yok"));

        mockMvc.perform(get("/yok"))
                .andExpect(status().isNotFound());





    }

    @Test
    void gecerliAdresIcin201VeLocation() throws Exception {
        when(linkService.kisalt("https://ornek.com")).thenReturn("aB3xK9q");

        mockMvc.perform(post("/api/links")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"targetUrl\":\"https://ornek.com\"}"))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost:8080/aB3xK9q"));
    }

    @Test
    void bosAdresIcin400VeServisCagrilmaz() throws Exception {
        mockMvc.perform(post("/api/links")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"targetUrl\":\"\"}"))
                .andExpect(status().isBadRequest());

        verify(linkService, never()).kisalt(anyString());
    }

    @Test
    void httpIleBaslamayanAdresIcin400() throws Exception {
        mockMvc.perform(post("/api/links")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"targetUrl\":\"ftp://ornek.com\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void bozukJsonIcin400() throws Exception {
        mockMvc.perform(post("/api/links")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"targetUrl\": "))
                .andExpect(status().isBadRequest());
    }

    @Test
    void varOlanKodIcin302VeLocation() throws Exception {
        when(linkService.hedefBul("abc")).thenReturn("https://ornek.com/uzun/adres");

        mockMvc.perform(get("/abc"))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", "https://ornek.com/uzun/adres"));
    }
}
