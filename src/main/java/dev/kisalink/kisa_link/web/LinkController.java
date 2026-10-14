package dev.kisalink.kisa_link.web;

import dev.kisalink.kisa_link.domain.Link;
import dev.kisalink.kisa_link.service.LinkService;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.http.ProblemDetail;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Optional;

@RestController
public class LinkController {

    private final LinkService linkService;

    public LinkController(LinkService linkService) {
        this.linkService = linkService;
    }

    @ApiResponse(responseCode = "201", description = "Kısa link oluşturuldu, adresi Location başlığında",
            headers = @Header(name = "Location", description = "Oluşan kısa linkin adresi"))
    @ApiResponse(responseCode = "400", description = "targetUrl boş ya da http(s):// ile başlamıyor")
    @PostMapping("/api/links")
    public ResponseEntity<Void> kisalt( @Valid @RequestBody KisaltIstegi istek) {
        String kod = linkService.kisalt(istek.targetUrl());
        URI adres = URI.create("http://localhost:8080/" + kod);
        return ResponseEntity.created(adres).build();
    }

    @ApiResponse(responseCode = "200", description = "Kod bulundu; sürüm 1 sadece adresi, sürüm 2 tıklanma ve tarihi de verir",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(oneOf = {LinkBilgisiV1.class, LinkBilgisiV2.class})))
    @ApiResponse(responseCode = "404", description = "Bu kısa kod kayıtlı değil",
            content = @Content(mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetail.class)))
    @GetMapping(path = "/api/links/{kod}", version = "1")
    public LinkBilgisiV1 bilgi(@PathVariable String kod) {
        Link link = linkService.linkBul(kod);
        return new LinkBilgisiV1(link.targetUrl());
    }

    @ApiResponse(responseCode = "200", description = "Kod bulundu; sürüm 1 sadece adresi, sürüm 2 tıklanma ve tarihi de verir",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(oneOf = {LinkBilgisiV1.class, LinkBilgisiV2.class})))
    @ApiResponse(responseCode = "404", description = "Bu kısa kod kayıtlı değil",
            content = @Content(mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetail.class)))
    @GetMapping(path = "/api/links/{kod}", version = "2")
    public LinkBilgisiV2 bilgiV2(@PathVariable String kod) {
        Link link = linkService.linkBul(kod);
        return new LinkBilgisiV2(link.targetUrl(), link.clickCount(), link.createdAt());
    }

    @ApiResponse(responseCode = "302", description = "Kod bulundu, kullanıcı uzun adrese yönlendiriliyor",
            headers = @Header(name = "Location", description = "Gidilecek uzun adres"))
    @ApiResponse(responseCode = "404", description = "Bu kısa kod kayıtlı değil")
    @GetMapping("/{kod}")
    public ResponseEntity<Void> yonlendir(@PathVariable String kod) {
        String hedef = linkService.hedefBul(kod);
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(hedef))
                .build();
    }


}