package com.unimib.wardrobe.model;

import java.util.List;

public class ProductCategory {
    private String categoryTitle;
    private List<Product> productList;

    public ProductCategory(String categoryTitle, List<Product> productList) {
        this.categoryTitle = categoryTitle;
        this.productList = productList;
    }

    public String getCategoryTitle() {
        return categoryTitle;
    }

    public List<Product> getProductList() {
        return productList;
    }
}

