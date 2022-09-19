package sr.we.data.controller;

import com.google.gson.GsonBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.client.RestTemplate;
import sr.we.shekelflowcore.entity.Account;
import sr.we.shekelflowcore.entity.helper.vo.AccountVO;
import sr.we.shekelflowcore.settings.Routes;

import java.util.Arrays;
import java.util.List;

@Controller
public class AccountService extends MyController {

    public List<Account> list(String accessToken, AccountVO vo) {
        String body = new GsonBuilder().create().toJson(vo);
        RestTemplate restTemplate = new RestTemplate();
        String fooResourceUrl = configProperties.getRest() + Routes.ACCOUNT_LIST ;
//        HttpEntity<String> httpEntity = getAuthHttpEntity(body,accessToken);

        return encapsulate(() -> {
            HttpEntity<String> httpEntity = getAuthHttpEntity(body,accessToken);
            ResponseEntity<Account[]> exchange = restTemplate.exchange(fooResourceUrl, HttpMethod.POST, httpEntity, Account[].class);
//            ResponseEntity<Account[]> exchange = restTemplate.exchange(fooResourceUrl, HttpMethod.GET, httpEntity, Account[].class);
            return Arrays.asList(exchange.getBody());
        });
    }

    public Account create(String accessToken, AccountVO vo){


        return encapsulate(() -> {
            String body = new GsonBuilder().create().toJson(vo);
            RestTemplate restTemplate = new RestTemplate();
            String fooResourceUrl
                    = configProperties.getRest()+ Routes.ACCOUNT_CREATE;
            HttpEntity<String> httpEntity = getAuthHttpEntity(body,accessToken);
            ResponseEntity<Account> exchange = restTemplate.exchange(fooResourceUrl, HttpMethod.POST, httpEntity, Account.class);
            return exchange.getBody();
        });
    }
//
//    public Business edit(String accessToken, BusinessVO vo){
//        String body = new GsonBuilder().create().toJson(vo);
//        RestTemplate restTemplate = new RestTemplate();
//        String fooResourceUrl
//                = configProperties.getRest()+ Services.BUSINESS_EDIT;
//        HttpEntity<String> httpEntity = getAuthHttpEntity(body,accessToken);
//
//        return encapsulate(() -> {
//            ResponseEntity<Business> exchange = restTemplate.exchange(fooResourceUrl, HttpMethod.POST, httpEntity, Business.class);
//            return exchange.getBody();
//        });
//    }


}