package sr.we.data.controller;

import com.google.gson.GsonBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.client.RestTemplate;
import sr.we.shekelflowcore.entity.PosHeaderDetail;
import sr.we.shekelflowcore.entity.helper.vo.PosHeaderDetailVO;
import sr.we.shekelflowcore.settings.Routes;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Controller
public class PosHeaderDetailService extends MyController {

    public PosHeaderDetail get(Long id, String accessToken) {
        RestTemplate restTemplate = new RestTemplate();

        return encapsulate(() -> {
            ResponseEntity<PosHeaderDetail> exchange = restTemplate.exchange(configProperties.getRest() + Routes.POS_HEADER_DETAIL_GET + "?id=" + id, HttpMethod.GET, getAuthHttpEntity(accessToken), PosHeaderDetail.class);
            PosHeaderDetail body = exchange.getBody();
            return body;
        });
    }

    public List<PosHeaderDetail> list(Long businessId, LocalDate targetDate, String accessToken) {
        RestTemplate restTemplate = new RestTemplate();
        String fooResourceUrl
                = configProperties.getRest() + Routes.POS_HEADER_DETAIL_LIST;
        HttpEntity<String> httpEntity = getAuthHttpEntity(accessToken);

        return encapsulate(() -> {
            ResponseEntity<PosHeaderDetail[]> exchange = restTemplate.exchange(fooResourceUrl + "?businessId=" + businessId + "&targetDate=" + targetDate, HttpMethod.GET, httpEntity, PosHeaderDetail[].class);
            return Arrays.asList(Objects.requireNonNull(exchange.getBody()));
        });
    }

    public PosHeaderDetail create(String accessToken, PosHeaderDetailVO vo) {
        String body = new GsonBuilder().create().toJson(vo);
        RestTemplate restTemplate = new RestTemplate();
        String fooResourceUrl
                = configProperties.getRest() + Routes.POS_HEADER_DETAIL_CREATE;
        HttpEntity<String> httpEntity = getAuthHttpEntity(body, accessToken);

        return encapsulate(() -> {
            ResponseEntity<PosHeaderDetail> exchange = restTemplate.exchange(fooResourceUrl, HttpMethod.POST, httpEntity, PosHeaderDetail.class);
            return exchange.getBody();
        });
    }

    public PosHeaderDetail edit(String accessToken, PosHeaderDetailVO vo) {
        String body = new GsonBuilder().create().toJson(vo);
        RestTemplate restTemplate = new RestTemplate();
        String fooResourceUrl
                = configProperties.getRest() + Routes.POS_HEADER_DETAIL_EDIT;
        HttpEntity<String> httpEntity = getAuthHttpEntity(body, accessToken);

        return encapsulate(() -> {
            ResponseEntity<PosHeaderDetail> exchange = restTemplate.exchange(fooResourceUrl, HttpMethod.POST, httpEntity, PosHeaderDetail.class);
            return exchange.getBody();
        });
    }


}