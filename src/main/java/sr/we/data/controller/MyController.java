package sr.we.data.controller;

import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpStatusCodeException;
import sr.we.ConfigProperties;
import sr.we.shekelflowcore.entity.helper.Build;
import sr.we.shekelflowcore.entity.helper.Error;
import sr.we.shekelflowcore.exception.ExceptionService;

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
