package com.unimib.wardrobe.ui.home.viewmodel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.fido.u2f.api.common.ResponseData;
import com.unimib.wardrobe.model.Product;
import com.unimib.wardrobe.model.ProductAPIResponse;
import com.unimib.wardrobe.model.Result;
import com.unimib.wardrobe.repository.product.ProductRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;


public class ProductViewModel extends ViewModel {
    private static final String TAG = ProductViewModel.class.getSimpleName();

    private final ProductRepository productRepository;
    private final int page;
    private MutableLiveData<Result> productsListMutableLiveData;
    private LiveData<Result> productsListLiveData;
    private MutableLiveData<Result> favoriteNewsListLiveData;
    private final MutableLiveData<Result> combinedLiveData = new MutableLiveData<>();

    public ProductViewModel(ProductRepository productRepository) {
        this.productRepository = productRepository;
        this.page = 1;
    }

    public LiveData<List<Product>> getFavoriteProductsBySearchTerm(String searchTerm) {
        return productRepository.getFavoriteProductsBySearchTerm(searchTerm);
    }

    public MutableLiveData<Result> getProducts(String searchTerm, long lastUpdate) {
        if (productsListMutableLiveData == null) {
            fetchProducts(searchTerm, lastUpdate);
        }
        return productsListMutableLiveData;
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

    private LiveData<Result> fetchProducts(String searchTerm, long lastUpdate) {
        productsListLiveData = productRepository.fetchProducts(searchTerm, page, lastUpdate);
        return productsListLiveData;
    }

    private void getFavoriteProducts() {
        favoriteNewsListLiveData = productRepository.getFavoriteNews();
    }
    public LiveData<List<Product>> getLikedProducts() {
        return productRepository.getLikedProducts();
    }

    public void removeFromFavorite(Product product) {
        productRepository.updateProduct(product);
    }

    public void deleteAllFavoriteProducts() {
        productRepository.deleteFavoriteProducts();
    }

    public LiveData<Result> getCombinedProducts(List<String> searchTerms, long lastUpdate) {
        return combinedLiveData;
    }
    public void fetchCombinedProducts(List<String> searchTerms, long lastUpdate) {
        List<Product> combinedList = new ArrayList<>();
        AtomicInteger responsesCount = new AtomicInteger(0);
        int totalRequests = searchTerms.size();

        for (String searchTerm : searchTerms) {
            fetchAndProcessProducts(searchTerm, lastUpdate, combinedList, responsesCount, totalRequests);
        }
    }




    private void fetchAndProcessProducts(String searchTerm, long lastUpdate, List<Product> combinedList,
                                         AtomicInteger responsesCount, int totalRequests) {

        Log.d("DEBUG", "fetchAndProcessProducts called with searchTerm: " + searchTerm);

        productRepository.fetchProducts(searchTerm, 10, lastUpdate)
                .observeForever(result -> {
                    Log.d("DEBUG", "Inside observeForever for searchTerm: " + searchTerm);

                    if (result instanceof Result.Success) {
                        ProductAPIResponse response = ((Result.Success) result).getData();

                        if (response != null && response.getData() != null) {
                            List<Product> products = response.getData().getProducts();
                            String responseSearchTerm = response.getData().getSearchTerm(); // <-- qui

                            Log.d("DEBUG", ">> searchTerm from response = " + responseSearchTerm);

                            if (products != null) {
                                for (Product product : products) {
                                    Log.d("DEBUG", "Prodotto originale: " + product.getName() + " | searchTerm=" + product.getSearchTerm());

                                    Product newProduct = new Product();
                                    newProduct.setUid(product.getUid());
                                    newProduct.setName(product.getName());
                                    newProduct.setBrandName(product.getBrandName());
                                    newProduct.setImageUrl(product.getImageUrl());

                                    // ⬇️ Assegniamo il searchTerm dalla response
                                    newProduct.setSearchTerm(responseSearchTerm);

                                    Log.d("DEBUG", "NUOVO prodotto creato: " + newProduct.getName() + " | searchTerm=" + newProduct.getSearchTerm());

                                    synchronized (combinedList) {
                                        combinedList.add(newProduct);
                                    }
                                }
                            }
                        }
                    }

                    if (responsesCount.incrementAndGet() == totalRequests) {
                        ProductAPIResponse combinedResponse = new ProductAPIResponse(combinedList);
                        combinedLiveData.postValue(new Result.Success(combinedResponse));
                    }
                });
    }





}
