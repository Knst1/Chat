package com.chat.authorization.controller;

import com.chat.authorization.services.token.TokenService;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import static org.slf4j.LoggerFactory.getLogger;

@RestController
@RequestMapping("token")
public class TokenController {
    private static final Logger logger = getLogger(TokenController.class);
    private final TokenService tokenService;

    @Autowired
    public TokenController(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @GetMapping()
    public ResponseEntity<String> getToken(@RequestParam("username") final String appId, @RequestParam("password") final String password) {
        logger.info("GET запрос на получение токена для доступа к сервису с AppId {}", appId);
        return tokenService.login(appId, password);
    }

    @GetMapping("validate")
    public ResponseEntity<Boolean> validate(@RequestParam("username") final String appId, @RequestParam("token") final String token) {
        logger.info("GET запрос для валидации токена от сервиса с АppId {}", appId);
        return tokenService.validate(appId, token);
    }
}
