package sr.we.data.controller;

import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.client.RestTemplate;
import sr.we.shekelflowcore.entity.Product;
import sr.we.shekelflowcore.entity.helper.PagingResult;
import sr.we.shekelflowcore.entity.helper.vo.ProductVO;
import sr.we.shekelflowcore.settings.Routes;

import java.util.Arrays;
import java.util.List;

@Controller
public class ProductService extends MyController {

    public Product get(Long id, String accessToken) {
        RestTemplate restTemplate = new RestTemplate();

        return encapsulate(() -> {
            ResponseEntity<Product> exchange = restTemplate.exchange(configProperties.getRest() + Routes.PRODUCT_GET + "?id=" + id, HttpMethod.GET, getAuthHttpEntity(accessToken), Product.class);
            Product body = exchange.getBody();
            return body;
        });
    }

    public PagingResult<Product> list(String accessToken, Long businessId) {
        RestTemplate restTemplate = new RestTemplate();
        String fooResourceUrl = configProperties.getRest() + Routes.PRODUCT_LIST + "?businessId=" + businessId;
        HttpEntity<String> httpEntity = getAuthHttpEntity(accessToken);

        return encapsulate(() -> {
            ResponseEntity<String> exchange = restTemplate.exchange(fooResourceUrl, HttpMethod.GET, httpEntity, String.class);
            return transform(exchange,new TypeToken<PagingResult<Product>>(){}.getType());
        });
    }

    public Product create(String accessToken, ProductVO vo) {


        return encapsulate(() -> {
            String body = new GsonBuilder().create().toJson(vo);
            RestTemplate restTemplate = new RestTemplate();
            String fooResourceUrl = configProperties.getRest() + Routes.PRODUCT_CREATE;
            HttpEntity<String> httpEntity = getAuthHttpEntity(body, accessToken);
            ResponseEntity<Product> exchange = restTemplate.exchange(fooResourceUrl, HttpMethod.POST, httpEntity, Product.class);
            return exchange.getBody();
        });
    }

    public Product edit(String accessToken, ProductVO vo) {
        if(vo.getProductsInventory() != null){
            vo.getProductsInventory().stream().forEach(f -> {
                if(f.getProductsInventoryDetails() != null){
                    f.getProductsInventoryDetails().stream().forEach(g -> g.setProductsInventoryVO(null));
                }
            });
        }
        String body = new GsonBuilder().create().toJson(vo);
        RestTemplate restTemplate = new RestTemplate();
        String fooResourceUrl = configProperties.getRest() + Routes.PRODUCT_EDIT;
        HttpEntity<String> httpEntity = getAuthHttpEntity(body, accessToken);

        return encapsulate(() -> {
            ResponseEntity<Product> exchange = restTemplate.exchange(fooResourceUrl, HttpMethod.POST, httpEntity, Product.class);
            return exchange.getBody();
        });
    }

}