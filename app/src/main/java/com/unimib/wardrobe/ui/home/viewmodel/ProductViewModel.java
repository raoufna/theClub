package com.unimib.wardrobe.ui.home.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.unimib.wardrobe.model.Product;
import com.unimib.wardrobe.model.Result;
import com.unimib.wardrobe.repository.ProductRepository;



public class ProductViewModel extends ViewModel {
    private static final String TAG = ProductViewModel.class.getSimpleName();

    private final ProductRepository productRepository;
    private final int page;
    private MutableLiveData<Result> productsListLiveData;
    private MutableLiveData<Result> favoriteNewsListLiveData;

    public ProductViewModel(ProductRepository productRepository) {
        this.productRepository = productRepository;
        this.page = 1;
    }

    public MutableLiveData<Result> getProducts(String searchTerm, long lastUpdate) {
        if (productsListLiveData == null) {
            fetchProducts(searchTerm, lastUpdate);
        }
        return productsListLiveData;
    }

    public MutableLiveData<Result> getFavoriteProductsLiveData() {
        if (favoriteNewsListLiveData == null) {
            getFavoriteProducts();
        }
        return favoriteNewsListLiveData;
    }


    public void updateProduct(Product product) {
        productRepository.updateProduct(product);
    }

    private void fetchProducts(String searchTerm, long lastUpdate) {
        productsListLiveData = productRepository.fetchProducts(searchTerm, page, lastUpdate);
    }

    private void getFavoriteProducts() {
        favoriteNewsListLiveData = productRepository.getFavoriteNews();
    }

    public void removeFromFavorite(Product product) {
        productRepository.updateProduct(product);
    }

    public void deleteAllFavoriteProducts() {
        productRepository.deleteFavoriteProducts();
    }
}
