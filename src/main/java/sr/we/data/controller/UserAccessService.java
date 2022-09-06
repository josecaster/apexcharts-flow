package sr.we.data.controller;

import com.google.gson.GsonBuilder;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.client.RestTemplate;
import sr.we.shekelflowcore.entity.helper.adapter.LocalDateAdapter;
import sr.we.shekelflowcore.security.Access;
import sr.we.shekelflowcore.security.PrivilegeModeAbstract;
import sr.we.shekelflowcore.security.Privileges;
import sr.we.shekelflowcore.settings.Routes;

import java.time.LocalDate;

@Controller
public class UserAccessService extends MyController {


    public boolean hasAccess(String accessToken, PrivilegeModeAbstract privilegeMode, Privileges ... privilege){
        Access access = privilegeMode.getAccess();
        access.setPermissions(privilege);
        String body = new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, new LocalDateAdapter()).create().toJson(access);
        RestTemplate restTemplate = new RestTemplate();
        String fooResourceUrl
                = configProperties.getRest() + Routes.ACCESS_PERMISSION;
        HttpEntity<String> httpEntity = getAuthHttpEntity(body, accessToken);

        return encapsulate(() -> {
            ResponseEntity<Access> exchange = restTemplate.exchange(fooResourceUrl, HttpMethod.POST, httpEntity, Access.class);
            return exchange.getBody().isHasAccess();
        });
    }
}