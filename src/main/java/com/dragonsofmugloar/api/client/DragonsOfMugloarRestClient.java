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
    private static final String REPUTATION_ENDPOINT = "/api/v2/:gameId/investigate/reputation";
    private static final String MESSAGEBOARD_ENDPOINT = "/api/v2/:gameId/messages";
    private static final String SOLVE_ENDPOINT = "/api/v2/:gameId/solve/:adId";
    private static final String SHOP_ENDPOINT = "/api/v2/:gameId/shop";
    private static final String BUY_ENDPOINT = "/api/v2/:gameId/shop/buy/:itemId";

    public DragonsOfMugloarRestClient() {
        restClient = new RestClient();
    }

    public GameInfoDTO startGame() throws CustomException {
        return restClient.post(BASE_URL + START_GAME_ENDPOINT,GameInfoDTO.class);
    }

    public ReputationDTO runInvestigation(int gameId) throws CustomException {
        return restClient.post(BASE_URL + REPUTATION_ENDPOINT, ReputationDTO.class, gameId);
    }

    public MessageBoardDTO getAllMessagesFromMessageBoard(int gameId) throws CustomException {
        return restClient.get(BASE_URL + MESSAGEBOARD_ENDPOINT, MessageBoardDTO.class, gameId);
    }

    public MessageAfterSolveDTO solve(int gameId, int adId) throws CustomException {
        return restClient.post(BASE_URL + SOLVE_ENDPOINT, MessageAfterSolveDTO.class, gameId, adId);
    }

    public ListOfShopItemsDTO getShopItemsList(int gameId) throws CustomException {
        return restClient.get(BASE_URL + SHOP_ENDPOINT, ListOfShopItemsDTO.class, gameId);
    }

    public MessageAfterBuyItemShop buy(int gameId, int productId) throws CustomException {
        return restClient.post(BASE_URL + BUY_ENDPOINT, MessageAfterBuyItemShop.class, gameId, productId);
    }

    public static void main(String[] args) throws CustomException {
        DragonsOfMugloarRestClient restClient = new DragonsOfMugloarRestClient();
        System.out.println(restClient.startGame());
    }


}
