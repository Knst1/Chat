package com.chat.authorization.domain;

import lombok.Data;
import lombok.experimental.Accessors;
import javax.persistence.*;

@Data
@Entity
@Accessors(chain = true)
@Table(name = "token")
public class Token {
    @Id
    @Column(name = "appId", nullable = false)
    private String appId;

    @Column(name = "appSecret", nullable = false)
    private String appSecret;

    @Column(name = "token", nullable = false)
    private String token;
}
