package com.unimib.wardrobe.model;

import java.util.List;

public class ProductAPIResponse {
    public List<Product> products;

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }
}
