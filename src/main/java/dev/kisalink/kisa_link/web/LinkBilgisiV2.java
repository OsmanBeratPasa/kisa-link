package dev.kisalink.kisa_link.web;

import java.time.Instant;

public record LinkBilgisiV2(String targetUrl, long clickCount, Instant createdAt) {
}