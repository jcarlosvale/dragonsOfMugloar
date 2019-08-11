package com.dragonsofmugloar.api.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents the Message received after try to buy item from shop
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageAfterBuyItemShop {
    private boolean shoppingSuccess;
    private int gold;
    private int lives;
    private int level;
    private int turn;
}
