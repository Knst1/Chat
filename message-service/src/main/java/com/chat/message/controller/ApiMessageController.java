package com.chat.message.controller;

import com.chat.message.model.MessageInfo;
import com.chat.message.model.MessagePage;
import com.chat.message.model.MessageResponse;
import com.chat.message.service.MessageService;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.UUID;

import static org.slf4j.LoggerFactory.getLogger;

// get список по страницам для группы
// post добавить
// del удалить сообщения группы

@RestController
@RequestMapping("api/message")
public class ApiMessageController {
    public static final int MESSAGES_PER_PAGE = 30;
    private static final Logger logger = getLogger(ApiMessageController.class);
    private final MessageService messageService;
    @Autowired
    public ApiMessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @GetMapping("{groupId}")
    public  ResponseEntity<MessagePage> getMessagePage(@PathVariable("groupId") UUID groupId,
            @PageableDefault(size = MESSAGES_PER_PAGE, sort = { "creationDate" }, direction = Sort.Direction.DESC) Pageable pageable) {
        logger.info("GET запрос на выдачу страницы с сообщениями");
        return messageService.getMessages(pageable, groupId);
    }

    @PostMapping
    public ResponseEntity<MessageResponse> addMessage(@RequestBody @Valid MessageInfo request) {
        logger.info("POST запрос на добавление сообщения");
        return messageService.addMessage(request);
    }

    @DeleteMapping
    public ResponseEntity<Void> remove(@RequestBody @Valid UUID group) {
        logger.info("DELETE запрос на удаление всех сообщений группы");
        return messageService.delete(group);
    }
}

