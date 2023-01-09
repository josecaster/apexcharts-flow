package sr.we.data.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.client.RestTemplate;
import sr.we.shekelflowcore.entity.Business;
import sr.we.shekelflowcore.entity.helper.PagingResult;
import sr.we.shekelflowcore.entity.helper.adapter.ByteArrayAdapter;
import sr.we.shekelflowcore.entity.helper.adapter.LocalDateAdapter;
import sr.we.shekelflowcore.entity.helper.adapter.LocalDateTimeAdapter;
import sr.we.shekelflowcore.entity.helper.vo.BusinessVO;
import sr.we.shekelflowcore.settings.Routes;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Controller
public class ImagesService extends MyController{

    public byte[] get(String id, String reference){
        RestTemplate restTemplate = new RestTemplate();

        return encapsulate(() -> {
            String url = configProperties.getRest() + Routes.IMAGES_GET ;
                url+= "?referenceId=" + id+"&reference="+reference;
            ResponseEntity<byte[]> exchange = restTemplate.exchange(url, HttpMethod.GET, getHttpEntity(), byte[].class);
            byte[] body = exchange.getBody();
            return body;
        });
    }


}