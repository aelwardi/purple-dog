package com.purple_dog.mvp.services;

import com.purple_dog.mvp.dao.CategoryRepository;
import com.purple_dog.mvp.dao.ProductRepository;
import com.purple_dog.mvp.dto.CategoryCreateRequest;
import com.purple_dog.mvp.dto.CategoryResponse;
import com.purple_dog.mvp.entities.Category;
import com.purple_dog.mvp.exceptions.CategoryException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    /**
     * Crée une nouvelle catégorie
     */
    public CategoryResponse createCategory(CategoryCreateRequest request) {
        if (request.getName() == null || request.getName().isBlank()) {
            throw new CategoryException("Category name is required");
        }

        Category category = new Category();
        category.setName(request.getName());
        category.setDescription(request.getDescription());
        category.setIconUrl(request.getIconUrl());
        category.setActive(request.getActive() != null ? request.getActive() : true);

        Category saved = categoryRepository.save(category);
        return toResponse(saved);
    }

    /**
     * Récupère une catégorie par ID
     */
    public CategoryResponse getCategory(Long id) {
        return categoryRepository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new CategoryException("Category not found with id: " + id));
    }

    /**
     * Récupère toutes les catégories
     */
    public List<CategoryResponse> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Récupère toutes les catégories actives
     */
    public List<CategoryResponse> getActiveCategories() {
        return categoryRepository.findByActiveTrue().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Met à jour une catégorie
     */
    public CategoryResponse updateCategory(Long id, CategoryCreateRequest request) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryException("Category not found with id: " + id));

        if (request.getName() != null && !request.getName().isBlank()) {
            category.setName(request.getName());
        }
        if (request.getDescription() != null) {
            category.setDescription(request.getDescription());
        }
        if (request.getIconUrl() != null) {
            category.setIconUrl(request.getIconUrl());
        }
        if (request.getActive() != null) {
            category.setActive(request.getActive());
        }

        Category updated = categoryRepository.save(category);
        return toResponse(updated);
    }

    /**
     * Supprime une catégorie
     */
    public void deleteCategory(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new CategoryException("Category not found with id: " + id);
        }
        categoryRepository.deleteById(id);
    }

    private CategoryResponse toResponse(Category category) {
        // Compter le nombre de produits dans cette catégorie
        Long productCount = productRepository.countByCategoryId(category.getId());

        return new CategoryResponse(
                category.getId(),
                category.getName(),
                category.getDescription(),
                category.getIconUrl(),
                category.getActive(),
                productCount);
    }
}
