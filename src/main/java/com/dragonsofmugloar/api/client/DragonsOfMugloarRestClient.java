package com.dragonsofmugloar.api.client;

import com.dragonsofmugloar.api.dto.GameInfoDTO;
import com.dragonsofmugloar.api.dto.ReputationDTO;
import com.dragonsofmugloar.api.exception.CustomException;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

/**
 * The rest client used to connect to REST API
 */
public class DragonsOfMugloarRestClient {

    protected RestTemplate restTemplate;
    public static final String BASE_URL = "https://dragonsofmugloar.com";
    public static final String START_GAME_ENDPOINT = "/api/v2/game/start";
    public static final String REPUTATION_ENDPOINT = "/api/v2/:gameId/investigate/reputation";

    public DragonsOfMugloarRestClient() {
        restTemplate = new RestTemplate();
    }

    public GameInfoDTO startGame() throws CustomException {
        try {
            ResponseEntity<GameInfoDTO> response =
                    restTemplate.exchange(BASE_URL + START_GAME_ENDPOINT, HttpMethod.POST, null, GameInfoDTO.class);
            if (response.getStatusCode().is2xxSuccessful()) return response.getBody();
            else throw new CustomException(response.getStatusCode(), "Error starting a game.");
        } catch (HttpClientErrorException exception) {
            throw new CustomException(exception.getStatusCode(), exception.getMessage());
        }
    }

    public ReputationDTO runInvestigation(int gameId) throws CustomException {
        try {
            ResponseEntity<ReputationDTO> response =
                    restTemplate.exchange(BASE_URL + REPUTATION_ENDPOINT, HttpMethod.POST, null, ReputationDTO.class, gameId);
            if (response.getStatusCode().is2xxSuccessful()) return response.getBody();
            else throw new CustomException(response.getStatusCode(), "Error running an investigation using gameId = " + gameId);
        } catch (HttpClientErrorException exception) {
            throw new CustomException(exception.getStatusCode(), exception.getMessage());
        }
    }

    public static void main(String[] args) throws CustomException {
        DragonsOfMugloarRestClient restClient = new DragonsOfMugloarRestClient();
        System.out.println(restClient.startGame());
    }


}
