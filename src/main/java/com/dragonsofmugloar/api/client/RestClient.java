package com.dragonsofmugloar.api.client;

import com.dragonsofmugloar.api.dto.GameInfoDTO;
import com.dragonsofmugloar.api.exception.CustomException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.client.*;

public class RestClient {

    protected RestTemplate restTemplate;

    public RestClient() {
        restTemplate = new RestTemplate();
    }

    public <T> T post(String url, Class<T> responseType) throws CustomException {
        try {
            ResponseEntity<T> response =
                    restTemplate.exchange(url, HttpMethod.POST, null, responseType);
            if (response.getStatusCode().is2xxSuccessful()) return response.getBody();
            else throw new CustomException(response.getStatusCode(), "Error starting a game.");
        } catch (HttpClientErrorException exception) {
            throw new CustomException(exception.getStatusCode(), exception.getMessage());
        }
    }
}
