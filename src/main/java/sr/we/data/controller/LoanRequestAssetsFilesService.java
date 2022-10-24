package sr.we.data.controller;

import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.client.RestTemplate;
import sr.we.shekelflowcore.entity.Business;
import sr.we.shekelflowcore.entity.LoanRequestAssets;
import sr.we.shekelflowcore.entity.LoanRequestAssetsFiles;
import sr.we.shekelflowcore.entity.helper.PagingResult;
import sr.we.shekelflowcore.entity.helper.adapter.ByteArrayAdapter;
import sr.we.shekelflowcore.entity.helper.adapter.LocalDateAdapter;
import sr.we.shekelflowcore.entity.helper.adapter.LocalDateTimeAdapter;
import sr.we.shekelflowcore.entity.helper.vo.LoanRequestAssetsFilesVO;
import sr.we.shekelflowcore.settings.Routes;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Controller
public class LoanRequestAssetsFilesService extends MyController {

    public LoanRequestAssetsFiles get(Long id, String accessToken) {
        RestTemplate restTemplate = new RestTemplate();

        return encapsulate(() -> {
            ResponseEntity<LoanRequestAssetsFiles> exchange = restTemplate.exchange(configProperties.getRest() + Routes.LOAN_REQUEST_ASSETS_DOC_GET + "?id=" + id, HttpMethod.GET, getAuthHttpEntity(accessToken), LoanRequestAssetsFiles.class);
            LoanRequestAssetsFiles body = exchange.getBody();
            return body;
        });
    }

    public PagingResult<LoanRequestAssetsFiles> list(String accessToken, Long loanRequestAssetsId, Long loanRequestId) {


        return encapsulate(() -> {
            RestTemplate restTemplate = new RestTemplate();
            String fooResourceUrl = configProperties.getRest() + Routes.LOAN_REQUEST_ASSETS_DOC_LIST + "?loanRequestId=" + loanRequestId;
            if (loanRequestAssetsId != null) {
                fooResourceUrl += "&loanRequestAssetsId=" + loanRequestAssetsId;
            }

            HttpEntity<String> httpEntity = getAuthHttpEntity(accessToken);
            ResponseEntity<String> exchange = restTemplate.exchange(fooResourceUrl, HttpMethod.GET, httpEntity, String.class);
            String content = exchange.getBody();
            Type collectionType = new TypeToken<PagingResult<LoanRequestAssetsFiles>>(){}.getType();
            PagingResult<LoanRequestAssetsFiles> myJson = new GsonBuilder().registerTypeAdapter(LocalDate.class, new LocalDateAdapter()).registerTypeAdapter(byte[].class, new ByteArrayAdapter())
                    .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter()).create().fromJson(content, collectionType);
            return myJson;
        });
    }

    public LoanRequestAssetsFiles create(String accessToken, LoanRequestAssetsFilesVO vo) {


        return encapsulate(() -> {
            String body = new GsonBuilder().registerTypeAdapter(LocalDate.class, new LocalDateAdapter()).create().toJson(vo);
            RestTemplate restTemplate = new RestTemplate();
            String fooResourceUrl = configProperties.getRest() + Routes.LOAN_REQUEST_ASSETS_DOC_CREATE;
            HttpEntity<String> httpEntity = getAuthHttpEntity(body, accessToken);
            ResponseEntity<LoanRequestAssetsFiles> exchange = restTemplate.exchange(fooResourceUrl, HttpMethod.POST, httpEntity, LoanRequestAssetsFiles.class);
            return exchange.getBody();
        });
    }

    public LoanRequestAssetsFiles edit(String accessToken, LoanRequestAssetsFilesVO vo) {
        String body = new GsonBuilder().create().toJson(vo);
        RestTemplate restTemplate = new RestTemplate();
        String fooResourceUrl = configProperties.getRest() + Routes.LOAN_REQUEST_ASSETS_DOC_EDIT;
        HttpEntity<String> httpEntity = getAuthHttpEntity(body, accessToken);

        return encapsulate(() -> {
            ResponseEntity<LoanRequestAssetsFiles> exchange = restTemplate.exchange(fooResourceUrl, HttpMethod.POST, httpEntity, LoanRequestAssetsFiles.class);
            return exchange.getBody();
        });
    }


}