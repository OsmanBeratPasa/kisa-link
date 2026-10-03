package dev.kisalink.kisa_link.domain;

import java.time.Instant;

public record Link(Long id, String code, String targetUrl, long clickCount, Instant createdAt) {}