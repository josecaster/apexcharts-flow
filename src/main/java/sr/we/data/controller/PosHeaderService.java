package sr.we.data.controller;

import com.google.gson.GsonBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.client.RestTemplate;
import sr.we.shekelflowcore.entity.PosHeader;
import sr.we.shekelflowcore.entity.helper.adapter.LocalDateAdapter;
import sr.we.shekelflowcore.entity.helper.vo.PosHeaderVO;
import sr.we.shekelflowcore.settings.Routes;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@Controller
public class PosHeaderService extends MyController {

    public PosHeader get(Long id, String accessToken) {
        RestTemplate restTemplate = new RestTemplate();

        return encapsulate(() -> {
            ResponseEntity<PosHeader> exchange = restTemplate.exchange(configProperties.getRest() + Routes.POS_HEADER_GET + "?id=" + id, HttpMethod.GET, getAuthHttpEntity(accessToken), PosHeader.class);
            PosHeader body = exchange.getBody();
            return body;
        });
    }

    public List<PosHeader> list(Long businessId, LocalDate targetDate, String accessToken) {
        RestTemplate restTemplate = new RestTemplate();
        String fooResourceUrl
                = configProperties.getRest() + Routes.POS_HEADER_LIST;
        HttpEntity<String> httpEntity = getAuthHttpEntity(accessToken);

        return encapsulate(() -> {
            ResponseEntity<PosHeader[]> exchange = restTemplate.exchange(fooResourceUrl + "?businessId=" + businessId + "&targetDate=" + targetDate, HttpMethod.GET, httpEntity, PosHeader[].class);
            return Arrays.asList(exchange.getBody());
        });
    }

    public PosHeader create(String accessToken, PosHeaderVO vo) {
        String body = new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, new LocalDateAdapter()).create().toJson(vo);
        RestTemplate restTemplate = new RestTemplate();
        String fooResourceUrl
                = configProperties.getRest() + Routes.POS_HEADER_CREATE;
        HttpEntity<String> httpEntity = getAuthHttpEntity(body, accessToken);

        return encapsulate(() -> {
            ResponseEntity<PosHeader> exchange = restTemplate.exchange(fooResourceUrl, HttpMethod.POST, httpEntity, PosHeader.class);
            return exchange.getBody();
        });
    }

    public PosHeader edit(String accessToken, PosHeaderVO vo) {
        String body = new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, new LocalDateAdapter()).create().toJson(vo);
        RestTemplate restTemplate = new RestTemplate();
        String fooResourceUrl
                = configProperties.getRest() + Routes.POS_HEADER_EDIT;
        HttpEntity<String> httpEntity = getAuthHttpEntity(body, accessToken);

        return encapsulate(() -> {
            ResponseEntity<PosHeader> exchange = restTemplate.exchange(fooResourceUrl, HttpMethod.POST, httpEntity, PosHeader.class);
            return exchange.getBody();
        });
    }


}