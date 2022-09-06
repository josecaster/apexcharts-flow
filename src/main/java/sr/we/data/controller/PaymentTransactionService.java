package sr.we.data.controller;

import com.google.gson.GsonBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.client.RestTemplate;
import sr.we.shekelflowcore.entity.PaymentTransaction;
import sr.we.shekelflowcore.entity.helper.adapter.LocalDateAdapter;
import sr.we.shekelflowcore.entity.helper.vo.PaymentTransactionVO;
import sr.we.shekelflowcore.settings.Routes;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@Controller
public class PaymentTransactionService extends MyController {

    public List<PaymentTransaction> list(String accessToken, Long businessId) {
        RestTemplate restTemplate = new RestTemplate();
        String fooResourceUrl = configProperties.getRest() + Routes.PAYMENT_TRANSACTION_LIST + "?businessId=" + businessId;
        HttpEntity<String> httpEntity = getAuthHttpEntity(accessToken);

        return encapsulate(() -> {
            ResponseEntity<PaymentTransaction[]> exchange = restTemplate.exchange(fooResourceUrl, HttpMethod.GET, httpEntity, PaymentTransaction[].class);
            return Arrays.asList(exchange.getBody());
        });
    }

    public PaymentTransaction create(String accessToken, PaymentTransactionVO vo) {


        return encapsulate(() -> {
            String body = new GsonBuilder()
                    .registerTypeAdapter(LocalDate.class, new LocalDateAdapter()).create().toJson(vo);
            RestTemplate restTemplate = new RestTemplate();
            String fooResourceUrl = configProperties.getRest() + Routes.PAYMENT_TRANSACTION_CREATE;
            HttpEntity<String> httpEntity = getAuthHttpEntity(body, accessToken);
            ResponseEntity<PaymentTransaction> exchange = restTemplate.exchange(fooResourceUrl, HttpMethod.POST, httpEntity, PaymentTransaction.class);
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