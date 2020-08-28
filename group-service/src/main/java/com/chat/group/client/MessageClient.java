package com.chat.group.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.UUID;

@FeignClient("message-service")
public interface MessageClient {

    @DeleteMapping("api/message")
    ResponseEntity<Void> remove(@RequestBody UUID group, @RequestHeader("Authorization") String token);
}
