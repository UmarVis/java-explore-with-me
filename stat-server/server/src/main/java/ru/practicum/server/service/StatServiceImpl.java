package ru.practicum.server.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.DtoHitIn;
import ru.practicum.dto.DtoStatOut;
import ru.practicum.server.mapper.MapperHit;
import ru.practicum.server.repository.StatRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StatServiceImpl implements StatService {
    private final StatRepository statRepository;

    @Override
    @Transactional
    public void addHit(DtoHitIn dtoHitIn) {
        statRepository.save(MapperHit.makeHit(dtoHitIn));
    }

    @Override
    public List<DtoStatOut> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        if (uris == null || uris.isEmpty()) {
            return unique ? statRepository.findAllDistinctStat(start, end)
                    : statRepository.findAllStat(start, end);
        } else {
            return unique ? statRepository.findDistinctStat(start, end, uris)
                    : statRepository.findStat(start, end, uris);
        }
    }
}
