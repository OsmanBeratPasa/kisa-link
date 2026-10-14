# kisa-link

Uzun bir adresi kısa bir koda çeviren URL kısaltıcı. Java 25 ve Spring Boot 4 ile yazıldı;
veriler PostgreSQL'de, şema Flyway ile kuruluyor.

## Çalıştırma

Gerekenler: JDK 25 ve çalışan bir PostgreSQL (`localhost:5432`, veritabanı ve kullanıcı
`kisalink`, şifre `kisalink`). Tablolar ilk açılışta Flyway tarafından oluşturulur.

```bash
./mvnw spring-boot:run
```

Uygulama `http://localhost:8080` adresinde açılır. API belgesi:

- Swagger UI: `http://localhost:8080/swagger-ui.html`
- Ham OpenAPI belgesi: `http://localhost:8080/v3/api-docs`

## Uç noktalar

| Yöntem | Adres | Ne yapar | Durum kodları |
| --- | --- | --- | --- |
| POST | `/api/links` | `{"targetUrl": "https://..."}` alır, kısa kod üretir; adres `Location` başlığında | 201, 400 |
| GET | `/{kod}` | Kısa koda tıklayanı uzun adrese yönlendirir, tıklanma sayısını artırır | 302, 404 |
| GET | `/api/links/{kod}` | Kodun bilgisini verir; sürüm `X-API-Version` başlığıyla seçilir (varsayılan 1) | 200, 404 |

Bilgi ucunun sürümleri: `1` yalnız `targetUrl`, `2` ek olarak `clickCount` ve `createdAt` döner.
Hatalar Problem Details biçiminde (`application/problem+json`) döner.

```bash
curl -i -X POST localhost:8080/api/links -H "Content-Type: application/json" -d '{"targetUrl":"https://example.com"}'
curl -i localhost:8080/api/links/KOD -H "X-API-Version: 2"
```

## Notlar

Geliştirirken verdiğim kararlar `notlar/` klasöründe:

- [01 - Bağımlılık enjeksiyonu](notlar/01-di.md)
- [02 - HTTP ve 301/302 seçimi](notlar/02-http.md)
- [03 - Flyway](notlar/03-flyway.md)
- [04 - API sürümleme](notlar/04-surumleme.md)
