package com.chat.group.repository;

import com.chat.group.domain.Group;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.annotation.Nonnull;
import java.util.Optional;
import java.util.UUID;


public interface GroupRepository extends JpaRepository<Group, UUID> {
    Optional<Group> findById (@Nonnull UUID id);
}
