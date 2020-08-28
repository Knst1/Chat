package com.chat.message.service;

import org.springframework.security.core.userdetails.User;

import java.util.Optional;

public interface TokenService {
    Optional<User> findByToken(String token);
}
