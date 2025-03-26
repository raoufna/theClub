package com.unimib.wardrobe.repository;

public interface IProductRepository {
    void fetchProduct(String searchTerm, int page, long lastUpdate);

    void getFavoritsProduct();

}
