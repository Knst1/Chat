package com.chat.message.service;

import com.chat.message.model.MessageInfo;
import com.chat.message.model.MessagePage;
import com.chat.message.model.MessageResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import javax.annotation.Nonnull;
import java.util.UUID;

public interface MessageService {
    ResponseEntity<MessageResponse> addMessage(@Nonnull MessageInfo request);
    ResponseEntity<Void> delete(@Nonnull UUID group);
    ResponseEntity<MessagePage> getMessages(@Nonnull Pageable pageable, @Nonnull UUID group);
}