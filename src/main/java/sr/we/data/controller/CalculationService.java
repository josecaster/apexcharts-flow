package sr.we.data.controller;

import com.google.gson.GsonBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.client.RestTemplate;
import sr.we.shekelflowcore.entity.helper.adapter.CalculationParam;
import sr.we.shekelflowcore.entity.helper.adapter.CalculationResult;
import sr.we.shekelflowcore.settings.Routes;

@Controller
public class CalculationService extends MyController {

    public CalculationResult calculate(String accessToken, CalculationParam vo) {
        String body = new GsonBuilder().create().toJson(vo);
        RestTemplate restTemplate = new RestTemplate();
        String fooResourceUrl = configProperties.getRest() + Routes.CALCULATE_GET;
        HttpEntity<String> httpEntity = getAuthHttpEntity(body, accessToken);

        return encapsulate(() -> {
            ResponseEntity<CalculationResult> exchange = restTemplate.exchange(fooResourceUrl, HttpMethod.POST, httpEntity, //
                    CalculationResult.class);
            return exchange.getBody();
        });

    }


}