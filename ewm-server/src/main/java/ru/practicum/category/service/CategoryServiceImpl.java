package ru.practicum.category.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exception.ConflictException;

import java.util.List;

@Service
@AllArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class CategoryServiceImpl implements CategoryService {
    public static final Sort SORT_BY_ASC = Sort.by(Sort.Direction.ASC, "id");
    private final CategoryRepository categoryRepository;
    private final EventRepository eventRepository;

    @Override
    @Transactional
    public CategoryDto add(CategoryDtoIn categoryDtoIn) {
        log.info("Добавление новой категории {}", categoryDtoIn);
        Category category = CategoryMapper.makeCategory(categoryDtoIn);
        return CategoryMapper.makeCategoryDto(categoryRepository.save(category));
    }

    @Override
    @Transactional
    public CategoryDto update(Long catId, CategoryDtoIn categoryDtoIn) {
        log.info("Update category {}", categoryDtoIn);
        Category newCategory = categoryRepository.findById(catId).orElseThrow(() ->
                new CategoryException("Category with id=" + catId + " was not found"));
        newCategory.setName(categoryDtoIn.getName());
        return CategoryMapper.makeCategoryDto(newCategory);
    }

    @Override
    @Transactional
    public void delete(Long catId) {
        log.info("Delete category with ID {}", catId);
        categoryRepository.findById(catId).orElseThrow(() ->
                new CategoryException("Category with id=" + catId + " was not found"));

        if (eventRepository.existsAllByCategoryId(catId)) {
            throw new ConflictException("К категории привязаны события");
        } else {
            categoryRepository.deleteById(catId);
        }
    }

    @Override
    public List<CategoryDto> getAll(int from, int size) {
        log.info("Получения всех ктегорий постранично {} и {}", from, size);
        Pageable pageable = PageRequest.of(from / size, size, SORT_BY_ASC);
        Page<Category> page = categoryRepository.findAll(pageable);
        return CategoryMapper.makeCategoryDtoList(page);
    }

    @Override
    public CategoryDto findById(Long catId) {
        log.info("Get category with ID {}", catId);
        Category category = categoryRepository.findById(catId).orElseThrow(() ->
                new CategoryException("Category with id=" + catId + " was not found"));
        return CategoryMapper.makeCategoryDto(category);
    }
}
