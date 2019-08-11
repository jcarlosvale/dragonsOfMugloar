package com.dragonsofmugloar.api.client;

import com.dragonsofmugloar.api.dto.*;
import com.dragonsofmugloar.api.exception.CustomException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

public class DragonsOfMugloarRestClientTest {

    private DragonsOfMugloarRestClient dragonsOfMugloarRestClient;

    @Before
    public void initialize() {
        dragonsOfMugloarRestClient = new DragonsOfMugloarRestClient();
        dragonsOfMugloarRestClient.restClient = Mockito.mock(RestClient.class);
    }

    @Test
    public void verifyStartGameURL() throws CustomException {
        String expectedURL = "https://dragonsofmugloar.com/api/v2/game/start";
        GameInfoDTO expectedDTO = new GameInfoDTO("some", 1, 2, 3, 4, 5, 6);
        when(dragonsOfMugloarRestClient.restClient.post(expectedURL, GameInfoDTO.class)).thenReturn(expectedDTO);
        GameInfoDTO actualDTO = dragonsOfMugloarRestClient.startGame();
        assertEquals(expectedDTO, actualDTO);
    }

    @Test
    public void verifyGetAllMessagesFromMessageBoardURL() throws CustomException {
        String expectedURL = "https://dragonsofmugloar.com/api/v2/{gameId}/messages";

        GameInfoDTO gameInfoDTO = new GameInfoDTO("some", 1, 2, 3, 4, 5, 6);
        MessageOfBoardDTO[] expectedDTO = new MessageOfBoardDTO[]{
                new MessageOfBoardDTO("some adID", "some message", 1, 2, null, "some probability")};

        when(dragonsOfMugloarRestClient.restClient.get(expectedURL, MessageOfBoardDTO[].class, gameInfoDTO.getGameId())).thenReturn(expectedDTO);
        MessageOfBoardDTO[] actualDTO = dragonsOfMugloarRestClient.getAllMessagesFromMessageBoard(gameInfoDTO.getGameId());
        assertArrayEquals(expectedDTO, actualDTO);
    }

    @Test
    public void verifySolveTaskURL() throws CustomException {
        String expectedURL = "https://dragonsofmugloar.com/api/v2/{gameId}/solve/{adId}";

        GameInfoDTO gameInfoDTO = new GameInfoDTO("some", 1, 2, 3, 4, 5, 6);
        MessageOfBoardDTO messageOfBoardDTO =
                new MessageOfBoardDTO("some adID", "some message", 1, 2, null, "some probability");

        MessageAfterSolveDTO expectedDTO = new MessageAfterSolveDTO(false, 1, 2, 3, 4, 5, "some message");

        when(dragonsOfMugloarRestClient.restClient
                .post(expectedURL, MessageAfterSolveDTO.class, gameInfoDTO.getGameId(), messageOfBoardDTO.getAdId()))
                .thenReturn(expectedDTO);

        MessageAfterSolveDTO actualDTO = dragonsOfMugloarRestClient.solveTask(gameInfoDTO.getGameId(), messageOfBoardDTO.getAdId());

        assertEquals(expectedDTO, actualDTO);
    }

    @Test
    public void verifyGetAllShopItemsURL() throws CustomException {
        String expectedURL = "https://dragonsofmugloar.com/api/v2/{gameId}/shop";
        GameInfoDTO gameInfoDTO = new GameInfoDTO("some", 1, 2, 3, 4, 5, 6);
        ShopItemDTO[] expectedDTO = new ShopItemDTO[]{new ShopItemDTO("some id", "some desc", 1)};

        when(dragonsOfMugloarRestClient.restClient
                .get(expectedURL, ShopItemDTO[].class, gameInfoDTO.getGameId())).thenReturn(expectedDTO);

        ShopItemDTO[] actualDTO = dragonsOfMugloarRestClient.getAllShopItems(gameInfoDTO.getGameId());
        assertArrayEquals(expectedDTO, actualDTO);
    }

    @Test
    public void verifybuyItemURL() throws CustomException {
        String expectedURL = "https://dragonsofmugloar.com/api/v2/{gameId}/shop/buy/{itemId}";
        GameInfoDTO gameInfoDTO = new GameInfoDTO("some", 1, 2, 3, 4, 5, 6);
        ShopItemDTO shopItemDTO = new ShopItemDTO("some id", "some desc", 1);
        MessageAfterBuyItemShop expectedDTO = new MessageAfterBuyItemShop(true, 1,2,3,4);

        when(dragonsOfMugloarRestClient.restClient
                .post(expectedURL, MessageAfterBuyItemShop.class, gameInfoDTO.getGameId(), shopItemDTO.getId()))
                .thenReturn(expectedDTO);

        MessageAfterBuyItemShop actualDTO = dragonsOfMugloarRestClient.buyItem(gameInfoDTO.getGameId(), shopItemDTO.getId());

    }
}