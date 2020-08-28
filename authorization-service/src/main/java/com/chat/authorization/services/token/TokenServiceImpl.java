package com.chat.authorization.services.token;

import com.chat.authorization.domain.Token;
import com.chat.authorization.repository.TokenRepository;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.slf4j.LoggerFactory.getLogger;

@Service
public class TokenServiceImpl implements TokenService {
    private final TokenRepository tokenRepository;
    private static final Logger logger = getLogger(TokenServiceImpl.class);

    @Autowired
    public TokenServiceImpl(TokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
        Thread timeoutThread = new Thread(() -> {
            while (true) {
                try {
                    List<Token> tokens = tokenRepository.findAll();
                    for (Token token : tokens) {
                        token.setToken(UUID.randomUUID().toString());
                        setToken(token);
                    }
                    Thread.sleep(1800_000);
                } catch (Exception ignored) {
                }
            }
        });
        timeoutThread.start();
    }

    @Override
    public ResponseEntity<String> login(String appId, String appSecret) {
        Optional<Token> tokenOptional = tokenRepository.findById(appId);
        if (tokenOptional.isEmpty()) {
            logger.info("Не существует appId {}", appId);
            return new ResponseEntity<>("", HttpStatus.UNAUTHORIZED);
        }
        Token token = tokenOptional.get();
        if (token.getAppSecret().equals(appSecret)) {
            return new ResponseEntity<>(token.getToken(), HttpStatus.OK);
        } else {
            logger.info("Неверный appSecret для appId {}", appId);
            return new ResponseEntity<>("", HttpStatus.UNAUTHORIZED);
        }
    }

    @Override
    public ResponseEntity<Boolean> validate(String appId, String token) {
        Optional<Token> tokenOptional = tokenRepository.findById(appId);
        if (tokenOptional.isEmpty()) {
            logger.info("Не существует appId {}", appId);
            return new ResponseEntity<>(Boolean.FALSE, HttpStatus.UNAUTHORIZED);
        }
        Token t = tokenOptional.get();
        if (t.getToken().equals(token))
            return new ResponseEntity<>(Boolean.TRUE, HttpStatus.OK);
        else
            return new ResponseEntity<>(Boolean.FALSE, HttpStatus.OK);
    }

    private synchronized void setToken(Token token) {
        try {
            tokenRepository.save(token);
        } catch(Exception e) {
            logger.info("Не удалось обновить токен: {}", e.getMessage());
        }
        logger.info("Обновлен токен для appId {}", token.getAppId());
    }
}
