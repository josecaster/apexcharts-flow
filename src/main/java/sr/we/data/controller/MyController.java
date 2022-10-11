package sr.we.data.controller;

import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import sr.we.ConfigProperties;
import sr.we.shekelflowcore.entity.helper.Executable;
import sr.we.shekelflowcore.entity.helper.Error;
import sr.we.shekelflowcore.exception.ExceptionService;

import java.util.HashMap;

@Component
public abstract class MyController {

    @Autowired
    protected ConfigProperties configProperties;

    protected HttpEntity<String> getAuthHttpEntity(String accessToken) {
        HttpHeaders headers = getAuthHttpHeaders(accessToken);
        HttpEntity<String> httpEntity = new HttpEntity<>(headers);
        return httpEntity;
    }

    public HashMap<String, String> getMap(){
        return new HashMap();
    }

    protected HttpEntity<String> getAuthHttpEntity(String accessToken, HashMap<String, String> map) {
        HttpHeaders headers = getAuthHttpHeaders(accessToken);
        if(map != null && !map.isEmpty()){
            map.entrySet().stream().forEach(f -> {
                headers.add(f.getKey(), f.getValue());
            });
        }
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

    protected HttpEntity<String> getHttpEntity(String body) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> httpEntity = new HttpEntity<>(body,headers);
        return httpEntity;
    }

    public <T> T encapsulate(Executable<T> executable) {
        try {
            return executable.build();
        } catch (HttpStatusCodeException e) {
            String responseBodyAsString = e.getResponseBodyAsString();
            Error error = new Gson().fromJson(responseBodyAsString, Error.class);
            if(error != null && error.getCode() != null && !error.getCode().isEmpty()){
                throw ExceptionService.initByCode(error);
            }
            throw e;
        }
    }

}
