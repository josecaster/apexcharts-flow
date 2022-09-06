package sr.we.data.controller;

import com.google.gson.GsonBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.client.RestTemplate;
import sr.we.shekelflowcore.entity.Services;
import sr.we.shekelflowcore.entity.helper.vo.ServicesVO;
import sr.we.shekelflowcore.settings.Routes;

import java.util.Arrays;
import java.util.List;

@Controller
public class ServicesService extends MyController {

    public Services get(Long id, String accessToken) {
        RestTemplate restTemplate = new RestTemplate();

        return encapsulate(() -> {
            ResponseEntity<Services> exchange = restTemplate.exchange(configProperties.getRest() + Routes.SERVICES_GET + "?id=" + id, HttpMethod.GET, getAuthHttpEntity(accessToken), Services.class);
            Services body = exchange.getBody();
            return body;
        });
    }

    public List<Services> list(String accessToken, Long businessId) {
        RestTemplate restTemplate = new RestTemplate();
        String fooResourceUrl = configProperties.getRest() + Routes.SERVICES_LIST + "?businessId=" + businessId;
        HttpEntity<String> httpEntity = getAuthHttpEntity(accessToken);

        return encapsulate(() -> {
            ResponseEntity<Services[]> exchange = restTemplate.exchange(fooResourceUrl, HttpMethod.GET, httpEntity, Services[].class);
            return Arrays.asList(exchange.getBody());
        });
    }

    public Services create(String accessToken, ServicesVO vo) {


        return encapsulate(() -> {
            String body = new GsonBuilder().create().toJson(vo);
            RestTemplate restTemplate = new RestTemplate();
            String fooResourceUrl = configProperties.getRest() + Routes.SERVICES_CREATE;
            HttpEntity<String> httpEntity = getAuthHttpEntity(body, accessToken);
            ResponseEntity<Services> exchange = restTemplate.exchange(fooResourceUrl, HttpMethod.POST, httpEntity, Services.class);
            return exchange.getBody();
        });
    }

    public Services edit(String accessToken, ServicesVO vo) {
//        if(vo.getProductsInventory() != null){
//            vo.getProductsInventory().stream().forEach(f -> {
//                if(f.getProductsInventoryDetails() != null){
//                    f.getProductsInventoryDetails().stream().forEach(g -> g.setProductsInventoryVO(null));
//                }
//            });
//        }
        String body = new GsonBuilder().create().toJson(vo);
        RestTemplate restTemplate = new RestTemplate();
        String fooResourceUrl = configProperties.getRest() + Routes.SERVICES_EDIT;
        HttpEntity<String> httpEntity = getAuthHttpEntity(body, accessToken);

        return encapsulate(() -> {
            ResponseEntity<Services> exchange = restTemplate.exchange(fooResourceUrl, HttpMethod.POST, httpEntity, Services.class);
            return exchange.getBody();
        });
    }

}