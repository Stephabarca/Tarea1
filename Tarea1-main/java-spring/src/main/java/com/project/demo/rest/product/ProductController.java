package com.project.demo.rest.product;

import com.project.demo.logic.entity.category.Category;
import com.project.demo.logic.entity.http.GlobalResponseHandler;
import com.project.demo.logic.entity.http.Meta;
import com.project.demo.logic.entity.product.Product;
import com.project.demo.logic.entity.product.ProductRepository;
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
@RequestMapping("/products")
public class ProductController {

    @Autowired
    private ProductRepository productRepository;

    @PostMapping
    @PreAuthorize("isAuthenticated() && hasAnyRole('SUPER_ADMIN')")
    public ResponseEntity<?> addProduct(@RequestBody Product product, HttpServletRequest request) {
        Product savedProduct = productRepository.save(product);
        return new GlobalResponseHandler().handleResponse(
                "Prodcut successfully saved",
                savedProduct,
                HttpStatus.OK,
                request);
    }

    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated() && hasAnyRole('SUPER_ADMIN')")
    public ResponseEntity<?> updateProduct(@RequestBody Product product, @PathVariable Long id ,HttpServletRequest request) {
        Product existingProduct = productRepository.findById(id).orElse(null);

        if(existingProduct == null) {
            return new GlobalResponseHandler().handleResponse(
                    "Product not found to update",
                    HttpStatus.NOT_FOUND,
                    request
            );
        }

        existingProduct.setName(product.getName());
        existingProduct.setDescription(product.getDescription());
        existingProduct.setCategory(product.getCategory());
        existingProduct.setPrice(product.getPrice());
        existingProduct.setQuantity(product.getQuantity());

        Product productSaved = productRepository.save(existingProduct);

        return new GlobalResponseHandler().handleResponse(
                "Product successfully update",
                productSaved,
                HttpStatus.OK,
                request
        );
    }


@DeleteMapping("/{productId}")
@PreAuthorize("isAuthenticated() && hasAnyRole('SUPER_ADMIN')")
public ResponseEntity<?> deleteProduct(@PathVariable Long productId, HttpServletRequest request) {
    Optional<Product> foundProduct = productRepository.findById(productId);
    if(foundProduct.isPresent()){
        productRepository.deleteById(productId);
        return new GlobalResponseHandler().handleResponse(
                "Product successfully deleted",
                foundProduct.get(),
                HttpStatus.OK,
                request);
    } else {
        return new GlobalResponseHandler().handleResponse(
                "Product with id " + productId + " not found",
                HttpStatus.NOT_FOUND,
                request);
    }
}

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getAllProducts(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest request)
    {
        Pageable pageable = PageRequest.of(page-1, size);
        Page<Product> productsPage = productRepository.findAll(pageable);
        com.project.demo.logic.entity.http.Meta meta = new Meta(request.getMethod(), request.getRequestURL().toString());
        meta.setTotalPages(productsPage.getTotalPages());
        meta.setTotalElements(productsPage.getTotalElements());
        meta.setPageNumber(productsPage.getNumber() + 1);
        meta.setPageSize(productsPage.getSize());

        return new GlobalResponseHandler().handleResponse(
                "Product retrieved successfully",
                productsPage.getContent(),
                HttpStatus.OK,
                meta);
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getProductById(@PathVariable Long id, HttpServletRequest request) {
        Optional<Product> foundProduct = productRepository.findById(id);
        if(foundProduct.isPresent()) {
            return new GlobalResponseHandler().handleResponse(
                    "Producto recuperado de manera exitosa",
                    foundProduct.get(),
                    HttpStatus.OK,
                    request);
        } else {
            return new GlobalResponseHandler().handleResponse(
                    "Product with id " + id + " no found",
                    HttpStatus.NOT_FOUND,
                    request);
        }
    }
}
