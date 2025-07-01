package com.project.demo.rest.category;


import com.project.demo.logic.entity.category.Category;
import com.project.demo.logic.entity.category.CategoryRepository;
import com.project.demo.logic.entity.http.GlobalResponseHandler;
import com.project.demo.logic.entity.http.Meta;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/category")
public class CategoryController {

    @Autowired
    private CategoryRepository categoryRepository;

    @PostMapping
    @PreAuthorize("isAuthenticated() && hasAnyRole('SUPER_ADMIN')")
    public ResponseEntity<?> addCategory(@RequestBody Category category, HttpServletRequest request) {
        Category categorySaved = categoryRepository.save(category);
        return new GlobalResponseHandler().handleResponse(
                "Category successfully saved",
                categorySaved,
                HttpStatus.OK,
                request);
    }

    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated() && hasAnyRole('SUPER_ADMIN')")
    public ResponseEntity<?> updateCategory(@RequestBody Category category, @PathVariable Long id, HttpServletRequest request) {
        Category existingCategory = categoryRepository.findById(id)
                .orElse(null);

        if (existingCategory == null) {
            return new GlobalResponseHandler().handleResponse(
                    "Category not found to update",
                    HttpStatus.NOT_FOUND,
                    request);
        }


        existingCategory.setName(category.getName());
        existingCategory.setDescription(category.getDescription());

        Category categorySaved = categoryRepository.save(existingCategory);

        return new GlobalResponseHandler().handleResponse(
                "Category successfully updated",
                categorySaved,
                HttpStatus.OK,
                request);
    }

    @DeleteMapping("/{categoryid}")
    @PreAuthorize("isAuthenticated() && hasAnyRole('SUPER_ADMIN')")
    public ResponseEntity<?> deleteCategory(@PathVariable Long categoryid, HttpServletRequest request) {
        Optional<Category> foundcategory = categoryRepository.findById(categoryid);
        if(foundcategory.isPresent()) {
            categoryRepository.deleteById(categoryid);
            return new GlobalResponseHandler().handleResponse(
                    "Category successfully deleted",
                    HttpStatus.OK,
                    request);
        } else {
            return new GlobalResponseHandler().handleResponse(
                    "Category with id " + categoryid + " not found",
                    HttpStatus.NOT_FOUND,
                    request);
        }
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getAllCategory(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest request)
    {
        Pageable pageable = PageRequest.of(page-1, size);
        Page<Category> categoryPage = categoryRepository.findAll(pageable);
        Meta meta = new Meta(request.getMethod(), request.getRequestURL().toString());
        meta.setTotalPages(categoryPage.getTotalPages());
        meta.setTotalElements(categoryPage.getTotalElements());
        meta.setPageNumber(categoryPage.getNumber() + 1);
        meta.setPageSize(categoryPage.getSize());

        return new GlobalResponseHandler().handleResponse(
                "Category retrieved successfully",
                categoryPage.getContent(),
                HttpStatus.OK,
                meta);
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getCategoryById(@PathVariable Long id, HttpServletRequest request) {
        Optional<Category> foundCategory = categoryRepository.findById(id);
        if(foundCategory.isPresent()) {
            return new GlobalResponseHandler().handleResponse(
                    "Categoria recuperada de manera exitosa",
                    foundCategory.get(),
                    HttpStatus.OK,
                    request);
        } else {
            return new GlobalResponseHandler().handleResponse(
                    "Category with id " + id + " no found",
                    HttpStatus.NOT_FOUND,
                    request);
        }
    }
}
