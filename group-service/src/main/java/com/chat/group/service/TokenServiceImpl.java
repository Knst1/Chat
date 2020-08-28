package com.chat.group.service;

import com.chat.group.client.AuthorizationClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class TokenServiceImpl implements TokenService {
    private final String APPID;
    private final AuthorizationClient authorizationClient;

    @Autowired
    public TokenServiceImpl(Environment env, AuthorizationClient authorizationClient) {
        this.APPID = env.getRequiredProperty("groupservice.appid");
        this.authorizationClient = authorizationClient;
    }

    @Override
    public Optional<User> findByToken(String token) {
        ResponseEntity<Boolean> valid;
        try {
            valid = authorizationClient.validate(APPID, token);
        } catch(Exception e) {
            return  Optional.empty();
        }
        if(valid.getStatusCode() == HttpStatus.OK && valid.getBody() != null && valid.getBody()){
            User user= new User(APPID, token,true,true,true, true,
                    AuthorityUtils.createAuthorityList("USER"));
            return Optional.of(user);
        }
        return  Optional.empty();
    }
}
