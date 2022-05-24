package sr.we.data.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.client.RestTemplate;
import sr.we.ConfigProperties;
import sr.we.shekelflowcore.entity.*;
import sr.we.shekelflowcore.entity.helper.vo.BusinessVO;
import sr.we.shekelflowcore.settings.Services;

import java.util.Arrays;
import java.util.List;

@Controller
public class PojoService extends MyController{



    public List<Country> listCountry(String accessToken){
        RestTemplate restTemplate = new RestTemplate();
        String fooResourceUrl
                = configProperties.getRest()+ Services.COUNTRY_LIST;
        HttpEntity<String> httpEntity = getAuthHttpEntity(accessToken);
        ResponseEntity<Country[]> exchange = restTemplate.exchange(fooResourceUrl, HttpMethod.GET, httpEntity, Country[].class);
        return Arrays.asList(exchange.getBody());
    }

    public List<Currency> listCurrency(String accessToken){
        RestTemplate restTemplate = new RestTemplate();
        String fooResourceUrl
                = configProperties.getRest()+ Services.CURRENCY_LIST;
        HttpEntity<String> httpEntity = getAuthHttpEntity(accessToken);
        ResponseEntity<Currency[]> exchange = restTemplate.exchange(fooResourceUrl, HttpMethod.GET, httpEntity, Currency[].class);
        return Arrays.asList(exchange.getBody());
    }

    public List<BusinessType> listBusinessType(String accessToken){
        RestTemplate restTemplate = new RestTemplate();
        String fooResourceUrl
                = configProperties.getRest()+ Services.BUSINESS_TYPE_LIST;
        HttpEntity<String> httpEntity = getAuthHttpEntity(accessToken);
        ResponseEntity<BusinessType[]> exchange = restTemplate.exchange(fooResourceUrl, HttpMethod.GET, httpEntity, BusinessType[].class);
        return Arrays.asList(exchange.getBody());
    }

    public List<BusinessOrganisationType> listBusinessOrganisationType(String accessToken){
        RestTemplate restTemplate = new RestTemplate();
        String fooResourceUrl
                = configProperties.getRest()+ Services.BUSINESS_ORGANISATION_TYPE_LIST;
        HttpEntity<String> httpEntity = getAuthHttpEntity(accessToken);
        ResponseEntity<BusinessOrganisationType[]> exchange = restTemplate.exchange(fooResourceUrl, HttpMethod.GET, httpEntity, BusinessOrganisationType[].class);
        return Arrays.asList(exchange.getBody());
    }






}