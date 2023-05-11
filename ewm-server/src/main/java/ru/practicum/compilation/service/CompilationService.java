package ru.practicum.compilation.service;

import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.CompilationDtoIn;
import ru.practicum.compilation.dto.CompilationDtoUpdate;

import java.util.List;

public interface CompilationService {
    CompilationDto add(CompilationDtoIn compilationDtoIn);

    CompilationDto update(CompilationDtoUpdate compilationDtoUpdate, Long compId);

    void delete(Long comId);

    List<CompilationDto> getAll(Boolean pinned, int from, int size);

    CompilationDto getById(Long comId);
}
