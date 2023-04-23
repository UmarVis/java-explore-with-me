package ru.practicum.category.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.dto.CategoryDtoIn;
import ru.practicum.category.service.CategoryService;

import javax.validation.constraints.Positive;

@RestController
@AllArgsConstructor
@Validated
@RequestMapping("/admin/categories")
public class CategoryAdminController {
    private final CategoryService categoryService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDto add(@Validated @RequestBody CategoryDtoIn categoryDtoIn) {
        return categoryService.add(categoryDtoIn);
    }

    @PatchMapping("/{catId}")
    @ResponseStatus(HttpStatus.OK)
    public CategoryDto update(@Positive @PathVariable Long catId,
                              @Validated @RequestBody CategoryDtoIn categoryDtoIn) {
        return categoryService.update(catId, categoryDtoIn);
    }

    @DeleteMapping("/{catId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long catId) {
        categoryService.delete(catId);
    }
}
