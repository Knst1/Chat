package com.chat.group.repository;

import com.chat.group.domain.Participant;
import com.chat.group.domain.ParticipantId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.annotation.Nonnull;
import java.util.UUID;

public interface ParticipantRepository extends JpaRepository<Participant, ParticipantId> {
    Page<Participant> findAllByGroupId(@Nonnull UUID group, @Nonnull Pageable page);
    Integer countAllByAdmin(@Nonnull Boolean b);
}
