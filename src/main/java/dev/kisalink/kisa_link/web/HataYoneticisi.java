package dev.kisalink.kisa_link.web;

import dev.kisalink.kisa_link.service.KodBulunamadiException;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.LinkedHashMap;
import java.util.Map;
import org.slf4j.Logger;

@RestControllerAdvice
public class HataYoneticisi {

    private static final Logger log = LoggerFactory.getLogger(HataYoneticisi.class);

    @ExceptionHandler(KodBulunamadiException.class)
    public ProblemDetail kodBulunamadi(KodBulunamadiException e) {
        ProblemDetail p = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, e.getMessage());
        p.setTitle("Kod bulunamadı");
        return p;
    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail dogrulama(MethodArgumentNotValidException e) {
        Map<String, String> hatalar = new LinkedHashMap<>();
        for (FieldError hata : e.getBindingResult().getFieldErrors()) {
            hatalar.put(hata.getField(), hata.getDefaultMessage());
        }
        ProblemDetail p = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST, "Gönderilen veri kurallara uymuyor");
        p.setTitle("Doğrulama hatası");
        p.setProperty("hatalar", hatalar);
        return p;
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ProblemDetail okunamayanGovde(HttpMessageNotReadableException e) {
        ProblemDetail p = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST, "İstek gövdesi okunamadı, geçerli bir JSON gönder");
        p.setTitle("Bozuk istek");
        return p;
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail beklenmeyen(Exception e) {
        log.error("Beklenmeyen hata", e);
        ProblemDetail p = ProblemDetail.forStatusAndDetail(
                HttpStatus.INTERNAL_SERVER_ERROR, "Beklenmeyen bir hata oluştu");
        p.setTitle("Sunucu hatası");
        return p;
    }
}