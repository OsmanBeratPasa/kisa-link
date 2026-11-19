# 05 — İşlem (transaction) deneyi

G2.1, 19 Kasım 2026, `deneme/islem` dalı. Kodu bu sefer Alfred yazdı (akşam, hızlı tur), ben
okudum. Kod: `deneme/IslemDenemesi.java`, test: `deneme/IslemDenemesiTest.java` (Testcontainers,
`postgres:18`).

## Kurulum

Her metot aynı şeyi yapıyor: `links`'e bir satır ekle → istisna fırlat → ikinci satırı ekle.
İkinci `INSERT` hiçbir zaman çalışmıyor. Soru şu: ilk satır tabloda kalıyor mu?

## Sonuçlar (kendi koşumum, 5/5 yeşil)

| Deney | Annotation | Fırlayan | Kalan satır | Neden |
| --- | --- | --- | --- | --- |
| 1. İşlemsiz | yok | `IllegalStateException` (unchecked) | **1** | İşlem yok, autocommit: ilk `INSERT` anında kalıcı oldu |
| 2. İşlemli | `@Transactional` | `IllegalStateException` (unchecked) | **0** | Proxy `RuntimeException`'ı yakaladı, `ROLLBACK` dedi |
| 3a. Checked | `@Transactional` | `Exception` (checked) | **1** | Varsayılan kural: checked istisnada `COMMIT`. Yarım iş kalıcı |
| 3b. Checked + rollbackFor | `@Transactional(rollbackFor = Exception.class)` | `Exception` (checked) | **0** | Kuralı değiştirdik: her istisnada geri al |

## Bilerek boz

2. deneyden `@Transactional`'ı sildim. Test kırmızı:

```
islemVarsaUncheckedIstisnadaHepsiGeriAlinir
expected: 0L
 but was: 1L
```

Annotation gidince 2. deney 1. deneye dönüştü. Geri koydum, yeşil.

## Proxy kanıtı

Teste enjekte edilen nesnenin sınıfını yazdırdım:

```
dev.kisalink.kisa_link.deneme.IslemDenemesi$$SpringCGLIB$$0
```

Yani elimdeki nesne benim `IslemDenemesi`'m değil, Spring'in CGLIB ile ürettiği **alt sınıf**
(vekil). `BEGIN`/`COMMIT`/`ROLLBACK` kararını o veriyor. 3a'nın nedeni de burada: vekilin
`catch` bloğu varsayılan olarak yalnız `RuntimeException` ve `Error` için geri alıyor.

## Dikkat ettiğimiz iki şey

- **Test sınıfında `@Transactional` yok.** Olsaydı Spring her testi kendi işleminde koşup sonunda
  geri alırdı; 1. deney de 0 satır gösterirdi ve yanlış sonuç çıkarırdım.
- **`@BeforeEach` ile tablo temizleniyor.** Yoksa önceki testin satırı sonrakinin sayımını bozar.

## Mülakat cümlesi

"`@Transactional` bir proxy ile çalışır; metot normal biterse commit, `RuntimeException` ya da
`Error` fırlarsa rollback. Checked istisnada varsayılan commit'tir, o yüzden
`rollbackFor = Exception.class` yazarım ya da iş hatalarımı unchecked yaparım."
