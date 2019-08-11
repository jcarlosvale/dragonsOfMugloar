package com.dragonsofmugloar.api.service.util;

import com.dragonsofmugloar.api.dto.GameInfoDTO;
import com.dragonsofmugloar.api.dto.MessageOfBoardDTO;
import lombok.extern.java.Log;

import java.util.Arrays;

@Log
public class Util {
    public static void logGameInfo(GameInfoDTO gameInfo) {
        if (null == gameInfo) return;
        String info = String.format("\n##### GAMEID - %s\n" +
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
        log.info(info);
    }
}
