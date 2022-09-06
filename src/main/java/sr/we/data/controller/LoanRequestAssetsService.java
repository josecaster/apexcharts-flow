package sr.we.data.controller;

import com.google.gson.GsonBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.client.RestTemplate;
import sr.we.shekelflowcore.entity.LoanRequestAssets;
import sr.we.shekelflowcore.entity.helper.adapter.LocalDateAdapter;
import sr.we.shekelflowcore.entity.helper.vo.LoanRequestAssetsVO;
import sr.we.shekelflowcore.settings.Routes;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@Controller
public class LoanRequestAssetsService extends MyController {

    public LoanRequestAssets get(Long id, String accessToken) {
        RestTemplate restTemplate = new RestTemplate();

        return encapsulate(() -> {
            ResponseEntity<LoanRequestAssets> exchange = restTemplate.exchange(configProperties.getRest() + Routes.LOAN_REQUEST_ASSETS_GET + "?id=" + id, HttpMethod.GET, getAuthHttpEntity(accessToken), LoanRequestAssets.class);
            LoanRequestAssets body = exchange.getBody();
            return body;
        });
    }

    public List<LoanRequestAssets> list(String accessToken, Long loanRequestId) {


        return encapsulate(() -> {
            RestTemplate restTemplate = new RestTemplate();
            String fooResourceUrl
                    = configProperties.getRest() + Routes.LOAN_REQUEST_ASSETS_LIST + "?loanRequestId=" + loanRequestId;

            HttpEntity<String> httpEntity = getAuthHttpEntity(accessToken);
            ResponseEntity<LoanRequestAssets[]> exchange = restTemplate.exchange(fooResourceUrl, HttpMethod.GET, httpEntity, LoanRequestAssets[].class);
            return Arrays.asList(exchange.getBody());
        });
    }

    public LoanRequestAssets create(String accessToken, LoanRequestAssetsVO vo) {


        return encapsulate(() -> {
            String body = new GsonBuilder()
                    .registerTypeAdapter(LocalDate.class, new LocalDateAdapter()).create().toJson(vo);
            RestTemplate restTemplate = new RestTemplate();
            String fooResourceUrl
                    = configProperties.getRest() + Routes.LOAN_REQUEST_ASSETS_CREATE;
            HttpEntity<String> httpEntity = getAuthHttpEntity(body, accessToken);
            ResponseEntity<LoanRequestAssets> exchange = restTemplate.exchange(fooResourceUrl, HttpMethod.POST, httpEntity, LoanRequestAssets.class);
            return exchange.getBody();
        });
    }

    public LoanRequestAssets edit(String accessToken, LoanRequestAssetsVO vo) {
        String body = new GsonBuilder().create().toJson(vo);
        RestTemplate restTemplate = new RestTemplate();
        String fooResourceUrl
                = configProperties.getRest() + Routes.LOAN_REQUEST_ASSETS_EDIT;
        HttpEntity<String> httpEntity = getAuthHttpEntity(body, accessToken);

        return encapsulate(() -> {
            ResponseEntity<LoanRequestAssets> exchange = restTemplate.exchange(fooResourceUrl, HttpMethod.POST, httpEntity, LoanRequestAssets.class);
            return exchange.getBody();
        });
    }


}