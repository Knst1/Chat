package com.chat.group.service;

import com.chat.group.client.AuthorizationClient;
import com.chat.group.client.MessageClient;
import com.chat.group.client.UserClient;
import com.chat.group.domain.Group;
import com.chat.group.domain.Participant;
import com.chat.group.domain.ParticipantId;
import com.chat.group.model.*;
import com.chat.group.repository.GroupRepository;
import com.chat.group.repository.ParticipantRepository;
import feign.FeignException;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import javax.annotation.Nonnull;
import javax.persistence.EntityNotFoundException;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.slf4j.LoggerFactory.getLogger;

@Service
public class GroupServiceImpl implements GroupService {
    private static final Logger logger = getLogger(GroupServiceImpl.class);
    @Value("")
    private static String USERTOKEN;
    private final String USERID;
    private final String USERSECRET;
    @Value("")
    private static String MESSAGETOKEN;
    private final String MESSAGEID;
    private final String MESSAGESECRET;
    private final UserClient userClient;
    private final MessageClient messageClient;
    private final GroupRepository groupRepository;
    private final ParticipantRepository participantRepository;
    private final AuthorizationClient authorizationClient;

    @Autowired
    public GroupServiceImpl(Environment env, UserClient userClient, GroupRepository groupRepository,
                            ParticipantRepository participantRepository, AuthorizationClient authorizationClient,
                            MessageClient messageClient) {
        this.USERID = env.getRequiredProperty("userservice.appid");
        this.USERSECRET = env.getRequiredProperty("userservice.appsecret");
        this.MESSAGEID = env.getRequiredProperty("messageservice.appid");
        this.MESSAGESECRET = env.getRequiredProperty("messageservice.appsecret");
        this.userClient = userClient;
        this.messageClient = messageClient;
        this.groupRepository = groupRepository;
        this.participantRepository = participantRepository;
        this.authorizationClient = authorizationClient;
    }

    @Override
    public ResponseEntity<GroupResponse> addGroup(@Nonnull GroupInfo request) {
        Group group = buildGroup(request);
        try {
            Group g = groupRepository.save(group);
            Participant firstParticipant = buildCreator(g, request);
            participantRepository.save(firstParticipant);
            logger.info("Добавлена группа '{}'", g.toString());
            return new ResponseEntity<>(new GroupResponse(g.getId(), g.getName()), HttpStatus.OK);
        } catch(Exception e) {
            logger.info("Исключение при добавлении группы в БД: '{}'", e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity<ParticipantPage> getParticipants(@Nonnull Pageable pageable, @Nonnull UUID group) {
        logger.info("Чтение участников группы с пагинацией.");
        try {
            Page<Participant> page = participantRepository.findAllByGroupId(group, pageable);
            ParticipantPage pp = new ParticipantPage();
            pp
                    .setCurrentPage(pageable.getPageNumber())
                    .setTotalPages(page.getTotalPages())
                    .setParticipants(page.get().map(this::buildParticipantInfo).collect(Collectors.toList()));
            return new ResponseEntity<>(pp, HttpStatus.OK);
        } catch(Exception ignored) {
        }
        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<Void> deleteGroup(@Nonnull UUID id) {
        try {
            if (deleteMessages(id) == HttpStatus.OK)
                groupRepository.deleteById(id);
            else {
                logger.info("Исключение при удалении группы из БД: сервис сообщений недоступен");
                return new ResponseEntity<>(HttpStatus.SERVICE_UNAVAILABLE);
            }
        } catch(Exception e) {
            logger.info("Исключение при удалении группы из БД: '{}'", e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        logger.info("Удалена группа с ID '{}'", id.toString());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Void> deleteParticipant(@Nonnull ParticipantId id) {
        try {
            if (participantRepository.findById(id).isPresent())
                if (!participantRepository.findById(id).get().getAdmin())
                    participantRepository.deleteById(id);
                else
                    if (participantRepository.countAllByAdmin(Boolean.TRUE) > 1) {
                        participantRepository.deleteById(id);
                        if (participantRepository.countAllByAdmin(Boolean.TRUE) < 1) {
                            final Group group = groupRepository
                                    .findById(id.getGroup())
                                    .orElseThrow(() -> new EntityNotFoundException("Не найдено группы с UUID " +
                                            id.getGroup()));
                            final Participant participant = buildParticipant(group, id);
                            participantRepository.save(participant);
                            logger.info("Попытка удаления единственного администратора группы");
                        }
                    }
            else
                logger.info("Попытка удаления несуществующего участника группы");
        } catch(Exception e) {
            logger.info("Исключение при удалении участника группы из БД: '{}'", e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        logger.info("Удален участник группы. ID удалённого участника '{}'", id.toString());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Override
    public ResponseEntity<ParticipantResponse> addParticipant(@Nonnull ParticipantResponse response) {
        try {
        final Group group = groupRepository
                .findById(response.getGroup())
                .orElseThrow(() -> new EntityNotFoundException("Не найдено группы с UUID " +
                        response.getGroup()));
            final Participant participant = buildParticipant(group, response);
            participantRepository.save(participant);
            logger.info("В группу добавлен участник '{}'", participant.toString());
        } catch(Exception e) {
            logger.info("Исключение при добавлении участника группы в БД: '{}'", e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Nonnull
    private Group buildGroup(@Nonnull GroupInfo request) {
        return new Group().setName(request.getName());
    }

    @Nonnull
    private Participant buildParticipant(@Nonnull Group group, @Nonnull ParticipantResponse response) {
        return new Participant()
                .setUser(response.getUser())
                .setGroup(group)
                .setAdmin(response.getAdmin());
    }

    @Nonnull
    private Participant buildParticipant(@Nonnull Group group, @Nonnull ParticipantId id) {
        return new Participant()
                .setUser(id.getUser())
                .setGroup(group)
                .setAdmin(Boolean.TRUE);
    }

    @Nonnull
    private Participant buildCreator(@Nonnull Group group, @Nonnull GroupInfo request) {
        return new Participant()
                .setUser(request.getCreator())
                .setGroup(group)
                .setAdmin(Boolean.TRUE);
    }

    private UserInfo getUserInfo(UUID id) {
        ResponseEntity<UserInfo> response;
        try {
            response = userClient.getUser(id, "Bearer " + USERTOKEN);
            if (response.getStatusCode() == HttpStatus.OK)
                return response.getBody();
        } catch (FeignException e) {
            if (e.status() == 401) {
                loginUserService();
                response = userClient.getUser(id, "Bearer " + USERTOKEN);
                if (response.getStatusCode() == HttpStatus.OK)
                    return response.getBody();
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }

    private HttpStatus deleteMessages(UUID id) {
        ResponseEntity<Void> response;
        try {
            response = messageClient.remove(id, "Bearer " + MESSAGETOKEN);
            return response.getStatusCode();
        } catch (FeignException e) {
            if (e.status() == 401) {
                loginMessageService();
                response = messageClient.remove(id, "Bearer " + MESSAGETOKEN);
                return response.getStatusCode();
            }
        } catch (Exception e) {
            return HttpStatus.SERVICE_UNAVAILABLE;
        }
        return HttpStatus.SERVICE_UNAVAILABLE;
    }

    @Nonnull
    private ParticipantInfo buildParticipantInfo(@Nonnull Participant participant) {
        UserInfo userInfo = getUserInfo(participant.getUser());
        String user = "";
        if (userInfo != null)
            user = userInfo.getName();
        return new ParticipantInfo()
                .setUser(participant.getUser())
                .setName(user)
                .setGroup(participant.getGroup().getId())
                .setAdmin(participant.getAdmin());
    }

    private void loginUserService() {
        try {
            ResponseEntity<String> response = authorizationClient.getToken(USERID, USERSECRET);
            if (response.getStatusCode() == HttpStatus.OK)
                USERTOKEN = response.getBody();
        } catch(Exception ignored) {
        }
    }

    private void loginMessageService() {
        try {
            ResponseEntity<String> response = authorizationClient.getToken(MESSAGEID, MESSAGESECRET);
            if (response.getStatusCode() == HttpStatus.OK)
                MESSAGETOKEN = response.getBody();
        } catch(Exception ignored) {
        }
    }
}
