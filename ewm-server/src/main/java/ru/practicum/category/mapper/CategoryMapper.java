package ru.practicum.category.mapper;

import lombok.experimental.UtilityClass;
import org.springframework.data.domain.Page;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.dto.CategoryDtoIn;
import ru.practicum.category.model.Category;

import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class CategoryMapper {
    public Category makeCategory(CategoryDtoIn categoryDtoIn) {
        return Category.builder()
                .name(categoryDtoIn.getName()).build();
    }

    public CategoryDto makeCategoryDto(Category category) {
        return CategoryDto.builder()
                .id(category.getId())
                .name(category.getName())
                .build();
    }

    public List<CategoryDto> makeCategoryDtoList(Page<Category> categories) {
        return categories.stream().map(CategoryMapper::makeCategoryDto).collect(Collectors.toList());
    }
}
