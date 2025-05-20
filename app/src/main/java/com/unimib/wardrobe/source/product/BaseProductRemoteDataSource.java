package com.unimib.wardrobe.source.product;

import android.util.Log;

import androidx.lifecycle.LiveData;

import com.unimib.wardrobe.model.Product;
import com.unimib.wardrobe.repository.product.ProductCallback;

import java.util.List;

public abstract class BaseProductRemoteDataSource {
    protected ProductCallback productCallback;

    public void setProductCallback(ProductCallback productCallback) {
        this.productCallback = productCallback;
        Log.d("DEBUG", "Callback impostato: " + productCallback);
    }

    public abstract void getProducts(String searchTerm);

}

