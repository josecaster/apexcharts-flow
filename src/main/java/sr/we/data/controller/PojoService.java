package sr.we.data.controller;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.client.RestTemplate;
import sr.we.shekelflowcore.entity.*;
import sr.we.shekelflowcore.settings.Routes;

import java.util.Arrays;
import java.util.List;

@Controller
public class PojoService extends MyController {


    public List<Country> listCountry(String accessToken) {
        RestTemplate restTemplate = new RestTemplate();
        String fooResourceUrl = configProperties.getRest() + Routes.COUNTRY_LIST;
        HttpEntity<String> httpEntity = getAuthHttpEntity(accessToken);

        return encapsulate(() -> {
            ResponseEntity<Country[]> exchange = restTemplate.exchange(fooResourceUrl, HttpMethod.GET, httpEntity, Country[].class);
            return Arrays.asList(exchange.getBody());
        });
    }

    public List<PaymentMethod> listPaymentMethod(String accessToken) {
        RestTemplate restTemplate = new RestTemplate();
        String fooResourceUrl = configProperties.getRest() + Routes.PAYMENT_METHOD_LIST;
        HttpEntity<String> httpEntity = getAuthHttpEntity(accessToken);

        return encapsulate(() -> {
            ResponseEntity<PaymentMethod[]> exchange = restTemplate.exchange(fooResourceUrl, HttpMethod.GET, httpEntity, PaymentMethod[].class);
            return Arrays.asList(exchange.getBody());
        });
    }

    public List<Currency> listCurrency(String accessToken) {
        RestTemplate restTemplate = new RestTemplate();
        String fooResourceUrl = configProperties.getRest() + Routes.CURRENCY_LIST;
        HttpEntity<String> httpEntity = getAuthHttpEntity(accessToken);

        return encapsulate(() -> {
            ResponseEntity<Currency[]> exchange = restTemplate.exchange(fooResourceUrl, HttpMethod.GET, httpEntity, Currency[].class);
            return Arrays.asList(exchange.getBody());
        });
    }

    public List<BusinessType> listBusinessType(String accessToken) {
        RestTemplate restTemplate = new RestTemplate();
        String fooResourceUrl = configProperties.getRest() + Routes.BUSINESS_TYPE_LIST;
        HttpEntity<String> httpEntity = getAuthHttpEntity(accessToken);

        return encapsulate(() -> {
            ResponseEntity<BusinessType[]> exchange = restTemplate.exchange(fooResourceUrl, HttpMethod.GET, httpEntity, BusinessType[].class);
            return Arrays.asList(exchange.getBody());
        });
    }

    public List<BusinessOrganisationType> listBusinessOrganisationType(String accessToken) {
        RestTemplate restTemplate = new RestTemplate();
        String fooResourceUrl = configProperties.getRest() + Routes.BUSINESS_ORGANISATION_TYPE_LIST;
        HttpEntity<String> httpEntity = getAuthHttpEntity(accessToken);

        return encapsulate(() -> {
            ResponseEntity<BusinessOrganisationType[]> exchange = restTemplate.exchange(fooResourceUrl, HttpMethod.GET, httpEntity, BusinessOrganisationType[].class);
            return Arrays.asList(exchange.getBody());
        });
    }

    public List<NumericVal> listNumericVal(String accessToken, String type) {
        return encapsulate(() -> {
            RestTemplate restTemplate = new RestTemplate();
            String fooResourceUrl = configProperties.getRest() + Routes.NUMERIC_VAL_LIST;
            if (StringUtils.isBlank(type)) {
                fooResourceUrl += "?type=" + type;
            }
            HttpEntity<String> httpEntity = getAuthHttpEntity(accessToken);
            ResponseEntity<NumericVal[]> exchange = restTemplate.exchange(fooResourceUrl, HttpMethod.GET, httpEntity, NumericVal[].class);
            return Arrays.asList(exchange.getBody());
        });
    }


    public List<AssetType> listAssetTypes(String accessToken) {
        RestTemplate restTemplate = new RestTemplate();
        String fooResourceUrl = configProperties.getRest() + Routes.ASSET_TYPE_LIST;
        HttpEntity<String> httpEntity = getAuthHttpEntity(accessToken);

        return encapsulate(() -> {
            ResponseEntity<AssetType[]> exchange = restTemplate.exchange(fooResourceUrl, HttpMethod.GET, httpEntity, AssetType[].class);
            return Arrays.asList(exchange.getBody());
        });
    }
}