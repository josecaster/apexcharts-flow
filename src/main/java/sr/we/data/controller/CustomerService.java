package sr.we.data.controller;

import com.google.gson.GsonBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.client.RestTemplate;
import sr.we.shekelflowcore.entity.Customer;
import sr.we.shekelflowcore.entity.helper.adapter.CustomerBody;
import sr.we.shekelflowcore.entity.helper.vo.CustomerVO;
import sr.we.shekelflowcore.settings.Routes;

import java.util.Arrays;
import java.util.List;

@Controller
public class CustomerService extends MyController{

    public Customer get(Long id, String accessToken){
        RestTemplate restTemplate = new RestTemplate();

        return encapsulate(() -> {
            ResponseEntity<Customer> exchange = restTemplate.exchange(configProperties.getRest() + Routes.CUSTOMER_GET + "?id=" + id, HttpMethod.GET, getAuthHttpEntity(accessToken), Customer.class);
            Customer body = exchange.getBody();
            return body;
        });
    }

    public List<Customer> list(Long businessId,String accessToken){
        RestTemplate restTemplate = new RestTemplate();
        String fooResourceUrl
                = configProperties.getRest()+ Routes.CUSTOMER_LIST;
        HttpEntity<String> httpEntity = getAuthHttpEntity(accessToken);

        return encapsulate(() -> {
            ResponseEntity<Customer[]> exchange = restTemplate.exchange(fooResourceUrl+"?businessId="+businessId, HttpMethod.GET, httpEntity, Customer[].class);
            return Arrays.asList(exchange.getBody());
        });
    }

    public Customer create(String accessToken, CustomerBody vo){
        String body = new GsonBuilder().create().toJson(vo);
        RestTemplate restTemplate = new RestTemplate();
        String fooResourceUrl
                = configProperties.getRest()+ Routes.CUSTOMER_CREATE;
        HttpEntity<String> httpEntity = getAuthHttpEntity(body,accessToken);

        return encapsulate(() -> {
            ResponseEntity<Customer> exchange = restTemplate.exchange(fooResourceUrl, HttpMethod.POST, httpEntity, Customer.class);
            return exchange.getBody();
        });
    }

    public Customer edit(String accessToken, CustomerVO vo){
        String body = new GsonBuilder().create().toJson(vo);
        RestTemplate restTemplate = new RestTemplate();
        String fooResourceUrl
                = configProperties.getRest()+ Routes.CUSTOMER_EDIT;
        HttpEntity<String> httpEntity = getAuthHttpEntity(body,accessToken);

        return encapsulate(() -> {
            ResponseEntity<Customer> exchange = restTemplate.exchange(fooResourceUrl, HttpMethod.POST, httpEntity, Customer.class);
            return exchange.getBody();
        });
    }


}