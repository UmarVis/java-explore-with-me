package ru.practicum.category.service;

import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.dto.CategoryDtoIn;

import java.util.List;

public interface CategoryService {
    CategoryDto add(CategoryDtoIn categoryDtoIn);

    CategoryDto update(Long catId, CategoryDtoIn categoryDtoIn);

    void delete(Long catId);

    List<CategoryDto> getAll(int from, int size);

    CategoryDto findById(Long catId);
}
