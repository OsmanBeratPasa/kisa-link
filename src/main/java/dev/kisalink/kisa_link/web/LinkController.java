package dev.kisalink.kisa_link.web;

import dev.kisalink.kisa_link.service.LinkService;
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

    @PostMapping("/api/links")
    public ResponseEntity<Void> kisalt(@RequestBody KisaltIstegi istek) {
        String kod = linkService.kisalt(istek.targetUrl());
        URI adres = URI.create("http://localhost:8080/" + kod);
        return ResponseEntity.created(adres).build();
    }

    @GetMapping("/{kod}")
    public ResponseEntity<Void> yonlendir(@PathVariable String kod) {
        Optional<String> hedef = linkService.hedefBul(kod);
        if (hedef.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(hedef.get()))
                .build();
    }
}