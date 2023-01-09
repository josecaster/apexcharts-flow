package sr.we.data.controller;

import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.client.RestTemplate;
import sr.we.shekelflowcore.entity.PosHeader;
import sr.we.shekelflowcore.entity.helper.PagingResult;
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

    public PagingResult<PosHeader> list(PosHeaderVO vo, String accessToken) {
        RestTemplate restTemplate = new RestTemplate();
        String fooResourceUrl
                = configProperties.getRest() + Routes.POS_HEADER_LIST;
        String body = new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, new LocalDateAdapter()).create().toJson(vo);
        HttpEntity<String> httpEntity = getAuthHttpEntity(body,accessToken);

        return encapsulate(() -> {
            String url = fooResourceUrl ;
//                    + "?businessId=" + businessId  ;
//            if(targetDate != null){
//                url +="&targetDate=" + targetDate;
//            }
            ResponseEntity<String> exchange = restTemplate.exchange(url, HttpMethod.POST, httpEntity, String.class);
            return transform(exchange,new TypeToken<PagingResult<PosHeader>>(){}.getType());
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