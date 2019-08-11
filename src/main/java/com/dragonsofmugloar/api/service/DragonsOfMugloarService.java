package com.dragonsofmugloar.api.service;

import com.dragonsofmugloar.api.client.DragonsOfMugloarRestClient;
import com.dragonsofmugloar.api.dto.*;
import com.dragonsofmugloar.api.exception.CustomException;
import lombok.extern.java.Log;

import java.util.*;

import static com.dragonsofmugloar.api.service.util.Util.logGameInfo;

@Log
public class DragonsOfMugloarService {

    final DragonsOfMugloarRestClient dragonsOfMugloarRestClient;

    public DragonsOfMugloarService() {
        dragonsOfMugloarRestClient = new DragonsOfMugloarRestClient();
    }

    DragonsOfMugloarService(DragonsOfMugloarRestClient dragonsOfMugloarRestClient) {
        this.dragonsOfMugloarRestClient = dragonsOfMugloarRestClient;
    }

    /**
     * Main method playing a game: Start -> Retrieve Tasks -> Solve
     * @return the final GameInfo
     */
    public GameInfoDTO playTheGame() {
        GameInfoDTO gameInfo = null;
        Set<String> preferredProbabilitiesTask = new HashSet<>();
        Set<String> avoidProbabilitiesTask = new HashSet<>();
        try {
            gameInfo = startNewGame();
            while (gameInfo.getLives() > 0) {
                if (gameInfo.getLives() < 3) tryToPurchaseAnyItem(gameInfo);
                log.info(logGameInfo(gameInfo));
                //get tasks
                MessageOfBoardDTO [] arrayOfTasks = getArrayOfTasks(gameInfo);
                log.info(Arrays.toString(arrayOfTasks));
                //select task to solve
                MessageOfBoardDTO task = selectTask(arrayOfTasks, preferredProbabilitiesTask, avoidProbabilitiesTask);
                log.info("Selected task: " + task);
                //no task
                if (task == null) break;
                //try to solve
                MessageAfterSolveDTO messageAfterSolve = solve(gameInfo, task);
                updateGameInfoFrom(messageAfterSolve, gameInfo);
                log.info(messageAfterSolve.toString());
                //update memory of tasks
                if (messageAfterSolve.isSuccess()) {
                    preferredProbabilitiesTask.add(task.getProbability());
                    avoidProbabilitiesTask.remove(task.getProbability());
                } else {
                    preferredProbabilitiesTask.remove(task.getProbability());
                    avoidProbabilitiesTask.add(task.getProbability());
                }
            }
        } catch (CustomException e) {
            log.severe(e.getMessage());
        }
        log.info(logGameInfo(gameInfo));
        return gameInfo;
    }

    /**
     * Method to try buy items
     * @param gameInfo to collect the ID
     */
    void tryToPurchaseAnyItem(GameInfoDTO gameInfo) throws CustomException {
        //buy everything is possible from the List until increment life
        int gold = gameInfo.getGold();
        ShopItemDTO[] arrayOfShopItem = getArrayOfShopItem(gameInfo);
        log.info(Arrays.toString(arrayOfShopItem));
        Arrays.sort(arrayOfShopItem, Comparator.comparing(ShopItemDTO::getCost));
        int lives = gameInfo.getLives();
        for(ShopItemDTO item : arrayOfShopItem) {
            if (gold >= item.getCost()) {
                buyItem(gameInfo, item);
                if (gameInfo.getLives() > lives) break;
            } else {
                break;
            }
        }
    }

    private boolean buyItem(GameInfoDTO gameInfo, ShopItemDTO item) throws CustomException {
        log.info(String.format("Buying item [%s]", item.getName()));
        MessageAfterBuyItemShop message = dragonsOfMugloarRestClient.buyItem(gameInfo.getGameId(), item.getId());
        log.info(message.toString());
        gameInfo.setLives(message.getLives());
        gameInfo.setGold(message.getGold());
        return message.isShoppingSuccess();
    }

    private ShopItemDTO [] getArrayOfShopItem(GameInfoDTO gameInfo) throws CustomException {
        log.info("Retrieving shop list...");
        return dragonsOfMugloarRestClient.getAllShopItems(gameInfo.getGameId());
    }

    private void updateGameInfoFrom(MessageAfterSolveDTO messageAfterSolve, GameInfoDTO gameInfo) {
        gameInfo.setGold(messageAfterSolve.getGold());
        gameInfo.setHighScore(messageAfterSolve.getHighScore());
        gameInfo.setScore(messageAfterSolve.getScore());
        gameInfo.setLives(messageAfterSolve.getLives());
        gameInfo.setTurn(messageAfterSolve.getTurn());
    }

    /**
     * call solve endpoint
     * @param gameInfo to collect the game info
     * @param task to be solve
     * @return message of solution result
     * @throws CustomException in case of error
     */
    private MessageAfterSolveDTO solve(GameInfoDTO gameInfo, MessageOfBoardDTO task) throws CustomException {
        log.info(String.format("Solving the task [%s] of gameId [%s]", task, gameInfo.getGameId()));
        return dragonsOfMugloarRestClient.solveTask(gameInfo.getGameId(), task.getAdId());
    }

    /**
     * Logic: selection based on Probability of Task using the solved tasks probabilities and avoiding the avoid tasks
     * and their probabilities.
     * @param arrayOfTasks to select one
     * @param preferredProbabilitiesTask the probabilities already used and successful solved
     * @param avoidProbabilitiesTask the probabilities already used and failed to solve
     * @return the selected task
     */
    MessageOfBoardDTO selectTask(MessageOfBoardDTO[] arrayOfTasks, Set<String> preferredProbabilitiesTask,
                                 Set<String> avoidProbabilitiesTask) {
        if (arrayOfTasks.length <= 0) return null;
        //sort by expires and reward
        Arrays.sort(arrayOfTasks, Comparator.comparing(MessageOfBoardDTO::getExpiresIn)
                .thenComparing((o1, o2) -> o2.getReward().compareTo(o1.getReward())));
        //verify if has some task with any preferred probability
        Optional<MessageOfBoardDTO> preferredTask =
                Arrays.stream(arrayOfTasks).filter(messageOfBoardDTO ->
                        preferredProbabilitiesTask.contains(messageOfBoardDTO.getProbability()))
                        .findFirst();
        if (preferredTask.isPresent()) return preferredTask.get();

        //verify if has some task without any avoid probability
        Optional<MessageOfBoardDTO> otherTask =
                Arrays.stream(arrayOfTasks).filter(messageOfBoardDTO ->
                        !avoidProbabilitiesTask.contains(messageOfBoardDTO.getProbability()))
                        .findFirst();

        return otherTask.orElseGet(() -> arrayOfTasks[0]);
    }

    /**
     * Method to get the list of tasks or the message board, filtering only not encrypted messages.
     * @param gameInfo used
     * @return the array of tasks
     * @throws CustomException used in error case
     */
    MessageOfBoardDTO [] getArrayOfTasks(GameInfoDTO gameInfo) throws CustomException {
        log.info("Getting tasks...");

        MessageOfBoardDTO [] arrayOfTasks = dragonsOfMugloarRestClient.getAllMessagesFromMessageBoard(gameInfo.getGameId());
        //FIXME: Bug when the TASK is encrypted BAD REQUEST to solve - removing encrypted tasks
        return Arrays.stream(arrayOfTasks)
                .filter(messageOfBoardDTO -> messageOfBoardDTO.getEncrypted() == null)
                .toArray(MessageOfBoardDTO[]::new);
    }

    /**
     * Starting a new game
     * @return the new game info
     * @throws CustomException used in case of error
     */
    private GameInfoDTO startNewGame() throws CustomException {
        log.info("Starting new game...");
        return dragonsOfMugloarRestClient.startGame();
    }
}
