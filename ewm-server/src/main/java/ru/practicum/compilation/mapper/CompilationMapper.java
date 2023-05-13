package ru.practicum.compilation.mapper;

import lombok.experimental.UtilityClass;
import org.springframework.data.domain.Page;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.CompilationDtoIn;
import ru.practicum.compilation.model.Compilation;
import ru.practicum.event.mapper.EventMapper;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@UtilityClass
public class CompilationMapper {
    public Compilation makeCompilations(CompilationDtoIn compilationDtoIn) {
        return Compilation.builder()
                .pinned(compilationDtoIn.isPinned())
                .title(compilationDtoIn.getTitle())
                .build();
    }

    public CompilationDto makeCompilationDto(Compilation compilation) {
        return CompilationDto.builder()
                .id(compilation.getId())
                .events(compilation.getEvents() != null && !compilation.getEvents().isEmpty() ?
                        EventMapper.makeSetEventDto(compilation.getEvents()) : Set.of())
                .pinned(compilation.getPinned())
                .title(compilation.getTitle())
                .build();
    }

    public List<CompilationDto> makeCompilationDtoList(Page<Compilation> compilations) {
        return compilations.stream().map(CompilationMapper::makeCompilationDto).collect(Collectors.toList());
    }
}
