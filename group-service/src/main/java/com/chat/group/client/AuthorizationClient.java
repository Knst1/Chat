package com.chat.group.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient("authorization-service")
public interface AuthorizationClient {
    @GetMapping("token")
    ResponseEntity<String> getToken(@RequestParam("username") String username, @RequestParam("password") String password);
    @GetMapping("token/validate")
    ResponseEntity<Boolean> validate(@RequestParam("username") String username, @RequestParam("token") String token);
}
