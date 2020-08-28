package com.chat.group.service;

import com.chat.group.domain.ParticipantId;
import com.chat.group.model.*;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import javax.annotation.Nonnull;
import java.util.UUID;

public interface GroupService {
    ResponseEntity<GroupResponse> addGroup(@Nonnull GroupInfo request);
    ResponseEntity<ParticipantPage> getParticipants(@Nonnull Pageable pageable, @Nonnull UUID group);
    ResponseEntity<ParticipantResponse> addParticipant(@Nonnull ParticipantResponse participant);
    ResponseEntity<Void> deleteGroup(@Nonnull UUID id);
    ResponseEntity<Void> deleteParticipant(@Nonnull ParticipantId id);
}
