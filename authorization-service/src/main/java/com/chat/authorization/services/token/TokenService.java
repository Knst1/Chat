package com.chat.authorization.services.token;

import org.springframework.http.ResponseEntity;

public interface TokenService {
    ResponseEntity<String> login(String appId, String appSecret);
    ResponseEntity<Boolean> validate(String appId, String token);
}