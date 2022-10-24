package sr.we.data.controller;

import com.google.gson.reflect.TypeToken;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.client.RestTemplate;
import sr.we.shekelflowcore.entity.*;
import sr.we.shekelflowcore.entity.helper.PagingResult;
import sr.we.shekelflowcore.settings.Routes;

import java.util.Arrays;

@Controller
public class PojoService extends MyController {


    public PagingResult<Country> listCountry(String accessToken) {
        RestTemplate restTemplate = new RestTemplate();
        String fooResourceUrl = configProperties.getRest() + Routes.COUNTRY_LIST;
        HttpEntity<String> httpEntity = getAuthHttpEntity(accessToken);

        return encapsulate(() -> {
            ResponseEntity<String> exchange = restTemplate.exchange(fooResourceUrl, HttpMethod.GET, httpEntity, String.class);
            return transform(exchange,new TypeToken<PagingResult<Country>>(){}.getType());
        });
    }

    public PagingResult<PaymentMethod> listPaymentMethod(String accessToken) {
        RestTemplate restTemplate = new RestTemplate();
        String fooResourceUrl = configProperties.getRest() + Routes.PAYMENT_METHOD_LIST;
        HttpEntity<String> httpEntity = getAuthHttpEntity(accessToken);

        return encapsulate(() -> {
            ResponseEntity<String> exchange = restTemplate.exchange(fooResourceUrl, HttpMethod.GET, httpEntity, String.class);
            return transform(exchange,new TypeToken<PagingResult<PaymentMethod>>(){}.getType());
        });
    }

    public PagingResult<Currency> listCurrency(String accessToken) {
        RestTemplate restTemplate = new RestTemplate();
        String fooResourceUrl = configProperties.getRest() + Routes.CURRENCY_LIST;
        HttpEntity<String> httpEntity = getAuthHttpEntity(accessToken);

        return encapsulate(() -> {
            ResponseEntity<String> exchange = restTemplate.exchange(fooResourceUrl, HttpMethod.GET, httpEntity, String.class);
            return transform(exchange,new TypeToken<PagingResult<Currency>>(){}.getType());
        });
    }

    public PagingResult<BusinessType> listBusinessType(String accessToken) {
        RestTemplate restTemplate = new RestTemplate();
        String fooResourceUrl = configProperties.getRest() + Routes.BUSINESS_TYPE_LIST;
        HttpEntity<String> httpEntity = getAuthHttpEntity(accessToken);

        return encapsulate(() -> {
            ResponseEntity<String> exchange = restTemplate.exchange(fooResourceUrl, HttpMethod.GET, httpEntity, String.class);
            return transform(exchange,new TypeToken<PagingResult<BusinessType>>(){}.getType());
        });
    }

    public PagingResult<BusinessOrganisationType> listBusinessOrganisationType(String accessToken) {
        RestTemplate restTemplate = new RestTemplate();
        String fooResourceUrl = configProperties.getRest() + Routes.BUSINESS_ORGANISATION_TYPE_LIST;
        HttpEntity<String> httpEntity = getAuthHttpEntity(accessToken);

        return encapsulate(() -> {
            ResponseEntity<String> exchange = restTemplate.exchange(fooResourceUrl, HttpMethod.GET, httpEntity, String.class);
            return transform(exchange,new TypeToken<PagingResult<BusinessOrganisationType>>(){}.getType());
        });
    }

    public PagingResult<NumericVal> listNumericVal(String accessToken, String type) {
        return encapsulate(() -> {
            RestTemplate restTemplate = new RestTemplate();
            String fooResourceUrl = configProperties.getRest() + Routes.NUMERIC_VAL_LIST;
            if (StringUtils.isBlank(type)) {
                fooResourceUrl += "?type=" + type;
            }
            HttpEntity<String> httpEntity = getAuthHttpEntity(accessToken);
            ResponseEntity<String> exchange = restTemplate.exchange(fooResourceUrl, HttpMethod.GET, httpEntity, String.class);
            return transform(exchange,new TypeToken<PagingResult<NumericVal>>(){}.getType());
        });
    }


    public PagingResult<AssetType> listAssetTypes(String accessToken) {
        RestTemplate restTemplate = new RestTemplate();
        String fooResourceUrl = configProperties.getRest() + Routes.ASSET_TYPE_LIST;
        HttpEntity<String> httpEntity = getAuthHttpEntity(accessToken);

        return encapsulate(() -> {
            ResponseEntity<String> exchange = restTemplate.exchange(fooResourceUrl, HttpMethod.GET, httpEntity, String.class);
            return transform(exchange,new TypeToken<PagingResult<AssetType>>(){}.getType());
        });
    }
}