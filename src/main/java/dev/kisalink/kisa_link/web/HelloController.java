package dev.kisalink.kisa_link.web;

import dev.kisalink.kisa_link.service.LinkService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    private final LinkService linkService;

    public HelloController(LinkService linkService) {
        this.linkService = linkService;
    }

    @GetMapping("/hello")
    public String hello() {
        return linkService.selamla();
    }
}
