package com.chat.group.client;

import com.chat.group.model.UserInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.UUID;

@FeignClient("user-service")
public interface UserClient {

    @GetMapping("api/user/{id}")
    ResponseEntity<UserInfo> getUser(@PathVariable UUID id, @RequestHeader("Authorization") String token);
}
