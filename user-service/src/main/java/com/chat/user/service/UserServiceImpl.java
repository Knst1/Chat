package com.chat.user.service;

import com.chat.user.domain.User;
import com.chat.user.model.UserInfo;
import com.chat.user.repository.UserRepository;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.annotation.Nonnull;

import java.util.Optional;
import java.util.UUID;

import static org.slf4j.LoggerFactory.getLogger;

@Service
public class UserServiceImpl implements UserService {
    private static final Logger logger = getLogger(UserServiceImpl.class);

    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public ResponseEntity<UserInfo> addUser(@Nonnull UserInfo request) {
        final User user = buildUser(request);
        try {
            userRepository.save(user);
        } catch(Exception e) {
            logger.info("Исключение при добавлении пользователя в БД: '{}'", e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        logger.info("Добавлен пользователь '{}'", user.toString());
        return new ResponseEntity<>(request, HttpStatus.OK);
    }

    @Nonnull
    @Override
    public ResponseEntity<UserInfo> getUser(@Nonnull UUID id) {
        Optional<User> user = userRepository.findById(id);
        if (user.isEmpty()) {
            logger.info("Не существует пользователь с ID {}", id.toString());
            return new ResponseEntity<>(new UserInfo(), HttpStatus.NOT_FOUND);
        }
        logger.info("Чтение пользователя с ID {}", id.toString());
        return new ResponseEntity<>(buildUserInfo(user.get()), HttpStatus.OK);
    }

    @Nonnull
    private User buildUser(@Nonnull UserInfo request) {
        return new User()
                .setId(request.getId())
                .setName(request.getName());
    }

    @Nonnull
    private UserInfo buildUserInfo(@Nonnull User user) {
        return new UserInfo()
                .setId(user.getId())
                .setName(user.getName());
    }

}
