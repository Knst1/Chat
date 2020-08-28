package com.chat.group.domain;

import lombok.Data;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.UUID;

@Data
@Embeddable
public class ParticipantId implements Serializable {
    private UUID user;
    private UUID group;
}
