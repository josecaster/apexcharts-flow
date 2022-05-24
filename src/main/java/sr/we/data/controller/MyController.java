package sr.we.data.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import sr.we.ConfigProperties;

@Component
public abstract class MyController {

    @Autowired
    protected ConfigProperties configProperties;

    protected HttpEntity<String> getAuthHttpEntity(String accessToken) {
        HttpHeaders headers = getAuthHttpHeaders(accessToken);
        HttpEntity<String> httpEntity = new HttpEntity<>(headers);
        return httpEntity;
    }

    protected HttpEntity<String> getAuthHttpEntity(String body, String accessToken) {
        HttpHeaders headers = getAuthHttpHeaders(accessToken);
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> httpEntity = new HttpEntity<>(body, headers);
        return httpEntity;
    }

    protected HttpHeaders getAuthHttpHeaders(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer "+ accessToken);
        return headers;
    }
    protected HttpEntity<String> getHttpEntity() {
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<String> httpEntity = new HttpEntity<>(headers);
        return httpEntity;
    }

}
