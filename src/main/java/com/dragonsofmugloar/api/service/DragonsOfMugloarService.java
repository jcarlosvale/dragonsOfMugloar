package com.dragonsofmugloar.api.service;

import com.dragonsofmugloar.api.client.DragonsOfMugloarRestClient;
import com.dragonsofmugloar.api.dto.*;
import com.dragonsofmugloar.api.exception.CustomException;
import lombok.extern.java.Log;

import java.util.*;
import java.util.stream.Collectors;

import static com.dragonsofmugloar.api.service.util.Util.logGameInfo;

@Log
public class DragonsOfMugloarService {

    private final DragonsOfMugloarRestClient dragonsOfMugloarRestClient;
    private final Set<MessageOfBoardDTO> tasksToAvoid;

    public DragonsOfMugloarService() {
        dragonsOfMugloarRestClient = new DragonsOfMugloarRestClient();
        tasksToAvoid = new HashSet<>();
    }

    /**
     * Main method playing a game: Start -> Retrieve Tasks -> Solve
     * @return
     */
    public GameInfoDTO playTheGame() {
        GameInfoDTO gameInfo = null;
        try {
            gameInfo = startNewGame();
            while (gameInfo.getLives() > 0) {
                if (gameInfo.getLives() < 3) tryToPurchaseAnyItem(gameInfo);
                logGameInfo(gameInfo);
                //get tasks
                MessageOfBoardDTO [] arrayOfTasks = getArrayOfTasks(gameInfo);
                log.info(Arrays.toString(arrayOfTasks));
                //select task to solve
                syncTasksToAvoid(arrayOfTasks);
                log.info("LIST PROBS " + Arrays.stream(arrayOfTasks).map(MessageOfBoardDTO::getProbability).collect(Collectors.toSet()));
                log.info("AVOID PROBS" + tasksToAvoid.stream().map(MessageOfBoardDTO::getProbability).collect(Collectors.toSet()));
                MessageOfBoardDTO task = selectTask(arrayOfTasks);
                log.info("Selected task: " + task);
                //no task
                if (task == null) break;
                //try to solve
                MessageAfterSolveDTO messageAfterSolve = solve(gameInfo, task);
                copyGameInfoFromTo(messageAfterSolve, gameInfo);
                log.info(messageAfterSolve.toString());
                if (messageAfterSolve.isSuccess()) {
                    tasksToAvoid.remove(task);
                } else {
                    tryToPurchaseAnyItem(gameInfo);
                    tasksToAvoid.add(task);
                }
            }
        } catch (CustomException e) {
            log.severe(e.getMessage());
        }
        logGameInfo(gameInfo);
        return gameInfo;
    }

    private void syncTasksToAvoid(MessageOfBoardDTO[] arrayOfTasks) {
        Set<MessageOfBoardDTO> retainSet = tasksToAvoid.stream().filter(messageOfBoardDTO -> {
            for (MessageOfBoardDTO message : arrayOfTasks) {
                if (messageOfBoardDTO.equals(message)) return true;
            }
            return false;
        }).collect(Collectors.toSet());
        tasksToAvoid.retainAll(retainSet);
    }

    /**will try to buy items
     *
     * @param gameInfo
     */
    private void tryToPurchaseAnyItem(GameInfoDTO gameInfo) throws CustomException {
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
        MessageAfterBuyItemShop message = dragonsOfMugloarRestClient.buy(gameInfo.getGameId(), item.getId());
        log.info(message.toString());
        gameInfo.setLives(message.getLives());
        gameInfo.setGold(message.getGold());
        return message.isShoppingSuccess();
    }

    private ShopItemDTO [] getArrayOfShopItem(GameInfoDTO gameInfo) throws CustomException {
        log.info("Retrieving shop list...");
        ShopItemDTO [] shopList = dragonsOfMugloarRestClient.getAllShopItems(gameInfo.getGameId());
        return shopList;
    }

    private void copyGameInfoFromTo(MessageAfterSolveDTO messageAfterSolve, GameInfoDTO gameInfo) {
        gameInfo.setGold(messageAfterSolve.getGold());
        gameInfo.setHighScore(messageAfterSolve.getHighScore());
        gameInfo.setScore(messageAfterSolve.getScore());
        gameInfo.setLives(messageAfterSolve.getLives());
        gameInfo.setTurn(messageAfterSolve.getTurn());
    }

    /**
     * call solve endpoint
     * @param gameInfo
     * @param task
     * @return
     * @throws CustomException
     */
    private MessageAfterSolveDTO solve(GameInfoDTO gameInfo, MessageOfBoardDTO task) throws CustomException {
        log.info(String.format("Solving the task [%s] of gameId [%s]", task, gameInfo.getGameId()));
        MessageAfterSolveDTO message = dragonsOfMugloarRestClient.solve(gameInfo.getGameId(), task.getAdId());
        return message;
    }

    /**
     * Logic: selection based on Probability of Task using the solved tasks probabilities and avoiding the avoid tasks
     * and their probabilities.
     * @param arrayOfTasks
     * @return
     */
    private MessageOfBoardDTO selectTask(MessageOfBoardDTO[] arrayOfTasks) {
        if (arrayOfTasks.length <= 0) return null;
        //sort by expires and reward
        Arrays.sort(arrayOfTasks, Comparator.comparing(MessageOfBoardDTO::getExpiresIn)
                .thenComparing((o1, o2) -> o2.getReward().compareTo(o1.getReward())));
        //probabilities to avoid
        Set<String> avoidProbabilitiesSet = tasksToAvoid.stream().map(MessageOfBoardDTO::getProbability).collect(Collectors.toSet());
        //verify if has some task with the same probability of any solved task AND it is not a task to avoid
        Optional<MessageOfBoardDTO> otherTask =
                Arrays.stream(arrayOfTasks).filter(messageOfBoardDTO ->
                        !avoidProbabilitiesSet.contains(messageOfBoardDTO.getProbability())
                                && !tasksToAvoid.contains(messageOfBoardDTO))
                        .findFirst();
        return otherTask.orElseGet(() -> arrayOfTasks[0]);
    }

    /**
     * Method to get the list of tasks or the message board, filtering only not encrypted messages.
     * @param gameInfo
     * @return
     * @throws CustomException
     */
    private MessageOfBoardDTO [] getArrayOfTasks(GameInfoDTO gameInfo) throws CustomException {
        log.info("Getting tasks...");

        MessageOfBoardDTO [] arrayOfTasks = dragonsOfMugloarRestClient.getAllMessagesFromMessageBoard(gameInfo.getGameId());
        //FIXME: Bug when the TASK is encrypted BAD REQUEST to solve - removing encrypted tasks
        MessageOfBoardDTO[] result = Arrays.stream(arrayOfTasks)
                .filter(messageOfBoardDTO -> messageOfBoardDTO.getEncrypted() == null)
                .toArray(MessageOfBoardDTO[]::new);
        return result;
    }

    /**
     * Starting a new game
     * @return
     * @throws CustomException
     */
    private GameInfoDTO startNewGame() throws CustomException {
        log.info("Starting new game...");
        resetFields();
        GameInfoDTO gameInfo = dragonsOfMugloarRestClient.startGame();
        return gameInfo;
    }

    /**
     * reset or clear the tasks: to avoid, solved
     */
    private void resetFields() {
        //clear
        tasksToAvoid.clear();
    }
}
