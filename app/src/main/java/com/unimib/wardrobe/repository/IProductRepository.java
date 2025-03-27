package com.unimib.wardrobe.repository;

import com.unimib.wardrobe.model.Product;

public interface IProductRepository {

    void fetchProduct(String searchTerm, int page, long lastUpdate);
    void getFavoriteProduct();
    void deleteFavoriteProduct();
    void updateProduct(Product product);

}
