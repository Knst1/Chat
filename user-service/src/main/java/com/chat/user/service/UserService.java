package com.chat.user.service;

import com.chat.user.model.UserInfo;
import org.springframework.http.ResponseEntity;

import javax.annotation.Nonnull;
import java.util.UUID;

public interface UserService {
    ResponseEntity<UserInfo> addUser(@Nonnull UserInfo request);
    @Nonnull
    ResponseEntity<UserInfo> getUser(@Nonnull UUID id);
}