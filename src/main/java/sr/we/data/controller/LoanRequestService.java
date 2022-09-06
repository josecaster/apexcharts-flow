package sr.we.data.controller;

import com.google.gson.GsonBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.client.RestTemplate;
import sr.we.shekelflowcore.entity.LoanRequest;
import sr.we.shekelflowcore.entity.LoanRequestPlan;
import sr.we.shekelflowcore.entity.helper.adapter.LoanRequestBody;
import sr.we.shekelflowcore.entity.helper.adapter.LoanRequestSchedulePlan;
import sr.we.shekelflowcore.entity.helper.adapter.LoanRequestPlanningGenBody;
import sr.we.shekelflowcore.entity.helper.adapter.LocalDateAdapter;
import sr.we.shekelflowcore.entity.helper.vo.LoanRequestVO;
import sr.we.shekelflowcore.settings.Routes;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

@Controller
public class LoanRequestService extends MyController {

    public LoanRequest get(Long id, String accessToken) {
        RestTemplate restTemplate = new RestTemplate();

        return encapsulate(() -> {
            ResponseEntity<LoanRequest> exchange = restTemplate.exchange(configProperties.getRest() + Routes.LOAN_REQUEST_GET + "?id=" + id, HttpMethod.GET, getAuthHttpEntity(accessToken), LoanRequest.class);
            LoanRequest body = exchange.getBody();
            return body;
        });
    }

    public List<LoanRequest> list(String accessToken, Long businessId, Long loanId) {


        return encapsulate(() -> {
            RestTemplate restTemplate = new RestTemplate();
            String fooResourceUrl
                    = configProperties.getRest() + Routes.LOAN_REQUEST_LIST + "?businessId=" + businessId;
            if (loanId != null) {
                fooResourceUrl += "&loanId=" + loanId;
            }
            HttpEntity<String> httpEntity = getAuthHttpEntity(accessToken);
            ResponseEntity<LoanRequest[]> exchange = restTemplate.exchange(fooResourceUrl, HttpMethod.GET, httpEntity, LoanRequest[].class);
            return Arrays.asList(exchange.getBody());
        });
    }

    public LoanRequest create(String accessToken, LoanRequestBody vo) {


        return encapsulate(() -> {
            String body = new GsonBuilder()
                    .registerTypeAdapter(LocalDate.class, new LocalDateAdapter()).create().toJson(vo);
            RestTemplate restTemplate = new RestTemplate();
            String fooResourceUrl
                    = configProperties.getRest() + Routes.LOAN_REQUEST_CREATE;
            HttpEntity<String> httpEntity = getAuthHttpEntity(body, accessToken);
            ResponseEntity<LoanRequest> exchange = restTemplate.exchange(fooResourceUrl, HttpMethod.POST, httpEntity, LoanRequest.class);
            return exchange.getBody();
        });
    }

    public LoanRequest edit(String accessToken, LoanRequestVO vo) {
        String body = new GsonBuilder().create().toJson(vo);
        RestTemplate restTemplate = new RestTemplate();
        String fooResourceUrl
                = configProperties.getRest() + Routes.LOAN_REQUEST_EDIT;
        HttpEntity<String> httpEntity = getAuthHttpEntity(body, accessToken);

        return encapsulate(() -> {
            ResponseEntity<LoanRequest> exchange = restTemplate.exchange(fooResourceUrl, HttpMethod.POST, httpEntity, LoanRequest.class);
            return exchange.getBody();
        });
    }

    public LoanRequestSchedulePlan generatePlanning(LoanRequestPlan.Type type, Long freqAmount , LocalDate date, Boolean intrestFirst, Long loanRequestId, String accessToken) {
        return encapsulate(() -> {
            LoanRequestPlanningGenBody src = new LoanRequestPlanningGenBody(loanRequestId, date, type, intrestFirst);
            src.setFreqAmount(freqAmount);
            String body = new GsonBuilder()
                    .registerTypeAdapter(LocalDate.class, new LocalDateAdapter()).create().toJson(src);
            RestTemplate restTemplate = new RestTemplate();
            String fooResourceUrl
                    = configProperties.getRest() + Routes.LOAN_REQUEST_PLAN_PLAN;
            HttpEntity<String> httpEntity = getAuthHttpEntity(body, accessToken);
            ResponseEntity<LoanRequestSchedulePlan> exchange = restTemplate.exchange(fooResourceUrl, HttpMethod.POST, httpEntity, LoanRequestSchedulePlan.class);
            return exchange.getBody();
        });
    }

    public LoanRequestPlan save(LoanRequestSchedulePlan loanRequestPlan, String accessToken) {
        return encapsulate(() -> {
            String body = new GsonBuilder()
                    .registerTypeAdapter(LocalDate.class, new LocalDateAdapter()).create().toJson(loanRequestPlan);
            RestTemplate restTemplate = new RestTemplate();
            String fooResourceUrl
                    = configProperties.getRest() + Routes.LOAN_REQUEST_PLAN_CREATE;
            HttpEntity<String> httpEntity = getAuthHttpEntity(body, accessToken);
            ResponseEntity<LoanRequestPlan> exchange = restTemplate.exchange(fooResourceUrl, HttpMethod.POST, httpEntity, LoanRequestPlan.class);
            return exchange.getBody();
        });
    }

    public BigDecimal getBalance(String accessToken, Long loanRequestId, LocalDate initDate) {


        return encapsulate(() -> {
            RestTemplate restTemplate = new RestTemplate();
            String fooResourceUrl
                    = configProperties.getRest() + Routes.LOAN_REQUEST_GET_BALANCE + "?loanRequestId=" + loanRequestId+"&initDate="+initDate.format(DateTimeFormatter.ISO_DATE);

            HttpEntity<String> httpEntity = getAuthHttpEntity(accessToken);
            ResponseEntity<LoanRequest> exchange = restTemplate.exchange(fooResourceUrl, HttpMethod.GET, httpEntity, LoanRequest.class);
            return exchange.getBody().getBalance();
        });
    }

    public List<LoanRequestPlan> listPlan(String accessToken, Long loanRequestId) {


        return encapsulate(() -> {
            RestTemplate restTemplate = new RestTemplate();
            String fooResourceUrl
                    = configProperties.getRest() + Routes.LOAN_REQUEST_PLAN_LIST + "?loanRequestId=" + loanRequestId;
            HttpEntity<String> httpEntity = getAuthHttpEntity(accessToken);
            ResponseEntity<LoanRequestPlan[]> exchange = restTemplate.exchange(fooResourceUrl, HttpMethod.GET, httpEntity, LoanRequestPlan[].class);
            return Arrays.asList(exchange.getBody());
        });
    }

    public LoanRequest procesNextStep(String accessToken, Long loanRequestId, LoanRequest.Status status) {
        LoanRequestVO vo = new LoanRequestVO();
        vo.setId(loanRequestId);
        vo.setStatus(status);
        String body = new GsonBuilder().create().toJson(vo);
        RestTemplate restTemplate = new RestTemplate();
        String fooResourceUrl
                = configProperties.getRest() + Routes.LOAN_REQUEST_PROCES_NEXT_STEP;
        HttpEntity<String> httpEntity = getAuthHttpEntity(body, accessToken);

        return encapsulate(() -> {
            ResponseEntity<LoanRequest> exchange = restTemplate.exchange(fooResourceUrl, HttpMethod.POST, httpEntity, LoanRequest.class);
            return exchange.getBody();
        });

    }

    public LoanRequest procesPrevStep(String accessToken, Long loanRequestId, LoanRequest.Status status) {
        LoanRequestVO vo = new LoanRequestVO();
        vo.setId(loanRequestId);
        vo.setStatus(status);
        String body = new GsonBuilder().create().toJson(vo);
        RestTemplate restTemplate = new RestTemplate();
        String fooResourceUrl
                = configProperties.getRest() + Routes.LOAN_REQUEST_PROCES_PREV_STEP;
        HttpEntity<String> httpEntity = getAuthHttpEntity(body, accessToken);

        return encapsulate(() -> {
            ResponseEntity<LoanRequest> exchange = restTemplate.exchange(fooResourceUrl, HttpMethod.POST, httpEntity, LoanRequest.class);
            return exchange.getBody();
        });
    }
}