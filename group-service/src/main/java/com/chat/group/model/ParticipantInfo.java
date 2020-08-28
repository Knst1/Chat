package com.chat.group.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class ParticipantInfo {
    @NotNull
    private UUID user;
    @NotNull
    private String name;
    @NotNull
    private UUID group;
    @NotNull
    private Boolean admin;
}
