package com.unimib.wardrobe.repository;

import com.unimib.wardrobe.model.Product;

public class ProductMockRepository implements IProductRepository{
    @Override
    public void fetchProduct(String country, int page, long lastUpdate) {

    }

    @Override
    public void getFavoriteProduct() {
    }
    @Override
    public void deleteFavoriteProduct() {
    }

    @Override
    public void updateProduct(Product product) {
    }
}
