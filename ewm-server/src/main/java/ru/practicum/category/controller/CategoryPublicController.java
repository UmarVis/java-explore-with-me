package ru.practicum.category.controller;

import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.service.CategoryService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@AllArgsConstructor
@Validated
@RequestMapping("/categories")
public class CategoryPublicController {
    public final CategoryService categoryService;

    @GetMapping
    public List<CategoryDto> getAll(@PositiveOrZero @RequestParam(defaultValue = "0") int from,
                                    @Positive @RequestParam(defaultValue = "10") int size) {
        return categoryService.getAll(from, size);
    }

    @GetMapping("/{catId}")
    public CategoryDto findById(@PathVariable Long catId) {
        return categoryService.findById(catId);
    }
}
