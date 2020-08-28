package com.chat.message.service;

import com.chat.message.client.AuthorizationClient;
import com.chat.message.client.UserClient;
import com.chat.message.domain.Message;
import com.chat.message.model.MessageInfo;
import com.chat.message.model.MessagePage;
import com.chat.message.model.MessageResponse;
import com.chat.message.model.UserInfo;
import com.chat.message.repository.MessageRepository;
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
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.slf4j.LoggerFactory.getLogger;

@Service
public class MessageServiceImpl implements MessageService {
    private static final Logger logger = getLogger(MessageServiceImpl.class);
    @Value("")
    private static String USERTOKEN;
    private final String APPID;
    private final String APPSECRET;
    private final UserClient userClient;
    private final AuthorizationClient authorizationClient;
    private final MessageRepository messageRepository;

    @Autowired
    public MessageServiceImpl(Environment env, UserClient userClient, AuthorizationClient authorizationClient, MessageRepository messageRepository) {
        this.APPID = env.getRequiredProperty("userservice.appid");
        this.APPSECRET = env.getRequiredProperty("userservice.appsecret");
        this.userClient = userClient;
        this.authorizationClient = authorizationClient;
        this.messageRepository = messageRepository;
    }

    @Override
    public ResponseEntity<MessageResponse> addMessage(@Nonnull MessageInfo request) {
        final Message message = buildMessage(request);
        try {
            messageRepository.save(message);
        } catch(Exception e) {
            logger.info("Исключение при добавлении сообщения в БД: '{}'", e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        logger.info("Добавлено сообщение '{}'", message.toString());
        return new ResponseEntity<>(buildMessageResponse(message), HttpStatus.OK);
    }

    @Override
    @Transactional
    public ResponseEntity<Void> delete(@Nonnull UUID group) {
        try {
            Long count = messageRepository.deleteByGroupId(group);
            logger.info("Удалены все сообщения группы с ID {}\nКоличество удалённых сообщений - {}",
                    group.toString(), count.toString());
        } catch (Exception e) {
            logger.info("Исключение при удалении сообщений из БД: '{}'", e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Nonnull
    @Override
    public ResponseEntity<MessagePage> getMessages(@Nonnull Pageable pageable, @Nonnull UUID group) {
        logger.info("Чтение сообщений группы с пагинацией.");
        try {
            Page<Message> page = messageRepository.findAllByGroupId(group, pageable);
            MessagePage mp = new MessagePage();
            mp
                    .setCurrentPage(pageable.getPageNumber())
                    .setTotalPages(page.getTotalPages())
                    .setMessages(page.get().map(this::buildMessageResponse).collect(Collectors.toList()));
            return new ResponseEntity<>(mp, HttpStatus.OK);
        } catch(Exception ignored) {
        }
        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Nonnull
    private Message buildMessage(@Nonnull MessageInfo request) {
        return new Message()
                .setGroupId(request.getGroup())
                .setAuthorId(request.getAuthor())
                .setCreationDate(LocalDateTime.now())
                .setText(request.getText());
    }

    @Nonnull
    private MessageResponse buildMessageResponse(@Nonnull Message message) {
        UserInfo userInfo = getUserInfo(message.getAuthorId());
        String author = "";
        if (userInfo != null)
            author = userInfo.getName();
        return new MessageResponse()
                .setAuthor(author)
                .setCreationDate(message.getCreationDate())
                .setText(message.getText());
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

    private void loginUserService() {
        try {
            ResponseEntity<String> response = authorizationClient.getToken(APPID, APPSECRET);
            if (response.getStatusCode() == HttpStatus.OK)
                USERTOKEN = response.getBody();
        } catch(Exception ignored) {
        }
    }
}
