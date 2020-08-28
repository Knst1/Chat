package com.chat.user.controller;

import com.chat.user.model.UserInfo;
import com.chat.user.service.UserService;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.util.UUID;

import static org.slf4j.LoggerFactory.getLogger;

// get имя по id
// get список по страницам
// get список по стр для поиска
// post добавить
// post изменить имя по id
// del

@RestController
@RequestMapping("api/user")
public class ApiUserController {
    private static final Logger logger = getLogger(ApiUserController.class);
    private final UserService userService;

    @Autowired
    public ApiUserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<UserInfo> addUser(@RequestBody @Valid UserInfo request, @RequestHeader("Authorization") String token) {
        logger.info("POST запрос на добавление пользователя");
        return userService.addUser(request);
    }

    @GetMapping("{id}")
    public ResponseEntity<UserInfo> getUser(@PathVariable UUID id) {
        logger.info("GET запрос на получение пользователя по ID");
        return userService.getUser(id);
    }
}
