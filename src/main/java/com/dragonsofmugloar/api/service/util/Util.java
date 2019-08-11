package com.dragonsofmugloar.api.service.util;

import com.dragonsofmugloar.api.dto.GameInfoDTO;

/**
 * A simple util class to print the GameInfo
 */
public class Util {
    public static String logGameInfo(GameInfoDTO gameInfo) {
        if (null == gameInfo) return "";
        return String.format("\n##### GAMEID - %s\n" +
                "##### TURN - %d\n" +
                "##### LIVES - %d\n" +
                "##### GOLD - %d\n" +
                "##### SCORE - %d\n" +
                "##### HIGHSCORE - %d\n",
                gameInfo.getGameId(),
                gameInfo.getTurn(),
                gameInfo.getLives(),
                gameInfo.getGold(),
                gameInfo.getScore(),
                gameInfo.getHighScore());
    }
}
