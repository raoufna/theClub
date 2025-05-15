package com.unimib.wardrobe.ui.home.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.fido.u2f.api.common.ResponseData;
import com.unimib.wardrobe.model.Product;
import com.unimib.wardrobe.model.ProductAPIResponse;
import com.unimib.wardrobe.model.Result;
import com.unimib.wardrobe.repository.product.ProductRepository;

import java.util.ArrayList;
import java.util.List;
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

        for (String searchTerm : searchTerms) {
            // Crea una variabile finale per catturare il valore corretto
            final String currentSearchTerm = searchTerm;

            productRepository.fetchProducts(currentSearchTerm, 10, lastUpdate)
                    .observeForever(result -> {
                        if (result instanceof Result.Success) {
                            ProductAPIResponse response = ((Result.Success) result).getData();
                            if (response != null && response.getData() != null) {
                                List<Product> products = response.getData().getProducts();
                                if (products != null) {
                                    // Usa currentSearchTerm invece della variabile del loop
                                    for (Product product : products) {
                                        product.setSearchTerm(currentSearchTerm);
                                    }
                                    combinedList.addAll(products);
                                }
                            }
                        }

                        if (responsesCount.incrementAndGet() == searchTerms.size()) {
                            ProductAPIResponse combinedResponse = new ProductAPIResponse(combinedList);
                            combinedLiveData.postValue(new Result.Success(combinedResponse));
                        }
                    });
        }
    }

}
