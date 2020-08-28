package com.chat.authorization.repository;

import com.chat.authorization.domain.Token;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.annotation.Nonnull;
import java.util.Optional;


public interface TokenRepository extends JpaRepository<Token, String> {
    Optional<Token> findById (@Nonnull String id);
}
