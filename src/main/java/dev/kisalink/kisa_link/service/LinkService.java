package dev.kisalink.kisa_link.service;

import dev.kisalink.kisa_link.domain.Link;
import dev.kisalink.kisa_link.repository.LinkRepository;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Random;

@Service
public class LinkService {

    private static final String KARAKTERLER =
            "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    private final LinkRepository linkRepository;
    private final Random random = new Random();

    public LinkService(LinkRepository linkRepository) {
        this.linkRepository = linkRepository;
    }

    public String selamla() {
        return "servisten geldi";
    }

    public String kisalt(String targetUrl) {
        for (int deneme = 1; deneme <= 5; deneme++) {
            String kod = kodUret();
            try {
                linkRepository.kaydet(kod, targetUrl);
                return kod;
            } catch (DuplicateKeyException e) {

            }
        }
        throw new IllegalStateException("Benzersiz kod üretilemedi");
    }

    private String kodUret() {
        String kod = "";
        for (int i = 0; i < 7; i++) {
            kod = kod + KARAKTERLER.charAt(random.nextInt(KARAKTERLER.length()));
        }
        return kod;
    }

    public Optional<String> hedefBul(String kod) {
        Optional<Link> kutu = linkRepository.kodaGoreBul(kod);
        if (kutu.isEmpty()) {
            return Optional.empty();
        }
        linkRepository.tiklamaArtir(kod);
        return Optional.of(kutu.get().targetUrl());
    }
}