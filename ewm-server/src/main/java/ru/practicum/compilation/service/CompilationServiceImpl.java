package ru.practicum.compilation.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.CompilationDtoIn;
import ru.practicum.compilation.exception.CompilationException;
import ru.practicum.compilation.mapper.CompilationMapper;
import ru.practicum.compilation.model.Compilation;
import ru.practicum.compilation.repository.CompilationRepository;
import ru.practicum.event.model.Event;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exception.NotFoundException;

import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class CompilationServiceImpl implements CompilationService {
    public static final Sort SORT_BY_ASC = Sort.by(Sort.Direction.ASC, "id");
    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;

    @Override
    @Transactional
    public CompilationDto add(CompilationDtoIn compilationDtoIn) {
        log.info("Add new compilation {}", compilationDtoIn);
        Compilation compilation = CompilationMapper.makeCompilations(compilationDtoIn);
        if (compilationDtoIn.getEvents() != null && !compilationDtoIn.getEvents().isEmpty()) {
            compilation.setEvents(new HashSet<>(getEvent(compilationDtoIn.getEvents())));
        }
        return CompilationMapper.makeCompilationDto(compilationRepository.save(compilation));
    }

    @Override
    @Transactional
    public CompilationDto update(CompilationDtoIn compilationDtoIn, Long compId) {
        log.info("Update compilation {}", compilationDtoIn);
        Compilation compilationUpdate = get(compId);
        if (compilationDtoIn.getPinned() != null) {
            compilationUpdate.setPinned(compilationDtoIn.getPinned());
        }
        if (compilationDtoIn.getTitle() != null && !compilationDtoIn.getTitle().isBlank()) {
            compilationUpdate.setTitle(compilationDtoIn.getTitle());
        }
        List<Event> eventList = eventRepository.findAllById(compilationDtoIn.getEvents());
        compilationUpdate.setEvents(new HashSet<>(eventList));
        return CompilationMapper.makeCompilationDto(compilationUpdate);
    }

    @Override
    @Transactional
    public void delete(Long comId) {
        log.info("Delete compilation with ID {}", comId);
        get(comId);
        compilationRepository.deleteById(comId);
    }

    @Override
    public List<CompilationDto> getAll(Boolean pinned, int from, int size) {
        log.info("Get all compilation with pinned {}, from {}, size {}", pinned, from, size);
        Pageable pageable = PageRequest.of(from/size, size, SORT_BY_ASC);
        return pinned != null ?
                CompilationMapper.makeCompilationDtoList(compilationRepository.findAllByPinnedEquals(pinned, pageable))
                : CompilationMapper.makeCompilationDtoList(compilationRepository.findAll(pageable));
    }

    @Override
    public CompilationDto getById(Long comId) {
        log.info("Get compilation with ID {}", comId);
        return CompilationMapper.makeCompilationDto(get(comId));
    }

    private Compilation get(Long compId) {
        return compilationRepository.findById(compId)
                .orElseThrow(() -> new CompilationException("Category with " + compId + " was not found"));
    }

    private List<Event> getEvent(List<Long> eventIds) {
        List<Event> eventList = eventRepository.findAllById(eventIds);
        if (eventList.isEmpty() || eventList.size() != eventIds.size()) {
            throw new NotFoundException("События не найдены");
        } else {
            return eventList;
        }
    }
}
