package sr.we.data.controller;

import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.client.RestTemplate;
import sr.we.shekelflowcore.entity.helper.adapter.CurrencyExchange;
import sr.we.shekelflowcore.settings.Routes;

import java.io.IOException;
import java.math.BigDecimal;

@Controller
public class ExchangeRateService extends MyController {

    public BigDecimal exchange(String from, String to, Long businessId, String accessToken) throws IOException {
        RestTemplate restTemplate = new RestTemplate();

        return encapsulate(() -> {
            String url = configProperties.getRest() + Routes.CURRENCY_EXCHANGE_GET + "?from=" + from + "&to=" + to + "&businessId=" + businessId;
            ResponseEntity<CurrencyExchange> exchange = restTemplate.exchange(url, HttpMethod.GET, getAuthHttpEntity(accessToken), CurrencyExchange.class);
            CurrencyExchange body = exchange.getBody();
            return BigDecimal.valueOf(body.getRate());
        });
    }

}
