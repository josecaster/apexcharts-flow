package sr.we.data.controller;

import com.google.gson.GsonBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.client.RestTemplate;
import sr.we.shekelflowcore.entity.Invoice;
import sr.we.shekelflowcore.entity.InvoiceSetting;
import sr.we.shekelflowcore.entity.helper.vo.InvoiceVO;
import sr.we.shekelflowcore.settings.Routes;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Controller
public class InvoiceService extends MyController {

    public List<Invoice> list(String accessToken, InvoiceVO vo) {
        String body = new GsonBuilder().create().toJson(vo);
        RestTemplate restTemplate = new RestTemplate();
        String fooResourceUrl = configProperties.getRest() + Routes.INVOICE_LIST;
//        HttpEntity<String> httpEntity = getAuthHttpEntity(body,accessToken);

        return encapsulate(() -> {
            HttpEntity<String> httpEntity = getAuthHttpEntity(body, accessToken);
            ResponseEntity<Invoice[]> exchange = restTemplate.exchange(fooResourceUrl, HttpMethod.POST, httpEntity, Invoice[].class);
//            ResponseEntity<Invoice[]> exchange = restTemplate.exchange(fooResourceUrl, HttpMethod.GET, httpEntity, Invoice[].class);
            return Arrays.asList(Objects.requireNonNull(exchange.getBody()));
        });
    }

    public Invoice create(String accessToken, InvoiceVO vo) {


        return encapsulate(() -> {
            String body = new GsonBuilder().create().toJson(vo);
            RestTemplate restTemplate = new RestTemplate();
            String fooResourceUrl
                    = configProperties.getRest() + Routes.INVOICE_CREATE;
            HttpEntity<String> httpEntity = getAuthHttpEntity(body, accessToken);
            ResponseEntity<Invoice> exchange = restTemplate.exchange(fooResourceUrl, HttpMethod.POST, httpEntity, Invoice.class);
            return exchange.getBody();
        });
    }

    public Invoice get(Long id, String accessToken) {
        RestTemplate restTemplate = new RestTemplate();

        return encapsulate(() -> {
            ResponseEntity<Invoice> exchange = restTemplate.exchange(configProperties.getRest() + Routes.INVOICE_GET + "?id=" + id, HttpMethod.GET, getAuthHttpEntity(accessToken), Invoice.class);
            return exchange.getBody();
        });
    }

    public InvoiceSetting getSettings(Long businessId, String accessToken) {
        RestTemplate restTemplate = new RestTemplate();

        return encapsulate(() -> {
            ResponseEntity<InvoiceSetting> exchange = restTemplate.exchange(configProperties.getRest() + Routes.INVOICE_GET_SETTING + "?businessId=" + businessId, HttpMethod.GET, getAuthHttpEntity(accessToken), InvoiceSetting.class);
            return exchange.getBody();
        });
    }

    public boolean sendEmail(Long id, String value, String accessToken) {
        RestTemplate restTemplate = new RestTemplate();
        String fooResourceUrl = configProperties.getRest() + Routes.INVOICE_SEND+"?id="+id+"&email="+value;
        return encapsulate(() -> {
            HttpEntity<String> httpEntity = getAuthHttpEntity(accessToken);
            ResponseEntity<Boolean> exchange = restTemplate.exchange(fooResourceUrl, HttpMethod.POST, httpEntity, Boolean.class);
            return exchange.getBody();
        });
    }

    public String getSharableLink(Long id, String accessToken) {
        RestTemplate restTemplate = new RestTemplate();
        String fooResourceUrl = configProperties.getRest() + Routes.INVOICE_SHARE+"?id="+id;
        return encapsulate(() -> {
            HttpEntity<String> httpEntity = getAuthHttpEntity(accessToken);
            ResponseEntity<String> exchange = restTemplate.exchange(fooResourceUrl, HttpMethod.POST, httpEntity, String.class);
            return exchange.getBody();
        });
    }

    public Invoice getByToken(String invoiceToken) {
        RestTemplate restTemplate = new RestTemplate();

        return encapsulate(() -> {
            ResponseEntity<Invoice> exchange = restTemplate.exchange(configProperties.getRest() + Routes.INVOICE_TOKEN + "?token=" + invoiceToken, HttpMethod.GET, getHttpEntity(), Invoice.class);
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