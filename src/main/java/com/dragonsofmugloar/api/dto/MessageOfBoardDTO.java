package com.dragonsofmugloar.api.dto;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents one Message from message board.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@NoArgsConstructor
public class MessageOfBoardDTO {
    private String adId;
    private String message;
    private Integer reward;
    private Integer expiresIn;
    private String encrypted = null;
    private String probability;

    @Override
    public String toString() {
        return "Task{" +
                "adId='" + adId + '\'' +
                ", expiresIn='" + expiresIn + '\'' +
                ", reward=" + reward +
                ", probability='" + probability + '\'' +
                '}';
    }
}
