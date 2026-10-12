package dev.kisalink.kisa_link.service;

public class KodBulunamadiException extends RuntimeException {

    public KodBulunamadiException(String kod) {
        super("Kod bulunamadı: " + kod);
    }
}