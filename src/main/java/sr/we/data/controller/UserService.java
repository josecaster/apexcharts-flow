package sr.we.data.controller;

import com.google.gson.GsonBuilder;
import org.springframework.http.*;
import org.springframework.http.client.support.BasicAuthorizationInterceptor;
import org.springframework.stereotype.Controller;
import org.springframework.web.client.RestTemplate;
import sr.we.shekelflowcore.entity.*;
import sr.we.shekelflowcore.entity.helper.adapter.InviteStaffVO;
import sr.we.shekelflowcore.entity.helper.adapter.UsersRolesBody;
import sr.we.shekelflowcore.entity.helper.vo.UserVO;
import sr.we.shekelflowcore.settings.Routes;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class UserService extends MyController {


    public ThisUser authenticate(String username, String password) {
        RestTemplate restTemplate = new RestTemplate();
        //noinspection deprecation
        restTemplate.getInterceptors().add(
                new BasicAuthorizationInterceptor(username, password));
        String fooResourceUrl
                = configProperties.getRest() + Routes.USER_ME;

        return encapsulate(() -> {
            ResponseEntity<ThisUser> exchange = restTemplate.exchange(fooResourceUrl, HttpMethod.GET, getHttpEntity(), ThisUser.class);
            return exchange.getBody();
        });
    }


    public void publishVerify(String token) {
        RestTemplate restTemplate = new RestTemplate();
        String fooResourceUrl
                = configProperties.getRest() + Routes.USER_PUBLISH_VERIFY;
        HttpEntity<String> httpEntity = getAuthHttpEntity(token);


        encapsulate(() -> {
            ResponseEntity<String> exchange = restTemplate.exchange(fooResourceUrl, HttpMethod.GET, httpEntity, String.class);
            return exchange.getBody();
        });

    }

    public ThisUser verify(String token, String verify) {
        ApplicationReference vo = new ApplicationReference();
        vo.setToken(verify);
        String body = new GsonBuilder().create().toJson(vo);
        RestTemplate restTemplate = new RestTemplate();
        String fooResourceUrl
                = configProperties.getRest()+ Routes.USER_VERIFY;

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
                = configProperties.getRest() + Routes.USER_CREATE;
        HttpEntity<String> httpEntity = getHttpEntity(body);


        return encapsulate(() -> {
            ResponseEntity<ThisUser> exchange = restTemplate.exchange(fooResourceUrl, HttpMethod.POST, httpEntity, ThisUser.class);
            return exchange.getBody();
        });

    }

    public ThisUser inviteStaff(String accessToken,InviteStaffVO vo) {
        String body = new GsonBuilder().create().toJson(vo);
        RestTemplate restTemplate = new RestTemplate();
        String fooResourceUrl
                = configProperties.getRest() + Routes.USER_INVITE_STAFF;
        HttpEntity<String> httpEntity = getAuthHttpEntity(body,accessToken);


        return encapsulate(() -> {
            ResponseEntity<ThisUser> exchange = restTemplate.exchange(fooResourceUrl, HttpMethod.POST, httpEntity, ThisUser.class);
            return exchange.getBody();
        });

    }

    public void publishReset(String emailAddress) {
        RestTemplate restTemplate = new RestTemplate();

        encapsulate(() -> {
            ResponseEntity<String> exchange = restTemplate.exchange(configProperties.getRest() + Routes.USER_PUBLISH_RESET + "?emailAddress=" + emailAddress, HttpMethod.GET, getHttpEntity(), String.class);
            return exchange.getBody();
        });
    }

    public void select(String accessToken, Long businessId, Role role) {

        RestTemplate restTemplate = new RestTemplate();
        String fooResourceUrl
                = configProperties.getRest()+ Routes.USER_SELECT+"?businessId="+businessId+"&roleId="+role.getId();
        encapsulate(() -> {
            HttpEntity<String> httpEntity = getAuthHttpEntity(accessToken);
            ResponseEntity<ThisUser> exchange = restTemplate.exchange(fooResourceUrl, HttpMethod.GET, httpEntity, ThisUser.class);
            return null;
        });
    }

    public List<Privilege> staffPerms(String accessToken) {

        RestTemplate restTemplate = new RestTemplate();
        String fooResourceUrl
                = configProperties.getRest()+ Routes.ACCESS_STAFF_PERMISSION;
        return encapsulate(() -> {
            HttpEntity<String> httpEntity = getAuthHttpEntity(accessToken);
            ResponseEntity<Privilege[]> exchange = restTemplate.exchange(fooResourceUrl, HttpMethod.GET, httpEntity, Privilege[].class);
            return Arrays.asList(exchange.getBody());
        });
    }

    public List<UsersRoles> getStaff(String accessToken, Long businessId) {
        RestTemplate restTemplate = new RestTemplate();
        String fooResourceUrl
                = configProperties.getRest()+ Routes.USER_STAFF+"?businessId="+businessId;
        return encapsulate(() -> {
            HttpEntity<String> httpEntity = getAuthHttpEntity(accessToken);
            ResponseEntity<UsersRolesBody[]> exchange = restTemplate.exchange(fooResourceUrl, HttpMethod.GET, httpEntity, UsersRolesBody[].class);
            return Arrays.asList(exchange.getBody()).stream().map(f -> {
                f.getUsersRoles().setThisUser(f.getThisUser());
              return f.getUsersRoles();
            }).collect(Collectors.toList());
        });
    }

    public static class Auth {

        public String username, password, temp, confirmPassword;

    }

    public void reset(Auth vo) {
        String body = new GsonBuilder().create().toJson(vo);
        RestTemplate restTemplate = new RestTemplate();
        String fooResourceUrl
                = configProperties.getRest() + Routes.USER_RESET;
        HttpEntity<String> httpEntity = getHttpEntity(body);

         encapsulate(() -> {
            ResponseEntity<String> exchange = restTemplate.exchange(fooResourceUrl, HttpMethod.POST, httpEntity, String.class);
            return exchange.getBody();
        });

    }
}