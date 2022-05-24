package sr.we.data.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.support.BasicAuthorizationInterceptor;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import sr.we.ConfigProperties;
import sr.we.shekelflowcore.entity.ThisUser;
import sr.we.shekelflowcore.settings.Services;

@Controller
public class UserService extends MyController{


    public ThisUser authenticate(String username, String password){
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getInterceptors().add(
                new BasicAuthorizationInterceptor(username, password));
        String fooResourceUrl
                = configProperties.getRest()+ Services.USER_ME;

        ResponseEntity<ThisUser> exchange = restTemplate.exchange(fooResourceUrl, HttpMethod.GET, getHttpEntity(), ThisUser.class);
        return exchange.getBody();
    }



    public ThisUser verify(String username, String password, String token){
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getInterceptors().add(
                new BasicAuthorizationInterceptor(username, password));
        String fooResourceUrl
                = configProperties.getRest()+ Services.USER_VERIFY;
        MultiValueMap<String, String> map= new LinkedMultiValueMap<String, String>();
        map.add("token", token);
        ThisUser response
                = restTemplate.getForObject(fooResourceUrl , ThisUser.class, map);
        return response;
    }
}