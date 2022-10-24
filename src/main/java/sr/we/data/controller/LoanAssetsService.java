package sr.we.data.controller;

import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.client.RestTemplate;
import sr.we.shekelflowcore.entity.Business;
import sr.we.shekelflowcore.entity.LoanAssets;
import sr.we.shekelflowcore.entity.helper.PagingResult;
import sr.we.shekelflowcore.entity.helper.adapter.ByteArrayAdapter;
import sr.we.shekelflowcore.entity.helper.adapter.LocalDateAdapter;
import sr.we.shekelflowcore.entity.helper.adapter.LocalDateTimeAdapter;
import sr.we.shekelflowcore.entity.helper.vo.LoanAssetsVO;
import sr.we.shekelflowcore.settings.Routes;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Controller
public class LoanAssetsService extends MyController {

    public LoanAssets get(Long id, String accessToken) {
        RestTemplate restTemplate = new RestTemplate();

        return encapsulate(() -> {
            ResponseEntity<LoanAssets> exchange = restTemplate.exchange(configProperties.getRest() + Routes.LOAN_ASSETS_GET + "?id=" + id, HttpMethod.GET, getAuthHttpEntity(accessToken), LoanAssets.class);
            LoanAssets body = exchange.getBody();
            return body;
        });
    }

    public PagingResult<LoanAssets> list(String accessToken, Long loanId) {


        return encapsulate(() -> {
            RestTemplate restTemplate = new RestTemplate();
            String fooResourceUrl
                    = configProperties.getRest() + Routes.LOAN_ASSETS_LIST;
            if (loanId != null) {
                fooResourceUrl += "?loanId=" + loanId;
            }
            HttpEntity<String> httpEntity = getAuthHttpEntity(accessToken);
            ResponseEntity<String> exchange = restTemplate.exchange(fooResourceUrl, HttpMethod.GET, httpEntity, String.class);
            String content = exchange.getBody();
            Type collectionType = new TypeToken<PagingResult<LoanAssets>>(){}.getType();
            PagingResult<LoanAssets> myJson = new GsonBuilder().registerTypeAdapter(LocalDate.class, new LocalDateAdapter()).registerTypeAdapter(byte[].class, new ByteArrayAdapter())
                    .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter()).create().fromJson(content, collectionType);
            return myJson;
        });
    }

    public LoanAssets create(String accessToken, LoanAssetsVO vo) {


        return encapsulate(() -> {
            String body = new GsonBuilder()
                    .registerTypeAdapter(LocalDate.class, new LocalDateAdapter()).create().toJson(vo);
            RestTemplate restTemplate = new RestTemplate();
            String fooResourceUrl
                    = configProperties.getRest() + Routes.LOAN_ASSETS_CREATE;
            HttpEntity<String> httpEntity = getAuthHttpEntity(body, accessToken);
            ResponseEntity<LoanAssets> exchange = restTemplate.exchange(fooResourceUrl, HttpMethod.POST, httpEntity, LoanAssets.class);
            return exchange.getBody();
        });
    }

    public LoanAssets edit(String accessToken, LoanAssetsVO vo) {
        String body = new GsonBuilder().create().toJson(vo);
        RestTemplate restTemplate = new RestTemplate();
        String fooResourceUrl
                = configProperties.getRest() + Routes.LOAN_ASSETS_EDIT;
        HttpEntity<String> httpEntity = getAuthHttpEntity(body, accessToken);

        return encapsulate(() -> {
            ResponseEntity<LoanAssets> exchange = restTemplate.exchange(fooResourceUrl, HttpMethod.POST, httpEntity, LoanAssets.class);
            return exchange.getBody();
        });
    }


}