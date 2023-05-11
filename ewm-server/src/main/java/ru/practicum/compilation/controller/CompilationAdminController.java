package ru.practicum.compilation.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.CompilationDtoIn;
import ru.practicum.compilation.dto.CompilationDtoUpdate;
import ru.practicum.compilation.service.CompilationService;

import javax.validation.constraints.Positive;

@RestController
@RequestMapping("/admin/compilations")
@Validated
@AllArgsConstructor
public class CompilationAdminController {
    private final CompilationService compilationService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CompilationDto add(@Validated @RequestBody CompilationDtoIn compilationDtoIn) {
        return compilationService.add(compilationDtoIn);
    }

    @PatchMapping("/{compId}")
    public CompilationDto update(@RequestBody CompilationDtoUpdate compilationDtoUpdate,
                                 @PathVariable @Positive Long compId) {
        return compilationService.update(compilationDtoUpdate, compId);
    }

    @DeleteMapping("/{comId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long comId) {
        compilationService.delete(comId);
    }
}
