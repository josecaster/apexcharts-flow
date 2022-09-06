package sr.we.data.controller;

import com.google.gson.GsonBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.client.RestTemplate;
import sr.we.shekelflowcore.entity.Person;
import sr.we.shekelflowcore.entity.helper.adapter.LocalDateAdapter;
import sr.we.shekelflowcore.entity.helper.vo.PersonVO;
import sr.we.shekelflowcore.settings.Routes;

import java.time.LocalDate;

@Controller
public class PersonService extends MyController {

    public Person create(String accessToken, PersonVO vo) {
        String body = new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, new LocalDateAdapter()).create().toJson(vo);
        RestTemplate restTemplate = new RestTemplate();
        String fooResourceUrl
                = configProperties.getRest() + Routes.PERSON_CREATE;
        HttpEntity<String> httpEntity = getAuthHttpEntity(body, accessToken);

        return encapsulate(() -> {
            ResponseEntity<Person> exchange = restTemplate.exchange(fooResourceUrl, HttpMethod.POST, httpEntity, Person.class);
            return exchange.getBody();
        });
    }

    public Person edit(String accessToken, PersonVO vo) {
        String body = new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, new LocalDateAdapter()).create().toJson(vo);
        RestTemplate restTemplate = new RestTemplate();
        String fooResourceUrl
                = configProperties.getRest() + Routes.PERSON_EDIT;
        HttpEntity<String> httpEntity = getAuthHttpEntity(body, accessToken);

        return encapsulate(() -> {
            ResponseEntity<Person> exchange = restTemplate.exchange(fooResourceUrl, HttpMethod.POST, httpEntity, Person.class);
            return exchange.getBody();
        });
    }

    public Person me(String accessToken){
        RestTemplate restTemplate = new RestTemplate();

        return encapsulate(() -> {
            ResponseEntity<Person> exchange = restTemplate.exchange(configProperties.getRest() + Routes.PERSON_ME, HttpMethod.GET, getAuthHttpEntity(accessToken), Person.class);
            return exchange.getBody();
        });
    }

}