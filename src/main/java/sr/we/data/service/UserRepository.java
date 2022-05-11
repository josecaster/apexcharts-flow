package sr.we.data.service;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.support.BasicAuthorizationInterceptor;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import sr.we.shekelflowcore.entity.ThisUser;

import java.util.ArrayList;
import java.util.Arrays;

@Controller
public class UserRepository {

    public ThisUser findByUsername(String username, String password){
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getInterceptors().add(
                new BasicAuthorizationInterceptor(username, password));
        String fooResourceUrl
                = "http://localhost:9090/user/rest/me";
        ThisUser response
                = restTemplate.getForObject(fooResourceUrl , ThisUser.class);
//        Assertions.assertEquals(response.getStatusCode(), HttpStatus.OK);
        if(response != null && response.getRoles() != null){
        }
        return response;
    }
}