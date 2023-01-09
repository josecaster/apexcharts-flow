package sr.we.data.controller;

import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.client.RestTemplate;
import sr.we.shekelflowcore.entity.PaymentTransaction;
import sr.we.shekelflowcore.entity.helper.PagingResult;
import sr.we.shekelflowcore.entity.helper.adapter.LocalDateAdapter;
import sr.we.shekelflowcore.entity.helper.adapter.LocalDateTimeAdapter;
import sr.we.shekelflowcore.entity.helper.vo.PaymentTransactionVO;
import sr.we.shekelflowcore.settings.Routes;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Controller
public class PaymentTransactionService extends MyController {

    public PagingResult<PaymentTransaction> list(String accessToken, PaymentTransactionVO vo) {
        String body = new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter()).
                create().toJson(vo);
        RestTemplate restTemplate = new RestTemplate();
        String fooResourceUrl = configProperties.getRest() + Routes.PAYMENT_TRANSACTION_LIST;
        HttpEntity<String> httpEntity = getAuthHttpEntity(body,accessToken);

        return encapsulate(() -> {
            ResponseEntity<String> exchange = restTemplate.exchange(fooResourceUrl, HttpMethod.POST, httpEntity, String.class);
            return transform(exchange, new TypeToken<PagingResult<PaymentTransaction>>() {
            }.getType());
        });
    }

    public PaymentTransaction create(String accessToken, PaymentTransactionVO vo) {


        return encapsulate(() -> {
            String body = new GsonBuilder()
                    .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                    .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter()).
                    create().toJson(vo);
            RestTemplate restTemplate = new RestTemplate();
            String fooResourceUrl = configProperties.getRest() + Routes.PAYMENT_TRANSACTION_CREATE;
            HttpEntity<String> httpEntity = getAuthHttpEntity(body, accessToken);
            ResponseEntity<PaymentTransaction> exchange = restTemplate.exchange(fooResourceUrl, HttpMethod.POST, httpEntity, PaymentTransaction.class);
            return exchange.getBody();
        });
    }

    public Long delete(String accessToken, PaymentTransactionVO vo) {
        return encapsulate(() -> {
            String body = new GsonBuilder()
                    .registerTypeAdapter(LocalDate.class, new LocalDateAdapter()).create().toJson(vo);
            RestTemplate restTemplate = new RestTemplate();
            String fooResourceUrl = configProperties.getRest() + Routes.PAYMENT_TRANSACTION_DELETE;
            HttpEntity<String> httpEntity = getAuthHttpEntity(body, accessToken);
            ResponseEntity<Long> exchange = restTemplate.exchange(fooResourceUrl, HttpMethod.POST, httpEntity, Long.class);
            return exchange.getBody();
        });
    }
//
//    public Business edit(String accessToken, BusinessVO vo){
//        String body = new GsonBuilder().create().toJson(vo);
//        RestTemplate restTemplate = new RestTemplate();
//        String fooResourceUrl
//                = configProperties.getRest()+ Services.BUSINESS_EDIT;
//        HttpEntity<String> httpEntity = getAuthHttpEntity(body,accessToken);
//
//        return encapsulate(() -> {
//            ResponseEntity<Business> exchange = restTemplate.exchange(fooResourceUrl, HttpMethod.POST, httpEntity, Business.class);
//            return exchange.getBody();
//        });
//    }


}