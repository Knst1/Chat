package com.chat.user.domain;

import lombok.Data;
import lombok.experimental.Accessors;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import javax.persistence.*;
import java.util.UUID;

import org.hibernate.validator.constraints.Length;

@Data
@Entity
@Accessors(chain = true)
@Table(name = "usr")
public class User {
    @Id
    @Column(name = "id")
    private UUID id;

    @Column(name = "name", nullable = false)
    @Length(min = 4, max = 64)
    private String name;
}
