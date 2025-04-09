package com.unimib.wardrobe.source;

import android.util.Log;

public abstract class BaseProductRemoteDataSource {
    protected ProductCallback productCallback;

    public void setProductCallback(ProductCallback productCallback) {
        this.productCallback = productCallback;
        Log.d("DEBUG", "Callback impostato: " + productCallback);
    }

    public abstract void getProducts(String searchTerm);
}

