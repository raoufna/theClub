package com.unimib.wardrobe.source;

import com.unimib.wardrobe.model.Product;

import java.util.List;

public abstract class BaseProductLocalDataSource {
    protected ProductCallback productCallback;

    public void setProductCallback(ProductCallback productCallback) {
        this.productCallback = productCallback;
    }

    public abstract void getProducts();

    public abstract void getFavoriteProducts();

    public abstract void updateProduct(Product product);

    public abstract void deleteFavoriteProducts();

    public abstract void insertProducts(List<Product> productList);
}
