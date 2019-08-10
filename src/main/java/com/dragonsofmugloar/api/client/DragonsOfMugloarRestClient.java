package com.dragonsofmugloar.api.client;

import com.dragonsofmugloar.api.dto.*;
import com.dragonsofmugloar.api.exception.CustomException;

/**
 * The rest client used to connect to REST API
 */
public class DragonsOfMugloarRestClient {

    protected RestClient restClient;
    private static final String BASE_URL = "https://dragonsofmugloar.com";
    private static final String START_GAME_ENDPOINT = "/api/v2/game/start";
    private static final String REPUTATION_ENDPOINT = "/api/v2/{gameId}/investigate/reputation";
    private static final String MESSAGEBOARD_ENDPOINT = "/api/v2/{gameId}/messages";
    private static final String SOLVE_ENDPOINT = "/api/v2/{gameId}/solve/{adId}";
    private static final String SHOP_ENDPOINT = "/api/v2/{gameId}/shop";
    private static final String BUY_ENDPOINT = "/api/v2/{gameId}/shop/buy/{itemId}";

    public DragonsOfMugloarRestClient() {
        restClient = new RestClient();
    }

    public GameInfoDTO startGame() throws CustomException {
        return restClient.post(BASE_URL + START_GAME_ENDPOINT,GameInfoDTO.class);
    }

    public ReputationDTO runInvestigation(String gameId) throws CustomException {
        return restClient.post(BASE_URL + REPUTATION_ENDPOINT, ReputationDTO.class, gameId);
    }

    public MessageOfBoardDTO [] getAllMessagesFromMessageBoard(String gameId) throws CustomException {
        return restClient.get(BASE_URL + MESSAGEBOARD_ENDPOINT, MessageOfBoardDTO[].class, gameId);
    }

    public MessageAfterSolveDTO solve(String gameId, String adId) throws CustomException {
        return restClient.post(BASE_URL + SOLVE_ENDPOINT, MessageAfterSolveDTO.class, gameId, adId);
    }

    public ShopItemDTO [] getAllShopItems(String gameId) throws CustomException {
        return restClient.get(BASE_URL + SHOP_ENDPOINT, ShopItemDTO[].class, gameId);
    }

    public MessageAfterBuyItemShop buy(String gameId, String productId) throws CustomException {
        return restClient.post(BASE_URL + BUY_ENDPOINT, MessageAfterBuyItemShop.class, gameId, productId);
    }
}
