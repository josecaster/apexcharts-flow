package sr.we.data.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.client.RestTemplate;
import sr.we.shekelflowcore.entity.CurrencyExchange;
import sr.we.shekelflowcore.entity.helper.PagingResult;
import sr.we.shekelflowcore.entity.helper.adapter.ByteArrayAdapter;
import sr.we.shekelflowcore.entity.helper.adapter.CurrencyExchangeBody;
import sr.we.shekelflowcore.entity.helper.adapter.LocalDateAdapter;
import sr.we.shekelflowcore.entity.helper.adapter.LocalDateTimeAdapter;
import sr.we.shekelflowcore.entity.helper.vo.CurrencyExchangeVO;
import sr.we.shekelflowcore.settings.Routes;

import java.io.IOException;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Controller
public class ExchangeRateService extends MyController {

    public BigDecimal exchange(String from, String to, Long businessId, String accessToken) throws IOException {
        RestTemplate restTemplate = new RestTemplate();

        return encapsulate(() -> {
            String url = configProperties.getRest() + Routes.CURRENCY_EXCHANGE_GET + "?from=" + from + "&to=" + to + "&businessId=" + businessId;
            ResponseEntity<CurrencyExchangeBody> exchange = restTemplate.exchange(url, HttpMethod.GET, getAuthHttpEntity(accessToken), CurrencyExchangeBody.class);
            CurrencyExchangeBody body = exchange.getBody();
            assert body != null;
            return BigDecimal.valueOf(body.getRate());
        });
    }

    public PagingResult<CurrencyExchange> list(String accessToken, CurrencyExchangeVO vo) {
        Gson gson = new GsonBuilder().create();
        String body = gson.toJson(vo);
        RestTemplate restTemplate = new RestTemplate();
        String fooResourceUrl = configProperties.getRest() + Routes.CURRENCY_EXCHANGE_LIST;

        return encapsulate(() -> {
            HttpEntity<String> httpEntity = getAuthHttpEntity(body,accessToken);
            CurrencyExchangeVO s = gson.fromJson(body, CurrencyExchangeVO.class);
            ResponseEntity<String> exchange = restTemplate.exchange(fooResourceUrl, HttpMethod.POST, httpEntity, String.class);
            return transform(exchange,new TypeToken<PagingResult<CurrencyExchange>>(){}.getType());
        });
    }

    public CurrencyExchange create(String accessToken, CurrencyExchangeVO vo) {


        return encapsulate(() -> {
            String body = new GsonBuilder().create().toJson(vo);
            RestTemplate restTemplate = new RestTemplate();
            String fooResourceUrl
                    = configProperties.getRest() + Routes.CURRENCY_EXCHANGE_CREATE;
            HttpEntity<String> httpEntity = getAuthHttpEntity(body, accessToken);
            ResponseEntity<CurrencyExchange> exchange = restTemplate.exchange(fooResourceUrl, HttpMethod.POST, httpEntity, CurrencyExchange.class);
            return exchange.getBody();
        });
    }

}
