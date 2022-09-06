package sr.we.data.controller;

import com.google.gson.GsonBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.client.RestTemplate;
import sr.we.shekelflowcore.entity.Business;
import sr.we.shekelflowcore.entity.helper.vo.BusinessVO;
import sr.we.shekelflowcore.settings.Routes;

import java.util.Arrays;
import java.util.List;

@Controller
public class BusinessService extends MyController{

    public Business get(Long id, String accessToken){
        RestTemplate restTemplate = new RestTemplate();

        return encapsulate(() -> {
            String url = configProperties.getRest() + Routes.BUSINESS_GET ;
            if(id != null){
                url+= "?id=" + id;
            }
            ResponseEntity<Business> exchange = restTemplate.exchange(url, HttpMethod.GET, getAuthHttpEntity(accessToken), Business.class);
            Business body = exchange.getBody();
            return body;
        });
    }

    public List<Business> list(String accessToken){
        RestTemplate restTemplate = new RestTemplate();
        String fooResourceUrl
                = configProperties.getRest()+ Routes.BUSINESS_LIST;
        HttpEntity<String> httpEntity = getAuthHttpEntity(accessToken);

        return encapsulate(() -> {
            ResponseEntity<Business[]> exchange = restTemplate.exchange(fooResourceUrl, HttpMethod.GET, httpEntity, Business[].class);
            return Arrays.asList(exchange.getBody());
        });
    }

    public Business create(String accessToken, BusinessVO vo){


        return encapsulate(() -> {
            String body = new GsonBuilder().create().toJson(vo);
            RestTemplate restTemplate = new RestTemplate();
            String fooResourceUrl
                    = configProperties.getRest()+ Routes.BUSINESS_CREATE;
            HttpEntity<String> httpEntity = getAuthHttpEntity(body,accessToken);
            ResponseEntity<Business> exchange = restTemplate.exchange(fooResourceUrl, HttpMethod.POST, httpEntity, Business.class);
            return exchange.getBody();
        });
    }

    public Business edit(String accessToken, BusinessVO vo){
        String body = new GsonBuilder().create().toJson(vo);
        RestTemplate restTemplate = new RestTemplate();
        String fooResourceUrl
                = configProperties.getRest()+ Routes.BUSINESS_EDIT;
        HttpEntity<String> httpEntity = getAuthHttpEntity(body,accessToken);

        return encapsulate(() -> {
            ResponseEntity<Business> exchange = restTemplate.exchange(fooResourceUrl, HttpMethod.POST, httpEntity, Business.class);
            return exchange.getBody();
        });
    }


    public void select(Long id, String accessToken) {
        BusinessVO vo = new BusinessVO();
        vo.setId(id);
        String body = new GsonBuilder().create().toJson(vo);
        RestTemplate restTemplate = new RestTemplate();
        String fooResourceUrl
                = configProperties.getRest()+ Routes.BUSINESS_SELECT;

        encapsulate(() -> {
            HttpEntity<String> httpEntity = getAuthHttpEntity(body,accessToken);
            ResponseEntity<Business> exchange = restTemplate.exchange(fooResourceUrl, HttpMethod.POST, httpEntity, Business.class);
            return null;
        });
    }

    public void unselectAll(String accessToken) {
        RestTemplate restTemplate = new RestTemplate();
        String fooResourceUrl
                = configProperties.getRest()+ Routes.BUSINESS_UNSELECT_ALL;

        encapsulate(() -> {
            HttpEntity<String> httpEntity = getAuthHttpEntity(accessToken);
            ResponseEntity<Business> exchange = restTemplate.exchange(fooResourceUrl, HttpMethod.POST, httpEntity, Business.class);
            return null;
        });
    }

    public List<Business> listFromUserRoles(String accessToken) {
        RestTemplate restTemplate = new RestTemplate();
        String fooResourceUrl
                = configProperties.getRest()+ Routes.BUSINESS_MY_LIST;
        HttpEntity<String> httpEntity = getAuthHttpEntity(accessToken);

        return encapsulate(() -> {
            ResponseEntity<Business[]> exchange = restTemplate.exchange(fooResourceUrl, HttpMethod.GET, httpEntity, Business[].class);
            return Arrays.asList(exchange.getBody());
        });
    }
}