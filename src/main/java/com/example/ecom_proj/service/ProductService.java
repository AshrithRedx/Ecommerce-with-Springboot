package com.example.ecom_proj.service;

import com.example.ecom_proj.model.Product;
import com.example.ecom_proj.repo.ProductRepo;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.hibernate.ResourceClosedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.stereotype.Service;


import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class ProductService {
    @Autowired
    private ProductRepo repo;


    public List<Product> getAllProducts() {
        return repo.findAll();
    }

    public Product getProductById(int id) {
        return repo.findById(id).get();
    }

    public Product addProduct(Product product, MultipartFile imageFile) throws IOException {
        product.setImageName(imageFile.getOriginalFilename());
        product.setImageType(imageFile.getContentType());
        product.setImageData(imageFile.getBytes());
        return repo.save(product);
    }

    public List<Product> searchProducts(String keyword) {
        return repo.searchProducts(keyword);
    }

    public Product updateProduct(int id, Product newProduct, MultipartFile imageFile) throws IOException {
    Product existingProduct = repo.findById(id).orElse(null);
    if (existingProduct == null) return null;
    
    existingProduct.setName(newProduct.getName());
    existingProduct.setDescription(newProduct.getDescription());
    existingProduct.setBrand(newProduct.getBrand());
    existingProduct.setCategory(newProduct.getCategory());
    existingProduct.setPrice(newProduct.getPrice());
    existingProduct.setStockQuantity(newProduct.getStockQuantity());
    existingProduct.setProduct_available(newProduct.isProduct_available());
    existingProduct.setReleaseDate(newProduct.getReleaseDate());

        if (imageFile != null && !imageFile.isEmpty()) {
        existingProduct.setImageName(imageFile.getOriginalFilename());
        existingProduct.setImageType(imageFile.getContentType());
        existingProduct.setImageData(imageFile.getBytes());
    }
    return repo.save(existingProduct);
}

    public void deleteProduct(int id) {
        repo.deleteById(id);
    }

    @Transactional
    public void reduceStock(int productId, int quantity) throws IllegalArgumentException {
        Product product = repo.findById(productId)
                .orElseThrow(() -> new NoSuchElementException("Product not found with id: " + productId));
        int currentStock = product.getStockQuantity();
        if (currentStock < quantity) {
            throw new IllegalArgumentException("Insufficient stock");
        }
        product.setStockQuantity(currentStock - quantity);
        repo.save(product);
    }

}
