package com.chat.user.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@FeignClient("authorization-service")
public interface AuthorizationClient {
    @GetMapping("token/validate")
    ResponseEntity<Boolean> validate(@RequestParam("username") String username, @RequestParam("token") String token);
}
