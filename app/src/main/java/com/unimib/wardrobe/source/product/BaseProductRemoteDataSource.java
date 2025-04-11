package com.unimib.wardrobe.source.product;

import android.util.Log;

import com.unimib.wardrobe.repository.product.ProductCallback;

public abstract class BaseProductRemoteDataSource {
    protected ProductCallback productCallback;

    public void setProductCallback(ProductCallback productCallback) {
        this.productCallback = productCallback;
        Log.d("DEBUG", "Callback impostato: " + productCallback);
    }

    public abstract void getProducts(String searchTerm);
}

