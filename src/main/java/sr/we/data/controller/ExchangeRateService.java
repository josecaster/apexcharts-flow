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
import sr.we.shekelflowcore.entity.helper.adapter.CurrencyExchangeBody;
import sr.we.shekelflowcore.entity.helper.adapter.LocalDateAdapter;
import sr.we.shekelflowcore.entity.helper.adapter.LocalDateTimeAdapter;
import sr.we.shekelflowcore.entity.helper.vo.CurrencyExchangeVO;
import sr.we.shekelflowcore.settings.Routes;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Controller
public class ExchangeRateService extends MyController {

    public BigDecimal exchange(String from, String to, Long businessId,LocalDate localDate, String accessToken) throws IOException {
        if(localDate == null){
            localDate = LocalDate.now();
        }
        return exchange(from, to, businessId, "b",localDate, accessToken);
    }

    public BigDecimal exchange(String from, String to, Long businessId, String bs,LocalDate localDate, String accessToken) throws IOException {
        CurrencyExchangeBody currencyExchangeBody = exchangeResult(from, to, businessId, bs, localDate, accessToken);
        assert currencyExchangeBody != null;
        return BigDecimal.valueOf(currencyExchangeBody.getRate());
    }

    public CurrencyExchangeBody exchangeResult(String from, String to, Long businessId, String bs, LocalDate localDate, String accessToken) throws IOException {
        if(from.equalsIgnoreCase(to)){
            CurrencyExchangeBody currencyExchangeBody = new CurrencyExchangeBody();
            currencyExchangeBody.setFrom(from);
            currencyExchangeBody.setTo(to);
            currencyExchangeBody.setRate(1d);
            return currencyExchangeBody;
        }
        CurrencyExchangeVO vo = new CurrencyExchangeVO();
        vo.setFrom(from);
        vo.setTo(to);
        vo.setBusinessId(businessId);
        vo.setBuySell(bs);
        vo.setLogDate(localDate);
        RestTemplate restTemplate = new RestTemplate();
        String bodyString = new GsonBuilder().registerTypeAdapter(LocalDate.class, new LocalDateAdapter()).create().toJson(vo);

        return encapsulate(() -> {
            String url = configProperties.getRest() + Routes.CURRENCY_EXCHANGE_GET;
            ResponseEntity<CurrencyExchangeBody> exchange = restTemplate.exchange(url, HttpMethod.POST, getAuthHttpEntity(bodyString, accessToken), CurrencyExchangeBody.class);
            CurrencyExchangeBody body = exchange.getBody();
            assert body != null;
            return body;
        });
    }

    public PagingResult<CurrencyExchange> list(String accessToken, CurrencyExchangeVO vo) {
        Gson gson = new GsonBuilder().create();
        String body = gson.toJson(vo);
        RestTemplate restTemplate = new RestTemplate();
        String fooResourceUrl = configProperties.getRest() + Routes.CURRENCY_EXCHANGE_LIST;

        return encapsulate(() -> {
            HttpEntity<String> httpEntity = getAuthHttpEntity(body, accessToken);
            CurrencyExchangeVO s = gson.fromJson(body, CurrencyExchangeVO.class);
            ResponseEntity<String> exchange = restTemplate.exchange(fooResourceUrl, HttpMethod.POST, httpEntity, String.class);
            return transform(exchange, new TypeToken<PagingResult<CurrencyExchange>>() {
            }.getType());
        });
    }

    public CurrencyExchange create(String accessToken, CurrencyExchangeVO vo) {


        return encapsulate(() -> {
            String body = new GsonBuilder().registerTypeAdapter(LocalDate.class, new LocalDateAdapter()).registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter()).create().toJson(vo);
            RestTemplate restTemplate = new RestTemplate();
            String fooResourceUrl = configProperties.getRest() + Routes.CURRENCY_EXCHANGE_CREATE;
            HttpEntity<String> httpEntity = getAuthHttpEntity(body, accessToken);
            ResponseEntity<CurrencyExchange> exchange = restTemplate.exchange(fooResourceUrl, HttpMethod.POST, httpEntity, CurrencyExchange.class);
            return exchange.getBody();
        });
    }

    public CurrencyExchange buySell(String accessToken, CurrencyExchangeVO vo) {
        return encapsulate(() -> {
            String body = new GsonBuilder().registerTypeAdapter(LocalDate.class, new LocalDateAdapter()).create().toJson(vo);
            RestTemplate restTemplate = new RestTemplate();
            String fooResourceUrl = configProperties.getRest() + Routes.CURRENCY_BUY_SELL;
            HttpEntity<String> httpEntity = getAuthHttpEntity(body, accessToken);
            ResponseEntity<CurrencyExchange> exchange = restTemplate.exchange(fooResourceUrl, HttpMethod.POST, httpEntity, CurrencyExchange.class);
            return exchange.getBody();
        });
    }
}
