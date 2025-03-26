package com.unimib.wardrobe.repository;

public interface IProductReposiitory {
    void fetchProduct(String country, int page, long lastUpdate);

    void getFavoritsProduct();

}
