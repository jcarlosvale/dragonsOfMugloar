package com.dragonsofmugloar.api.client;

import com.dragonsofmugloar.api.exception.CustomException;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

class RestClient {

    protected RestTemplate restTemplate;

    public RestClient() {
        restTemplate = new RestTemplate();
    }

    public <T> T post(String url, Class<T> responseType, Object ... parameters) throws CustomException {
        return exchange(url, HttpMethod.POST, responseType, parameters);
    }

    public <T> T get(String url, Class<T> responseType, Object ... parameters) throws CustomException {
        return exchange(url, HttpMethod.GET, responseType, parameters);
    }

    private <T> T exchange(String url, HttpMethod httpMethod, Class<T> responseType, Object ... parameters) throws CustomException {
        try {
            ResponseEntity<T> response =
                    restTemplate.exchange(url, httpMethod, null, responseType, parameters);
            if (response.getStatusCode().is2xxSuccessful()) return response.getBody();
            else throw new CustomException(response.getStatusCode());
        } catch (HttpClientErrorException exception) {
            throw new CustomException(exception.getStatusCode(), exception.getMessage());
        }
    }
}
