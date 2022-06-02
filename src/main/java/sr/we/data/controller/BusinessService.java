package sr.we.data.controller;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.GsonBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.client.RestTemplate;
import sr.we.shekelflowcore.entity.Business;
import sr.we.shekelflowcore.entity.BusinessOrganisationType;
import sr.we.shekelflowcore.entity.helper.vo.BusinessVO;
import sr.we.shekelflowcore.settings.Services;

import java.util.Arrays;
import java.util.List;

@Controller
public class BusinessService extends MyController{

    public Business get(Long id, String accessToken){
        RestTemplate restTemplate = new RestTemplate();

        return encapsulate(() -> {
            ResponseEntity<Business> exchange = restTemplate.exchange(configProperties.getRest() + Services.BUSINESS_GET + "?id=" + id, HttpMethod.GET, getAuthHttpEntity(accessToken), Business.class);
            Business body = exchange.getBody();
            return body;
        });
    }

    public List<Business> list(String accessToken){
        RestTemplate restTemplate = new RestTemplate();
        String fooResourceUrl
                = configProperties.getRest()+ Services.BUSINESS_LIST;
        HttpEntity<String> httpEntity = getAuthHttpEntity(accessToken);

        return encapsulate(() -> {
            ResponseEntity<Business[]> exchange = restTemplate.exchange(fooResourceUrl, HttpMethod.GET, httpEntity, Business[].class);
            return Arrays.asList(exchange.getBody());
        });
    }

    public Business create(String accessToken, BusinessVO vo){
        String body = new GsonBuilder().create().toJson(vo);
        RestTemplate restTemplate = new RestTemplate();
        String fooResourceUrl
                = configProperties.getRest()+ Services.BUSINESS_CREATE;
        HttpEntity<String> httpEntity = getAuthHttpEntity(body,accessToken);

        return encapsulate(() -> {
            ResponseEntity<Business> exchange = restTemplate.exchange(fooResourceUrl, HttpMethod.POST, httpEntity, Business.class);
            return exchange.getBody();
        });
    }

    public Business edit(String accessToken, BusinessVO vo){
        String body = new GsonBuilder().create().toJson(vo);
        RestTemplate restTemplate = new RestTemplate();
        String fooResourceUrl
                = configProperties.getRest()+ Services.BUSINESS_EDIT;
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
                = configProperties.getRest()+ Services.BUSINESS_SELECT;

        encapsulate(() -> {
            HttpEntity<String> httpEntity = getAuthHttpEntity(body,accessToken);
            ResponseEntity<Business> exchange = restTemplate.exchange(fooResourceUrl, HttpMethod.POST, httpEntity, Business.class);
            return null;
        });
    }

    public void unselectAll(String accessToken) {
        RestTemplate restTemplate = new RestTemplate();
        String fooResourceUrl
                = configProperties.getRest()+ Services.BUSINESS_UNSELECT_ALL;

        encapsulate(() -> {
            HttpEntity<String> httpEntity = getAuthHttpEntity(accessToken);
            ResponseEntity<Business> exchange = restTemplate.exchange(fooResourceUrl, HttpMethod.POST, httpEntity, Business.class);
            return null;
        });
    }
}