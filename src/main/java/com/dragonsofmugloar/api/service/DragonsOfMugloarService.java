package com.dragonsofmugloar.api.service;

import com.dragonsofmugloar.api.client.DragonsOfMugloarRestClient;
import com.dragonsofmugloar.api.dto.*;
import com.dragonsofmugloar.api.exception.CustomException;
import lombok.extern.java.Log;

import java.util.*;
import java.util.logging.Logger;

@Log
public class DragonsOfMugloarService {

    private final DragonsOfMugloarRestClient dragonsOfMugloarRestClient;
    private final StringBuilder gameSequenceOperation;

    public DragonsOfMugloarService() {
        dragonsOfMugloarRestClient = new DragonsOfMugloarRestClient();
        gameSequenceOperation = new StringBuilder();
    }

    public String playTheGame() {
        GameInfoDTO gameInfo = null;
        try {
            gameInfo = startNewGame();
            Set<String> preferenceToSolve = new HashSet<>();
            Set<String> avoidToSolve = new HashSet<>();
            while (gameInfo.getLives() > 0) {
                showGameInfo(gameInfo);
                getReputation(gameInfo);
                MessageOfBoardDTO [] arrayOfAds = getArrayOfAds(gameInfo);
                MessageOfBoardDTO task = selectAds(arrayOfAds, preferenceToSolve, avoidToSolve);
                //no task
                if (task == null) break;
                MessageAfterSolveDTO messageAfterSolve = solve(gameInfo, task);
                copyGameInfoFromTo(messageAfterSolve, gameInfo);
                if (messageAfterSolve.isSuccess()) {
                    avoidToSolve.remove(task.getProbability());
                    preferenceToSolve.add(task.getProbability());
                } else {
                    tryToPurchaseAnyItem(gameInfo);
                    preferenceToSolve.remove(task.getProbability());
                    avoidToSolve.add(task.getProbability());
                }
            }
        } catch (CustomException e) {
            log.severe(e.getMessage());
        }
        showGameInfo(gameInfo);
        return gameSequenceOperation.toString();
    }

    /**will try to buy items
     *
     * @param gameInfo
     */
    private void tryToPurchaseAnyItem(GameInfoDTO gameInfo) throws CustomException {
        //buy everything is possible from the List
        int gold = gameInfo.getGold();
        ShopItemDTO[] arrayOfShopItem = getArrayOfShopItem(gameInfo);
        Arrays.sort(arrayOfShopItem, Comparator.comparing(ShopItemDTO::getCost));
        for(ShopItemDTO item : arrayOfShopItem) {
            if (gold >= item.getCost()) {
                if (buyItem(gameInfo, item)) {
                    gold = gold - item.getCost();
                }
            } else {
                break;
            }
        }
    }

    private boolean buyItem(GameInfoDTO gameInfo, ShopItemDTO item) throws CustomException {
        log.info("Buying item...");
        gameSequenceOperation.append("Buying item...");
        gameSequenceOperation.append("\n");
        MessageAfterBuyItemShop message = dragonsOfMugloarRestClient.buy(gameInfo.getGameId(), item.getId());
        log.info(message.toString());
        gameSequenceOperation.append(message);
        gameSequenceOperation.append("\n");
        return message.isShoppingSuccess();
    }

    private ShopItemDTO [] getArrayOfShopItem(GameInfoDTO gameInfo) throws CustomException {
        log.info("Retrieving shop list...");
        gameSequenceOperation.append("Retrieving shop list...");
        gameSequenceOperation.append("\n");
        ShopItemDTO [] shopList = dragonsOfMugloarRestClient.getAllShopItems(gameInfo.getGameId());
        log.info(Arrays.toString(shopList));
        gameSequenceOperation.append(Arrays.toString(shopList));
        gameSequenceOperation.append("\n");
        return shopList;
    }

    private void showGameInfo(GameInfoDTO gameInfo) {
        if (null == gameInfo) return;
        log.info("##### GAMEID - " + gameInfo.getGameId());
        log.info("##### TURN - " + gameInfo.getTurn());
        gameSequenceOperation.append("##### TURN - " + gameInfo.getTurn());
        gameSequenceOperation.append("\n");
        log.info("##### LIVES - " + gameInfo.getLives());
        gameSequenceOperation.append("##### LIVES - " + gameInfo.getLives());
        gameSequenceOperation.append("\n");
        log.info("##### GOLD - " + gameInfo.getGold());
        gameSequenceOperation.append("##### GOLD - " + gameInfo.getGold());
        gameSequenceOperation.append("\n");
        log.info("##### SCORE - " + gameInfo.getScore());
        gameSequenceOperation.append("##### SCORE - " + gameInfo.getScore());
        gameSequenceOperation.append("\n");
        log.info("##### HIGHSCORE - " + gameInfo.getHighScore());
        gameSequenceOperation.append("##### HIGHSCORE - " + gameInfo.getHighScore());
        gameSequenceOperation.append("\n");
    }

    private void copyGameInfoFromTo(MessageAfterSolveDTO messageAfterSolve, GameInfoDTO gameInfo) {
        gameInfo.setGold(messageAfterSolve.getGold());
        gameInfo.setHighScore(messageAfterSolve.getHighScore());
        gameInfo.setScore(messageAfterSolve.getScore());
        gameInfo.setLives(messageAfterSolve.getLives());
        gameInfo.setTurn(messageAfterSolve.getTurn());
    }

    private MessageAfterSolveDTO solve(GameInfoDTO gameInfo, MessageOfBoardDTO task) throws CustomException {
        log.info(String.format("Solving the ad [%s] of gameId [%s]", task, gameInfo.getGameId()));
        gameSequenceOperation.append("Solving the ad...");
        gameSequenceOperation.append("\n");
        MessageAfterSolveDTO message = dragonsOfMugloarRestClient.solve(gameInfo.getGameId(), task.getAdId());
        log.info(message.toString());
        gameSequenceOperation.append(message);
        gameSequenceOperation.append("\n");
        return message;
    }

    private MessageOfBoardDTO selectAds(MessageOfBoardDTO[] arrayOfAds, Set<String> preferenceToSolve, Set<String> avoidToSolve) {
        if (arrayOfAds.length <= 0) return null;
        log.info("Sorting list of ads...");
        gameSequenceOperation.append("Sorting list of ads...");
        gameSequenceOperation.append("\n");
        //sort by expires and reward
        Arrays.sort(arrayOfAds, Comparator.comparing(MessageOfBoardDTO::getExpiresIn)
                .thenComparing((o1, o2) -> o2.getReward().compareTo(o1.getReward())));
        log.info(Arrays.toString(arrayOfAds));
        gameSequenceOperation.append(Arrays.toString(arrayOfAds));
        gameSequenceOperation.append("\n");

        //verify if has some preference
        Optional<MessageOfBoardDTO> preference =
                Arrays.stream(arrayOfAds).filter(messageOfBoardDTO -> preferenceToSolve.contains(messageOfBoardDTO.getProbability())).findFirst();
        if (preference.isPresent()) {
            return preference.get();
        } else {
            Optional<MessageOfBoardDTO> differentToAvoid =
                    Arrays.stream(arrayOfAds).filter(messageOfBoardDTO -> !avoidToSolve.contains(messageOfBoardDTO.getProbability())).findFirst();
            if (differentToAvoid.isPresent()) {
                return differentToAvoid.get();
            }
        }
        return arrayOfAds[0];
    }

    private MessageOfBoardDTO [] getArrayOfAds(GameInfoDTO gameInfo) throws CustomException {
        log.info("Fetching array of ads...");
        gameSequenceOperation.append("Fetching list of ads...");
        gameSequenceOperation.append("\n");
        MessageOfBoardDTO [] arrayOfAds = dragonsOfMugloarRestClient.getAllMessagesFromMessageBoard(gameInfo.getGameId());
        log.info(Arrays.toString(arrayOfAds));
        gameSequenceOperation.append(Arrays.toString(arrayOfAds));
        gameSequenceOperation.append("\n");
        //FIXME: Bug when the TASK is encrypted
        MessageOfBoardDTO[] result = Arrays.stream(arrayOfAds)
                .filter(messageOfBoardDTO -> messageOfBoardDTO.getEncrypted() == null)
                .toArray(MessageOfBoardDTO[]::new);
        return result;
    }

    private void getReputation(GameInfoDTO gameInfo) throws CustomException {
         log.info("Running an investigation...");
        gameSequenceOperation.append("Running an investigation");
        ReputationDTO investigation = dragonsOfMugloarRestClient.runInvestigation(gameInfo.getGameId());
        log.info(investigation.toString());
        gameSequenceOperation.append(investigation);
        gameSequenceOperation.append("\n");
    }

    private GameInfoDTO startNewGame() throws CustomException {
        //clear
        gameSequenceOperation.setLength(0);
        log.info("Starting new game...");
        gameSequenceOperation.append("Starting new game...");
        gameSequenceOperation.append("\n");
        GameInfoDTO gameInfo = dragonsOfMugloarRestClient.startGame();
        log.info(gameInfo.toString());
        gameSequenceOperation.append(gameInfo);
        gameSequenceOperation.append("\n");
        return gameInfo;
    }
}
