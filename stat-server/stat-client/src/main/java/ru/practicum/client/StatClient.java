package ru.practicum.client;

import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.dto.DtoHitIn;
import ru.practicum.dto.DtoStatOut;

import java.util.Arrays;
import java.util.List;
import java.util.Map;


@Service
public class StatClient extends BaseClient {

    @Autowired
    public StatClient(@Value("${statistic-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public DtoStatOut createEndpointHit(DtoHitIn dtoHitIn) {
        Gson gson = new Gson();
        ResponseEntity<Object> objectResponseEntity = post("/hit", dtoHitIn);
        String json = gson.toJson(objectResponseEntity.getBody());

        return gson.fromJson(json, DtoStatOut.class);
    }

    public List<DtoStatOut> getStatsEndpoint(String start, String end, List<String> uris, Boolean unique) {
        Gson gson = new Gson();
        Map<String, Object> parameters = Map.of(
                "uris", String.join(",", uris),
                "unique", unique,
                "start", start,
                "end", end
        );
        ResponseEntity<Object> objectResponseEntity =
                get("/stats?start={start}&end={end}&uris={uris}&unique={unique}", parameters);
        String json = gson.toJson(objectResponseEntity.getBody());
        DtoStatOut[] viewStatDtoArray = gson.fromJson(json, DtoStatOut[].class);

        return Arrays.asList(viewStatDtoArray);
    }
}
