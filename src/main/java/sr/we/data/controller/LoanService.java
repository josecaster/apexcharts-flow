package sr.we.data.controller;

import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.client.RestTemplate;
import sr.we.shekelflowcore.entity.Loan;
import sr.we.shekelflowcore.entity.helper.PagingResult;
import sr.we.shekelflowcore.entity.helper.vo.LoanVO;
import sr.we.shekelflowcore.settings.Routes;

import java.util.Arrays;
import java.util.List;

@Controller
public class LoanService extends MyController {

    public Loan get(Long id, String accessToken) {
        RestTemplate restTemplate = new RestTemplate();

        return encapsulate(() -> {
            ResponseEntity<Loan> exchange = restTemplate.exchange(configProperties.getRest() + Routes.LOAN_GET + "?id=" + id, HttpMethod.GET, getAuthHttpEntity(accessToken), Loan.class);
            Loan body = exchange.getBody();
            return body;
        });
    }

    public PagingResult<Loan> list(String accessToken, LoanVO vo) {
        RestTemplate restTemplate = new RestTemplate();
        String body = new GsonBuilder().create().toJson(vo);
        String fooResourceUrl
                = configProperties.getRest() + Routes.LOAN_LIST;
        HttpEntity<String> httpEntity = getAuthHttpEntity(body,accessToken);

        return encapsulate(() -> {
            ResponseEntity<String> exchange = restTemplate.exchange(fooResourceUrl, HttpMethod.POST, httpEntity, String.class);
            return transform(exchange,new TypeToken<PagingResult<Loan>>(){}.getType());
        });
    }

    public Loan create(String accessToken, LoanVO vo) {


        return encapsulate(() -> {
            String body = new GsonBuilder().create().toJson(vo);
            RestTemplate restTemplate = new RestTemplate();
            String fooResourceUrl
                    = configProperties.getRest() + Routes.LOAN_CREATE;
            HttpEntity<String> httpEntity = getAuthHttpEntity(body, accessToken);
            ResponseEntity<Loan> exchange = restTemplate.exchange(fooResourceUrl, HttpMethod.POST, httpEntity, Loan.class);
            return exchange.getBody();
        });
    }

    public Loan edit(String accessToken, LoanVO vo) {
        String body = new GsonBuilder().create().toJson(vo);
        RestTemplate restTemplate = new RestTemplate();
        String fooResourceUrl
                = configProperties.getRest() + Routes.LOAN_EDIT;
        HttpEntity<String> httpEntity = getAuthHttpEntity(body, accessToken);

        return encapsulate(() -> {
            ResponseEntity<Loan> exchange = restTemplate.exchange(fooResourceUrl, HttpMethod.POST, httpEntity, Loan.class);
            return exchange.getBody();
        });
    }

}