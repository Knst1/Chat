package com.chat.message.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class MessageResponse {
    @NotNull
    private UUID authorId;
    @NotNull
    private String author;
    @NotNull
    private LocalDateTime creationDate;
    @NotNull
    private String text;
}
