package com.dragonsofmugloar.api.client;

import com.dragonsofmugloar.api.dto.*;
import com.dragonsofmugloar.api.exception.CustomException;
import lombok.extern.java.Log;

/**
 * The rest client used to connect to REST API
 */
@Log
public class DragonsOfMugloarRestClient {

    protected RestClient restClient;
    private static final String BASE_URL = "https://dragonsofmugloar.com";
    private static final String START_GAME_ENDPOINT = "/api/v2/game/start";
    private static final String MESSAGEBOARD_ENDPOINT = "/api/v2/{gameId}/messages";
    private static final String SOLVE_ENDPOINT = "/api/v2/{gameId}/solve/{adId}";
    private static final String SHOP_ENDPOINT = "/api/v2/{gameId}/shop";
    private static final String BUY_ENDPOINT = "/api/v2/{gameId}/shop/buy/{itemId}";

    public DragonsOfMugloarRestClient() {
        restClient = new RestClient();
    }

    public GameInfoDTO startGame() throws CustomException {
        String url = BASE_URL + START_GAME_ENDPOINT;
        log.info("<POST> " + url);
        return restClient.post(url,GameInfoDTO.class);
    }

    public MessageOfBoardDTO [] getAllMessagesFromMessageBoard(String gameId) throws CustomException {
        String url = BASE_URL + MESSAGEBOARD_ENDPOINT;
        log.info(String.format("<GET> %s. [gameId : %s]", url, gameId));
        return restClient.get(BASE_URL + MESSAGEBOARD_ENDPOINT, MessageOfBoardDTO[].class, gameId);
    }

    public MessageAfterSolveDTO solve(String gameId, String adId) throws CustomException {
        String url = BASE_URL + SOLVE_ENDPOINT;
        log.info(String.format("<POST> %s. [gameId : %s, adId: %s]", url, gameId, adId));
        return restClient.post(url, MessageAfterSolveDTO.class, gameId, adId);
    }

    public ShopItemDTO [] getAllShopItems(String gameId) throws CustomException {
        String url = BASE_URL + SHOP_ENDPOINT;
        log.info(String.format("<GET> %s. [gameId : %s]", url, gameId));
        return restClient.get(url, ShopItemDTO[].class, gameId);
    }

    public MessageAfterBuyItemShop buy(String gameId, String productId) throws CustomException {
        String url = BASE_URL + BUY_ENDPOINT;
        log.info(String.format("<POST> %s. [gameId : %s, productId: %s]", url, gameId, productId));
        return restClient.post(url, MessageAfterBuyItemShop.class, gameId, productId);
    }
}
