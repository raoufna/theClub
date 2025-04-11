package com.unimib.wardrobe.repository.product;

import com.unimib.wardrobe.model.Product;
import com.unimib.wardrobe.model.ProductAPIResponse;

import java.util.List;

public interface ProductCallback {
    void onSuccessFromRemote(ProductAPIResponse productAPIResponse, long lastUpdate);
    void onFailureFromRemote(Exception exception);
    void onSuccessFromLocal(List<Product> ProductsList);
    void onFailureFromLocal(Exception exception);
    void onNewsFavoriteStatusChanged(Product news, List<Product> favoriteNews);
    void onNewsFavoriteStatusChanged(List<Product> news);
    void onDeleteFavoriteNewsSuccess(List<Product> favoriteNews);
}
