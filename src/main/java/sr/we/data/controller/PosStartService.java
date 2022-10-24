package sr.we.data.controller;

import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.client.RestTemplate;
import sr.we.shekelflowcore.entity.PosStart;
import sr.we.shekelflowcore.entity.helper.PagingResult;
import sr.we.shekelflowcore.entity.helper.adapter.LocalDateAdapter;
import sr.we.shekelflowcore.entity.helper.adapter.LocalDateTimeAdapter;
import sr.we.shekelflowcore.entity.helper.adapter.PosStartBody;
import sr.we.shekelflowcore.entity.helper.vo.PosStartVO;
import sr.we.shekelflowcore.settings.Routes;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Controller
public class PosStartService extends MyController {

    public PosStart get(Long id, String accessToken) {
        RestTemplate restTemplate = new RestTemplate();

        return encapsulate(() -> {
            ResponseEntity<PosStart> exchange = restTemplate.exchange(configProperties.getRest() + Routes.POS_START_GET + "?id=" + id, HttpMethod.GET, getAuthHttpEntity(accessToken), PosStart.class);
            PosStart body = exchange.getBody();
            return body;
        });
    }

    public PagingResult<PosStart> list(Long businessId, LocalDate targetDate, String accessToken) {
        String body = new GsonBuilder().registerTypeAdapter(LocalDate.class, new LocalDateAdapter()).create().toJson(new PosStartBody(businessId, targetDate));
        RestTemplate restTemplate = new RestTemplate();
        String fooResourceUrl = configProperties.getRest() + Routes.POS_START_LIST;
        HttpEntity<String> httpEntity = getAuthHttpEntity(body, accessToken);

        return encapsulate(() -> {
            ResponseEntity<String> exchange = restTemplate.exchange(fooResourceUrl, HttpMethod.POST, httpEntity, String.class);
            return transform(exchange,new TypeToken<PagingResult<PosStart>>(){}.getType());
        });
    }

    public PosStart create(String accessToken, PosStartVO vo) {
        String body = new GsonBuilder().registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter()).create().toJson(vo);
        RestTemplate restTemplate = new RestTemplate();
        String fooResourceUrl = configProperties.getRest() + Routes.POS_START_CREATE;
        HttpEntity<String> httpEntity = getAuthHttpEntity(body, accessToken);

        return encapsulate(() -> {
            ResponseEntity<PosStart> exchange = restTemplate.exchange(fooResourceUrl, HttpMethod.POST, httpEntity, PosStart.class);
            return exchange.getBody();
        });
    }

    public PosStart edit(String accessToken, PosStartVO vo) {
        String body = new GsonBuilder().registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter()).create().toJson(vo);
        RestTemplate restTemplate = new RestTemplate();
        String fooResourceUrl = configProperties.getRest() + Routes.POS_START_EDIT;
        HttpEntity<String> httpEntity = getAuthHttpEntity(body, accessToken);

        return encapsulate(() -> {
            ResponseEntity<PosStart> exchange = restTemplate.exchange(fooResourceUrl, HttpMethod.POST, httpEntity, PosStart.class);
            return exchange.getBody();
        });
    }


}