package com.unimib.wardrobe.util;

import com.unimib.wardrobe.model.Product;

import java.util.List;

public interface ResponseCallback {
    void onSuccess(List<Product> productList, long lastUpdate);
    void onFailure(String errorMessage);
}
