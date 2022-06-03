package sr.we.data.controller;

import com.google.gson.GsonBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.client.RestTemplate;
import sr.we.shekelflowcore.entity.Person;
import sr.we.shekelflowcore.entity.PersonForm;
import sr.we.shekelflowcore.entity.helper.vo.PersonFormVO;
import sr.we.shekelflowcore.entity.helper.vo.PersonVO;
import sr.we.shekelflowcore.settings.Services;

@Controller
public class PersonFormService extends MyController{

    public PersonForm create(String accessToken, PersonFormVO vo){
        String body = new GsonBuilder().create().toJson(vo);
        RestTemplate restTemplate = new RestTemplate();
        String fooResourceUrl
                = configProperties.getRest()+ Services.PERSON_FORM_CREATE;
        HttpEntity<String> httpEntity = getAuthHttpEntity(body,accessToken);

        return encapsulate(() -> {
            ResponseEntity<PersonForm> exchange = restTemplate.exchange(fooResourceUrl, HttpMethod.POST, httpEntity, PersonForm.class);
            return exchange.getBody();
        });
    }

    public PersonForm edit(String accessToken, PersonFormVO vo){
        String body = new GsonBuilder().create().toJson(vo);
        RestTemplate restTemplate = new RestTemplate();
        String fooResourceUrl
                = configProperties.getRest()+ Services.PERSON_FORM_EDIT;
        HttpEntity<String> httpEntity = getAuthHttpEntity(body,accessToken);

        return encapsulate(() -> {
            ResponseEntity<PersonForm> exchange = restTemplate.exchange(fooResourceUrl, HttpMethod.POST, httpEntity, PersonForm.class);
            return exchange.getBody();
        });
    }

    public PersonForm me(String accessToken){
        RestTemplate restTemplate = new RestTemplate();

        return encapsulate(() -> {
            ResponseEntity<PersonForm> exchange = restTemplate.exchange(configProperties.getRest() + Services.PERSON_FORM_ME, HttpMethod.GET, getAuthHttpEntity(accessToken), PersonForm.class);
            return exchange.getBody();
        });
    }

}