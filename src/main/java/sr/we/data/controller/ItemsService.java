package sr.we.data.controller;

import com.google.gson.GsonBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.client.RestTemplate;
import sr.we.shekelflowcore.entity.Items;
import sr.we.shekelflowcore.entity.helper.vo.ServicesVO;
import sr.we.shekelflowcore.settings.Routes;

import java.util.Arrays;
import java.util.List;

@Controller
public class ItemsService extends MyController {

    public Items get(Long id, String accessToken) {
        RestTemplate restTemplate = new RestTemplate();

        return encapsulate(() -> {
            ResponseEntity<Items> exchange = restTemplate.exchange(configProperties.getRest() + Routes.ITEMS_GET + "?id=" + id, HttpMethod.GET, getAuthHttpEntity(accessToken), Items.class);
            Items body = exchange.getBody();
            return body;
        });
    }

    public List<Items> list(String accessToken, Long businessId) {
        RestTemplate restTemplate = new RestTemplate();
        String fooResourceUrl = configProperties.getRest() + Routes.ITEMS_LIST + "?businessId=" + businessId;
        HttpEntity<String> httpEntity = getAuthHttpEntity(accessToken);

        return encapsulate(() -> {
            ResponseEntity<Items[]> exchange = restTemplate.exchange(fooResourceUrl, HttpMethod.GET, httpEntity, Items[].class);
            return Arrays.asList(exchange.getBody());
        });
    }

    public Items create(String accessToken, ServicesVO vo) {


        return encapsulate(() -> {
            String body = new GsonBuilder().create().toJson(vo);
            RestTemplate restTemplate = new RestTemplate();
            String fooResourceUrl = configProperties.getRest() + Routes.ITEMS_CREATE;
            HttpEntity<String> httpEntity = getAuthHttpEntity(body, accessToken);
            ResponseEntity<Items> exchange = restTemplate.exchange(fooResourceUrl, HttpMethod.POST, httpEntity, Items.class);
            return exchange.getBody();
        });
    }

    public Items edit(String accessToken, ServicesVO vo) {
        if(vo.getProductsInventory() != null){
            vo.getProductsInventory().stream().forEach(f -> {
                if(f.getProductsInventoryDetails() != null){
                    f.getProductsInventoryDetails().stream().forEach(g -> g.setProductsInventoryVO(null));
                }
            });
        }
        String body = new GsonBuilder().create().toJson(vo);
        RestTemplate restTemplate = new RestTemplate();
        String fooResourceUrl = configProperties.getRest() + Routes.ITEMS_EDIT;
        HttpEntity<String> httpEntity = getAuthHttpEntity(body, accessToken);

        return encapsulate(() -> {
            ResponseEntity<Items> exchange = restTemplate.exchange(fooResourceUrl, HttpMethod.POST, httpEntity, Items.class);
            return exchange.getBody();
        });
    }

}