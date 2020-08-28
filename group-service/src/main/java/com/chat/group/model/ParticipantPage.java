package com.chat.group.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class ParticipantPage {
    private List<ParticipantInfo> participants;
    private int currentPage;
    private int totalPages;
}
