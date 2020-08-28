package com.chat.group.domain;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.util.UUID;

@Data
@Entity
@IdClass(ParticipantId.class)
@Accessors(chain = true)
@Table(name = "participant")
public class Participant {
    @Id
    @Column(name = "usr_id")
    private UUID user;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", referencedColumnName = "id", nullable = false)
    private Group group;

    @Column(name = "admin", nullable = false)
    private Boolean admin;
}
