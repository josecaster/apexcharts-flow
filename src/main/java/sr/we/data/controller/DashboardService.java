package sr.we.data.controller;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.client.RestTemplate;
import sr.we.shekelflowcore.entity.helper.adapter.DashboardAccounts;
import sr.we.shekelflowcore.entity.helper.adapter.DashboardHL;
import sr.we.shekelflowcore.entity.helper.adapter.DashboardTransactionEvents;
import sr.we.shekelflowcore.settings.Routes;

import java.util.Arrays;
import java.util.List;

@Controller
public class DashboardService extends MyController {

    public DashboardHL getActiveLoanRequests(String accessToken, Long businessId) {
        RestTemplate restTemplate = new RestTemplate();
        String fooResourceUrl
                = configProperties.getRest()+ Routes.DASHBOARD_GET_ACTIVE;
        HttpEntity<String> httpEntity = getAuthHttpEntity(accessToken);

        return encapsulate(() -> {
            ResponseEntity<DashboardHL> exchange = restTemplate.exchange(fooResourceUrl+"?businessId="+businessId, HttpMethod.GET, httpEntity, DashboardHL.class);
            return exchange.getBody();
        });
    }

    public DashboardHL getOverdue(String accessToken, Long businessId) {
        RestTemplate restTemplate = new RestTemplate();
        String fooResourceUrl
                = configProperties.getRest()+ Routes.DASHBOARD_GET_OVERDUE;
        HttpEntity<String> httpEntity = getAuthHttpEntity(accessToken);

        return encapsulate(() -> {
            ResponseEntity<DashboardHL> exchange = restTemplate.exchange(fooResourceUrl+"?businessId="+businessId, HttpMethod.GET, httpEntity, DashboardHL.class);
            return exchange.getBody();
        });
    }

    public DashboardHL getMonthsPayments(String accessToken, Long businessId) {
        RestTemplate restTemplate = new RestTemplate();
        String fooResourceUrl
                = configProperties.getRest()+ Routes.DASHBOARD_GET_NEXT;
        HttpEntity<String> httpEntity = getAuthHttpEntity(accessToken);

        return encapsulate(() -> {
            ResponseEntity<DashboardHL> exchange = restTemplate.exchange(fooResourceUrl+"?businessId="+businessId, HttpMethod.GET, httpEntity, DashboardHL.class);
            return exchange.getBody();
        });
    }

    public DashboardHL getProfits(String accessToken, Long businessId) {
        RestTemplate restTemplate = new RestTemplate();
        String fooResourceUrl
                = configProperties.getRest()+ Routes.DASHBOARD_GET_PROFITS;
        HttpEntity<String> httpEntity = getAuthHttpEntity(accessToken);

        return encapsulate(() -> {
            ResponseEntity<DashboardHL> exchange = restTemplate.exchange(fooResourceUrl+"?businessId="+businessId, HttpMethod.GET, httpEntity, DashboardHL.class);
            return exchange.getBody();
        });
    }

    public List<DashboardTransactionEvents> getTransactionEvents(String accessToken, Long businessId) {
        RestTemplate restTemplate = new RestTemplate();
        String fooResourceUrl
                = configProperties.getRest()+ Routes.DASHBOARD_GET_TRANSACTION_EVENTS;
        HttpEntity<String> httpEntity = getAuthHttpEntity(accessToken);

        return encapsulate(() -> {
            ResponseEntity<DashboardTransactionEvents[]> exchange = restTemplate.exchange(fooResourceUrl+"?businessId="+businessId, HttpMethod.GET, httpEntity, DashboardTransactionEvents[].class);
            return Arrays.asList(exchange.getBody());
        });
    }

    public List<DashboardAccounts> getAccounts(String accessToken, Long businessId) {
        RestTemplate restTemplate = new RestTemplate();
        String fooResourceUrl
                = configProperties.getRest()+ Routes.DASHBOARD_GET_ACCOUNTS;
        HttpEntity<String> httpEntity = getAuthHttpEntity(accessToken);

        return encapsulate(() -> {
            ResponseEntity<DashboardAccounts[]> exchange = restTemplate.exchange(fooResourceUrl+"?businessId="+businessId, HttpMethod.GET, httpEntity, DashboardAccounts[].class);
            return Arrays.asList(exchange.getBody());
        });
    }
}
