package com.chat.group.controller;

import com.chat.group.domain.ParticipantId;
import com.chat.group.model.*;
import com.chat.group.service.GroupService;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.util.UUID;

import static org.slf4j.LoggerFactory.getLogger;

// get список по страницам для группы
// post добавить
// del удалить сообщения группы

@RestController
@RequestMapping("api/group")
public class ApiGroupController {
    private static final Logger logger = getLogger(ApiGroupController.class);
    private final GroupService groupService;

    @Autowired
    public ApiGroupController(GroupService groupService) {
        this.groupService = groupService;
    }

    @PostMapping
    public ResponseEntity<GroupResponse> addGroup(@RequestBody @Valid GroupInfo request) {
        logger.info("POST запрос на добавление группы");
        return groupService.addGroup(request);
    }
/*
    @GetMapping
    public ResponseEntity<ParticipantPage> getParticipants(@RequestBody @Valid GroupRequest request) {
        logger.info("GET запрос на получение списка пользователей группы");
        return groupService.getParticipants(request);
    }
*/
    @PostMapping("participant")
    public ResponseEntity<ParticipantResponse> addParticipant(@RequestBody @Valid ParticipantResponse request) {
        logger.info("POST запрос на добавление участника группы");
        return groupService.addParticipant(request);
    }

    /*
    @PostMapping("admin")
    public ResponseEntity<ParticipantResponse> makeAdmin(@RequestBody @Valid ParticipantResponse request) {
        logger.info("POST запрос на добавление участника группы");
        return groupService.addParticipant(request);
    }
    */

    @DeleteMapping
    public ResponseEntity<Void> deleteGroup(@RequestBody @Valid UUID id) {
        logger.info("DELETE запрос на удаление группы");
        return groupService.deleteGroup(id);
    }

    @DeleteMapping("participant")
    public ResponseEntity<Void> deleteParticipant(@RequestBody @Valid ParticipantId id) {
        logger.info("DELETE запрос на удаление участника группы");
        return groupService.deleteParticipant(id);
    }
}
