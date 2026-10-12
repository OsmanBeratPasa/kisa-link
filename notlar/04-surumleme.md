# 04 — API sürümleme kararı

G1.8, 12 Ekim 2026. Kararlar benim seçimlerim; cümleleri seçimlerimden Alfred yazdı.

## Hangi yolu uyguladık

Sürüm numarasını istekteki bir **başlığa** koyduk: `X-API-Version: 1`. Adres her iki sürümde de
aynı kalıyor (`/api/links/{kod}`), Spring başlıktaki numaraya bakıp doğru metodu seçiyor.

- **Artısı:** adres temiz kalıyor, sürüm değişince adres değişmiyor.
- **Eksisi:** tarayıcının adres çubuğundan elle denenemiyor; `curl` gibi başlık gönderebilen bir
  araç gerekiyor.

Diğer üç yol (adresin içinde `/v1/...`, soru işaretinden sonra `?version=1`, medya tipinde)
uygulanmadı.

## Sürüm gönderilmezse

Başlık yoksa istek **sürüm 1** sayılıyor (`spring.mvc.apiversion.default=1`).

Neden 2 değil 1: sürüm göndermeyenler, sürümleme eklenmeden önce yazılmış eski kullanıcılar.
Varsayılan 2 olsaydı onların cevabı kendiliğinden değişirdi.

Bu satırı eklemeden önce başlıksız her istek hata dönüyordu; `/hello` ve yönlendirme de
bozulmuştu.

## Sürüm 1'i kapatmak istersem

Önceden duyururum ve bir kapanış tarihi veririm. O tarihe kadar iki sürüm birlikte çalışır.
Haber vermeden kapatmam.
