package ru.practicum.category.service;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.dto.CategoryDtoIn;
import ru.practicum.category.exception.CategoryException;
import ru.practicum.category.mapper.CategoryMapper;
import ru.practicum.category.model.Category;
import ru.practicum.category.repository.CategoryRepository;

import java.util.List;

@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class CategoryServiceImpl implements CategoryService {
    public static final Sort SORT_BY_ASC = Sort.by(Sort.Direction.ASC, "id");
    private final CategoryRepository categoryRepository;

    @Override
    @Transactional
    public CategoryDto add(CategoryDtoIn categoryDtoIn) {
        Category category = CategoryMapper.makeCategory(categoryDtoIn);
        return CategoryMapper.makeCategoryDto(categoryRepository.save(category));
    }

    @Override
    @Transactional
    public CategoryDto update(Long catId, CategoryDtoIn categoryDtoIn) {
        Category newCategory = categoryRepository.findById(catId).orElseThrow(() ->
                new CategoryException("Category with id=" + catId + " was not found"));
        newCategory.setName(categoryDtoIn.getName());
        return CategoryMapper.makeCategoryDto(newCategory);
    }

    @Override
    @Transactional
    public void delete(Long catId) {
        //todo
        Category deleteCategory = categoryRepository.findById(catId).orElseThrow(() ->
                new CategoryException("Category with id=" + catId + " was not found"));
        categoryRepository.deleteById(catId);
    }

    @Override
    public List<CategoryDto> getAll(int from, int size) {
        Pageable pageable = PageRequest.of(from/size, size, SORT_BY_ASC);
        Page<Category> page = categoryRepository.findAll(pageable);
        return CategoryMapper.makeCategoryDtoList(page);
    }

    @Override
    public CategoryDto findById(Long catId) {
        Category category = categoryRepository.findById(catId).orElseThrow(() ->
                new CategoryException("Category with id=" + catId + " was not found"));
        return CategoryMapper.makeCategoryDto(category);
    }
}
