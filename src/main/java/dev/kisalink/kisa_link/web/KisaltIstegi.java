package dev.kisalink.kisa_link.web;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record KisaltIstegi(
        @NotBlank(message = "targetUrl boş olamaz")
        @Pattern(regexp = "^https?://.+", message = "targetUrl http:// ya da https:// ile başlamalı")
        String targetUrl
) {
}