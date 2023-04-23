package ru.practicum.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.dto.DtoHitIn;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;


@Service
public class StatClient extends BaseClient {

    @Autowired
    public StatClient(@Value("${stats-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> createEndpointHit(DtoHitIn dtoHitIn) {
        return post("/hit", dtoHitIn);
    }

    public ResponseEntity<Object> getStatsEndpoint(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        Map<String, Object> parameters = Map.of("uris", String.join(",", uris));
        return get("stats?start={start}&end={end}&uris={uris}&unique={unique}", parameters);
    }
}
