package sr.we.data.controller;

import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.client.RestTemplate;
import sr.we.shekelflowcore.entity.Business;
import sr.we.shekelflowcore.entity.Items;
import sr.we.shekelflowcore.entity.helper.PagingResult;
import sr.we.shekelflowcore.entity.helper.adapter.ByteArrayAdapter;
import sr.we.shekelflowcore.entity.helper.adapter.LocalDateAdapter;
import sr.we.shekelflowcore.entity.helper.adapter.LocalDateTimeAdapter;
import sr.we.shekelflowcore.entity.helper.vo.ServicesVO;
import sr.we.shekelflowcore.settings.Routes;
import sr.we.ui.views.pos.Item;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.LocalDateTime;
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

    public PagingResult<Items> list(String accessToken, ServicesVO vo) {
        RestTemplate restTemplate = new RestTemplate();
        String body = new GsonBuilder().registerTypeAdapter(LocalDate.class, new LocalDateAdapter()).create().toJson(vo);
        String fooResourceUrl = configProperties.getRest() + Routes.ITEMS_LIST;
        HttpEntity<String> httpEntity = getAuthHttpEntity(body,accessToken);

        return encapsulate(() -> {
            ResponseEntity<String> exchange = restTemplate.exchange(fooResourceUrl, HttpMethod.POST, httpEntity, String.class);
            String content = exchange.getBody();
            Type collectionType = new TypeToken<PagingResult<Items>>(){}.getType();
            PagingResult<Items> myJson = new GsonBuilder().registerTypeAdapter(LocalDate.class, new LocalDateAdapter()).registerTypeAdapter(byte[].class, new ByteArrayAdapter())
                    .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter()).create().fromJson(content, collectionType);
            return myJson;
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