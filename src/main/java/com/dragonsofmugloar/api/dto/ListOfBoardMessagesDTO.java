package com.dragonsofmugloar.api.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Represents all Messages from message board.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@NoArgsConstructor
public class ListOfBoardMessagesDTO {
    private List<MessageOfBoardDTO> messages;
}
