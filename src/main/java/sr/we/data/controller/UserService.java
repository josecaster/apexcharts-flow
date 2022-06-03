package sr.we.data.controller;

import com.google.gson.GsonBuilder;
import org.springframework.http.*;
import org.springframework.http.client.support.BasicAuthorizationInterceptor;
import org.springframework.stereotype.Controller;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import sr.we.shekelflowcore.entity.ApplicationUserVerification;
import sr.we.shekelflowcore.entity.Business;
import sr.we.shekelflowcore.entity.Person;
import sr.we.shekelflowcore.entity.ThisUser;
import sr.we.shekelflowcore.entity.helper.vo.BusinessVO;
import sr.we.shekelflowcore.entity.helper.vo.UserVO;
import sr.we.shekelflowcore.settings.Services;

@Controller
public class UserService extends MyController {


    public ThisUser authenticate(String username, String password) {
        RestTemplate restTemplate = new RestTemplate();
        //noinspection deprecation
        restTemplate.getInterceptors().add(
                new BasicAuthorizationInterceptor(username, password));
        String fooResourceUrl
                = configProperties.getRest() + Services.USER_ME;

        return encapsulate(() -> {
            ResponseEntity<ThisUser> exchange = restTemplate.exchange(fooResourceUrl, HttpMethod.GET, getHttpEntity(), ThisUser.class);
            return exchange.getBody();
        });
    }


    public void publishVerify(String token) {
        RestTemplate restTemplate = new RestTemplate();
        String fooResourceUrl
                = configProperties.getRest() + Services.USER_PUBLISH_VERIFY;
        HttpEntity<String> httpEntity = getAuthHttpEntity(token);


        encapsulate(() -> {
            ResponseEntity<String> exchange = restTemplate.exchange(fooResourceUrl, HttpMethod.GET, httpEntity, String.class);
            return exchange.getBody();
        });

    }

    public ThisUser verify(String token, String verify) {
        ApplicationUserVerification vo = new ApplicationUserVerification();
        vo.setToken(verify);
        String body = new GsonBuilder().create().toJson(vo);
        RestTemplate restTemplate = new RestTemplate();
        String fooResourceUrl
                = configProperties.getRest()+ Services.USER_VERIFY;

        return encapsulate(() -> {
            HttpEntity<String> httpEntity = getAuthHttpEntity(body,token);
            ResponseEntity<ThisUser> exchange = restTemplate.exchange(fooResourceUrl, HttpMethod.POST, httpEntity, ThisUser.class);
            return exchange.getBody();
        });

    }

    protected HttpEntity<String> getAuthHttpEntitya(String verify, String accessToken) {
        HttpHeaders headers = getAuthHttpHeaders(accessToken);
        headers.set("token", verify);
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        return new HttpEntity<>(headers);
    }

    public ThisUser create(UserVO vo) {
        String body = new GsonBuilder().create().toJson(vo);
        RestTemplate restTemplate = new RestTemplate();
        String fooResourceUrl
                = configProperties.getRest() + Services.USER_CREATE;
        HttpEntity<String> httpEntity = getHttpEntity(body);


        return encapsulate(() -> {
            ResponseEntity<ThisUser> exchange = restTemplate.exchange(fooResourceUrl, HttpMethod.POST, httpEntity, ThisUser.class);
            return exchange.getBody();
        });

    }

    public void publishReset(String emailAddress) {
        RestTemplate restTemplate = new RestTemplate();

        encapsulate(() -> {
            ResponseEntity<String> exchange = restTemplate.exchange(configProperties.getRest() + Services.USER_PUBLISH_RESET + "?emailAddress=" + emailAddress, HttpMethod.GET, getHttpEntity(), String.class);
            return exchange.getBody();
        });
    }

    public static class Auth {

        public String username, password, temp, confirmPassword;

    }

    public void reset(Auth vo) {
        String body = new GsonBuilder().create().toJson(vo);
        RestTemplate restTemplate = new RestTemplate();
        String fooResourceUrl
                = configProperties.getRest() + Services.USER_RESET;
        HttpEntity<String> httpEntity = getHttpEntity(body);

         encapsulate(() -> {
            ResponseEntity<String> exchange = restTemplate.exchange(fooResourceUrl, HttpMethod.POST, httpEntity, String.class);
            return exchange.getBody();
        });

    }
}