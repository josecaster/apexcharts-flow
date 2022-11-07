package sr.we.data.controller;

import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.client.RestTemplate;
import sr.we.shekelflowcore.entity.JournalsEntry;
import sr.we.shekelflowcore.entity.PosHeader;
import sr.we.shekelflowcore.entity.helper.PagingResult;
import sr.we.shekelflowcore.entity.helper.adapter.LocalDateAdapter;
import sr.we.shekelflowcore.entity.helper.vo.JournalsEntryVO;
import sr.we.shekelflowcore.settings.Routes;

import java.time.LocalDate;

@Controller
public class JournalEntryService extends MyController {

    public PagingResult<JournalsEntry> list(JournalsEntryVO vo, String accessToken) {
        RestTemplate restTemplate = new RestTemplate();
        String fooResourceUrl = configProperties.getRest() + Routes.JOURNAL_ENTRY_LIST;
        String body = new GsonBuilder().registerTypeAdapter(LocalDate.class, new LocalDateAdapter()).create().toJson(vo);
        HttpEntity<String> httpEntity = getAuthHttpEntity(body, accessToken);
        return encapsulate(() -> {
            ResponseEntity<String> exchange = restTemplate.exchange(fooResourceUrl, HttpMethod.POST, httpEntity, String.class);
            return transform(exchange, new TypeToken<PagingResult<JournalsEntry>>() {
            }.getType());
        });
    }


}