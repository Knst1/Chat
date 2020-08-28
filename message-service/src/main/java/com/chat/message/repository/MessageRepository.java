package com.chat.message.repository;

import com.chat.message.domain.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.annotation.Nonnull;
import java.util.UUID;


public interface MessageRepository extends JpaRepository<Message, UUID> {
    Page<Message> findAllByGroupId(@Nonnull UUID group, @Nonnull Pageable page);
    Long deleteByGroupId(@Nonnull UUID group);
}
