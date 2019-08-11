package com.dragonsofmugloar.api.service;

import com.dragonsofmugloar.api.client.DragonsOfMugloarRestClient;
import com.dragonsofmugloar.api.dto.*;
import com.dragonsofmugloar.api.exception.CustomException;
import org.junit.Before;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DragonsOfMugloarServiceTest {

    private DragonsOfMugloarService service;

    @Before
    public void initialize() {
        service = new DragonsOfMugloarService(mock(DragonsOfMugloarRestClient.class));
    }

    @Test
    public void testBuyItemIncreaseLife() throws CustomException {
        GameInfoDTO gameInfo = new GameInfoDTO("some ID", 0, 100, 0, 0, 0, 0);
        ShopItemDTO[] listItems = {new ShopItemDTO("1", "any", 20), new ShopItemDTO("2", "any", 80)};

        when(service.dragonsOfMugloarRestClient.getAllShopItems(gameInfo.getGameId())).thenReturn(listItems);
        when(service.dragonsOfMugloarRestClient.buyItem(gameInfo.getGameId(), "1"))
                .thenReturn(new MessageAfterBuyItemShop(true, 80,0,0,0));
        when(service.dragonsOfMugloarRestClient.buyItem(gameInfo.getGameId(), "2"))
                .thenReturn(new MessageAfterBuyItemShop(true, 0,1,0,0));

        service.tryToPurchaseAnyItem(gameInfo);

        assertEquals(1, gameInfo.getLives());
        assertEquals(0, gameInfo.getGold());
    }

    @Test
    public void testGetArrayOfTasks() throws CustomException {
        GameInfoDTO gameInfo = new GameInfoDTO("some ID", 0, 100, 0, 0, 0, 0);
        MessageOfBoardDTO[] arrayOfTasks = {
                new MessageOfBoardDTO("1","some",0,0,"true"," "),
                new MessageOfBoardDTO("2","some",0,0,null," "),
                new MessageOfBoardDTO("3","some",0,0,"true"," ")};
        MessageOfBoardDTO[] expectedArrayOfTasks = {new MessageOfBoardDTO("2","some",0,0,null," ")};

        when(service.dragonsOfMugloarRestClient.getAllMessagesFromMessageBoard(gameInfo.getGameId())).thenReturn(arrayOfTasks);

        MessageOfBoardDTO[] actualArrayOfTasks = service.getArrayOfTasks(gameInfo);

        assertArrayEquals(expectedArrayOfTasks, actualArrayOfTasks);
    }

    @Test
    public void testSelectTaskFirstExecution() {
        MessageOfBoardDTO[] arrayOfTasks = generateArrayOfTasks();
        Set<String> preferredProbabilitiesTask = new HashSet<>();
        Set<String> avoidProbabilitiesTask = new HashSet<>();
        MessageOfBoardDTO actualTask = service.selectTask(arrayOfTasks, preferredProbabilitiesTask, avoidProbabilitiesTask);
        assertEquals(arrayOfTasks[0], actualTask);
    }

    @Test
    public void testSelectPreferredTask() {
        MessageOfBoardDTO[] arrayOfTasks = generateArrayOfTasks();
        Set<String> preferredProbabilitiesTask = new HashSet<>();
        preferredProbabilitiesTask.add("preferred");
        Set<String> avoidProbabilitiesTask = new HashSet<>();
        MessageOfBoardDTO actualTask = service.selectTask(arrayOfTasks, preferredProbabilitiesTask, avoidProbabilitiesTask);
        assertEquals("preferred", actualTask.getProbability());
    }

    @Test
    public void testSelectNeutralTask() {
        MessageOfBoardDTO[] arrayOfTasks = generateArrayOfTasks();
        Set<String> preferredProbabilitiesTask = new HashSet<>();
        Set<String> avoidProbabilitiesTask = new HashSet<>();
        avoidProbabilitiesTask.add("preferred");
        avoidProbabilitiesTask.add("avoid");
        MessageOfBoardDTO actualTask = service.selectTask(arrayOfTasks, preferredProbabilitiesTask, avoidProbabilitiesTask);
        assertEquals("neutral", actualTask.getProbability());
    }

    @Test
    public void testPlayTheGame() throws CustomException {
        GameInfoDTO gameInfo = new GameInfoDTO("some ID", 1, 20, 0, 0, 0, 0);
        ShopItemDTO[] listItems = {new ShopItemDTO("1", "any", 20)};
        MessageOfBoardDTO[] arrayOfTasks = {
                new MessageOfBoardDTO("1","task 1",10,0,null,"avoid")};
        MessageAfterSolveDTO messageAfterSolve = new MessageAfterSolveDTO(false, 0,20,1,4,1,"");
        GameInfoDTO expectedGameInfo = new GameInfoDTO("some ID", 0, 20, 0, 1, 4, 1);

        when(service.dragonsOfMugloarRestClient.startGame()).thenReturn(gameInfo);
        when(service.dragonsOfMugloarRestClient.getAllShopItems(gameInfo.getGameId())).thenReturn(listItems);
        when(service.dragonsOfMugloarRestClient.buyItem(gameInfo.getGameId(), "1"))
                .thenReturn(new MessageAfterBuyItemShop(false, 0,0,0,0));
        when(service.dragonsOfMugloarRestClient.getAllMessagesFromMessageBoard(gameInfo.getGameId())).thenReturn(arrayOfTasks);
        when(service.dragonsOfMugloarRestClient.solveTask(gameInfo.getGameId(), "1")).thenReturn(messageAfterSolve);


        GameInfoDTO actualGameInfo = service.playTheGame();
        assertEquals(expectedGameInfo, actualGameInfo);
    }

    private MessageOfBoardDTO[] generateArrayOfTasks() {
        return new MessageOfBoardDTO[] {
                new MessageOfBoardDTO("1","task 1",10,0,null,"avoid"),
                new MessageOfBoardDTO("2","task 2",20,0,null,"neutral"),
                new MessageOfBoardDTO("3","task 3",30,0,null,"preferred")
        };
    }


}