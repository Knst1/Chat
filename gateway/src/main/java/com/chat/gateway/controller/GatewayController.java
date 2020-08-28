package com.chat.gateway.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/1.0")
public class GatewayController {
    @GetMapping
    public String list() {
        return "";
    }
}
