package com.unimib.wardrobe.repository.product;

import static com.unimib.wardrobe.util.Constants.FRESH_TIMEOUT;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.unimib.wardrobe.model.Product;
import com.unimib.wardrobe.model.ProductAPIResponse;
import com.unimib.wardrobe.model.Result;
import com.unimib.wardrobe.source.product.BaseProductLocalDataSource;
import com.unimib.wardrobe.source.product.BaseProductRemoteDataSource;

import java.util.List;

public class ProductRepository implements ProductCallback {

    private static final String TAG = ProductRepository.class.getSimpleName();

    private final MutableLiveData<Result> allProductsMutableLiveData;
    private final MutableLiveData<Result> favoriteProductsMutableLiveData;
    private final BaseProductRemoteDataSource ProductRemoteDataSource;
    private final BaseProductLocalDataSource ProductLocalDataSource;
    private final Context context;


    public ProductRepository(Context context, BaseProductRemoteDataSource ProductRemoteDataSource,
                             BaseProductLocalDataSource ProductLocalDataSource) {

        this.context = context.getApplicationContext();
        allProductsMutableLiveData = new MutableLiveData<>();
        favoriteProductsMutableLiveData = new MutableLiveData<>();
        this.ProductRemoteDataSource = ProductRemoteDataSource;
        this.ProductLocalDataSource = ProductLocalDataSource;
        this.ProductRemoteDataSource.setProductCallback(this);
        Log.d("DEBUG", "ProductRepository creato con callback: " + this);
        this.ProductLocalDataSource.setProductCallback(this);
    }


    public LiveData<Result> fetchProducts(String searchTerm, int page, long lastUpdate) {
        MutableLiveData<Result> resultLiveData = new MutableLiveData<>();
        long currentTime = System.currentTimeMillis();

        if (currentTime - lastUpdate > FRESH_TIMEOUT) {
            Log.d("DEBUG", "fetchProducts chiamato con: " + searchTerm);

            // Imposta il callback prima di avviare la richiesta
            ProductRemoteDataSource.setProductCallback(new ProductCallback() {
                @Override
                public void onSuccessFromRemote(ProductAPIResponse productAPIResponse, long lastUpdate) {
                    resultLiveData.postValue(new Result.Success(productAPIResponse));
                    Log.d("DEBUG", "✅ SUCCESS: Dati ricevuti, chiamata a insertProducts");
                    ProductLocalDataSource.insertProducts(productAPIResponse.getData().getProducts());
                    long now = System.currentTimeMillis();
                    SharedPreferences prefs = context.getSharedPreferences("my_prefs", Context.MODE_PRIVATE);
                    prefs.edit().putLong("last_update_timestamp", now).apply();
                }

                @Override
                public void onFailureFromRemote(Exception exception) {
                    Log.e("DEBUG", "❌ FAILURE: errore nella chiamata", exception);
                    resultLiveData.postValue(new Result.Error(exception.getMessage()));
                }

                @Override
                public void onSuccessFromLocal(List<Product> productsList) {
                    // Implementazione vuota per non utilizzare questo metodo
                }

                @Override
                public void onFailureFromLocal(Exception exception) {
                    // Implementazione vuota per non utilizzare questo metodo
                }

                @Override
                public void onNewsFavoriteStatusChanged(Product news, List<Product> favoriteNews) {
                    // Implementazione vuota per non utilizzare questo metodo
                }

                @Override
                public void onNewsFavoriteStatusChanged(List<Product> news) {
                    // Implementazione vuota per non utilizzare questo metodo
                }

                @Override
                public void onDeleteFavoriteNewsSuccess(List<Product> favoriteNews) {
                    // Implementazione vuota per non utilizzare questo metodo
                }
            });

            // Avvia la chiamata
            ProductRemoteDataSource.getProducts(searchTerm);

        } else {
            ProductLocalDataSource.getProducts();
        }

        return resultLiveData;
    }


    public MutableLiveData<Result> getFavoriteNews() {

        ProductLocalDataSource.getFavoriteProducts();
        return favoriteProductsMutableLiveData;
    }

    public void updateProduct(Product Product) {
        ProductLocalDataSource.updateProduct(Product);
    }

    public void deleteFavoriteProducts() {
        ProductLocalDataSource.deleteFavoriteProducts();
    }

    public void onSuccessFromRemote(ProductAPIResponse ProductApiResponse, long lastUpdate) {
        ProductLocalDataSource.insertProducts(ProductApiResponse.getData().getProducts());
    }

    public void onFailureFromRemote(Exception exception) {
        Result.Error result = new Result.Error(exception.getMessage());
        Log.d("DEBUG", "onFailureFromRemote chiamato con errore: " + exception.getMessage());
        allProductsMutableLiveData.postValue(result);
    }

    public void onSuccessFromLocal(List<Product> ProductList) {
        Log.d("ProductLocalDataSource", "Prodotti caricati dal database locale: " + ProductList.size());
        Result.Success result = new Result.Success(new ProductAPIResponse(ProductList));
        allProductsMutableLiveData.postValue(result);
    }

    public void onFailureFromLocal(Exception exception) {
        Result.Error resultError = new Result.Error(exception.getMessage());
        allProductsMutableLiveData.postValue(resultError);
        favoriteProductsMutableLiveData.postValue(resultError);
    }


    public void onNewsFavoriteStatusChanged(Product Product, List<Product> favoriteProducts) {
        Result allNewsResult = allProductsMutableLiveData.getValue();

        if (allNewsResult != null && allNewsResult.isSuccess()) {
            List<Product> oldAllProducts = ((Result.Success) allNewsResult).getData().getData().getProducts();
            if (oldAllProducts.contains(Product)) {
                oldAllProducts.set(oldAllProducts.indexOf(Product), Product);
                allProductsMutableLiveData.postValue(allNewsResult);
            }
        }
        favoriteProductsMutableLiveData.postValue(new Result.Success(new ProductAPIResponse(favoriteProducts)));
    }

    public void onNewsFavoriteStatusChanged(List<Product> favoriteProducts) {
        favoriteProductsMutableLiveData.postValue(new Result.Success(new ProductAPIResponse(favoriteProducts)));
    }

    public void onDeleteFavoriteNewsSuccess(List<Product> favoriteProducts) {
        Result allNewsResult = allProductsMutableLiveData.getValue();

        if (allNewsResult != null && allNewsResult.isSuccess()) {
            List<Product> oldAllNews = ((Result.Success) allNewsResult).getData().getData().getProducts();
            for (Product Product : favoriteProducts) {
                if (oldAllNews.contains(Product)) {
                    oldAllNews.set(oldAllNews.indexOf(Product), Product);
                }
            }
            allProductsMutableLiveData.postValue(allNewsResult);
        }

        if (favoriteProductsMutableLiveData.getValue() != null &&
                favoriteProductsMutableLiveData.getValue().isSuccess()) {
            favoriteProducts.clear();
            Result.Success result = new Result.Success(new ProductAPIResponse(favoriteProducts));
            favoriteProductsMutableLiveData.postValue(result);
        }
    }

    public LiveData<List<Product>> getLikedProducts() {
        return ProductLocalDataSource.getLikedProducts();
    }

    public LiveData<List<Product>> getFavoriteProductsBySearchTerm(String searchTerm) {
        return ProductLocalDataSource.getFavoriteProductsBySearchTerm(searchTerm);
    }

}
