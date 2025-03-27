package com.unimib.wardrobe.repository;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.unimib.wardrobe.R;
import com.unimib.wardrobe.database.ProductDAO;
import com.unimib.wardrobe.database.ProductRoomDatabase;
import com.unimib.wardrobe.model.Product;
import com.unimib.wardrobe.model.ProductAPIResponse;
import com.unimib.wardrobe.service.ProductAPIService;
import com.unimib.wardrobe.service.ServiceLocator;
import com.unimib.wardrobe.util.Constants;
import com.unimib.wardrobe.util.ResponseCallback;
import androidx.recyclerview.widget.RecyclerView;  // Import per RecyclerView
import com.google.android.material.progressindicator.CircularProgressIndicator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kotlin.jvm.Throws;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductAPIRepository implements IProductRepository{

    private static final String TAG = ProductAPIRepository.class.getSimpleName();

    private final Application application;
    private final ProductAPIService productAPIService;
    private final ProductDAO productDAO;
    private final ResponseCallback responseCallback;

    public ProductAPIRepository(Application application, ResponseCallback responseCallback){
        this.application = application;
        this.productAPIService = ServiceLocator.getInstance().getProductAPIService();
        ProductRoomDatabase productRoomDatabase = ServiceLocator.getInstance().getProductDB(application);
        this.productDAO = productRoomDatabase.ProductDao();
        this.responseCallback = responseCallback;
        ProductRoomDatabase.databaseWriteExecutor.execute(() -> {
            productDAO.deleteAll();  // Assicurati di avere questo metodo nel tuo DAO
        });
    }

    @Override
    public void fetchProduct(String searchTerm, int page, long lastUpdate) {
        long currentTime = System.currentTimeMillis();
        List<Product> cachedProducts = productDAO.getAll();
        if(cachedProducts != null && !cachedProducts.isEmpty()) {
            responseCallback.onSuccess(cachedProducts, System.currentTimeMillis());
        }else {
                Map<String, String> queryParams = new HashMap<>();
                queryParams.put("searchTerm", searchTerm);
                queryParams.put("country", "US");
                queryParams.put("store", "US");
                queryParams.put("languageShort", "en");
                queryParams.put("sizeSchema", "US");
                queryParams.put("limit", "50");
                queryParams.put("offset", "0");
                queryParams.put("sort", "recommended");
            Call<ProductAPIResponse> productAPIResponseCall = productAPIService.getHeadLinesProduct(searchTerm, "US", "US", "en", "US", 50, 0, "recommended", Constants.host, Constants.apiKey);
            productAPIResponseCall.enqueue(new Callback<ProductAPIResponse>(){
                @Override
                public void onResponse(@NonNull Call<ProductAPIResponse> call,
                                       @NonNull Response<ProductAPIResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        List<Product> productList = response.body().getData().getProducts();

                        if (productList != null && !productList.isEmpty()) {
                            saveDataInDatabase(productList);
                        } else {
                            Log.e("API_RESPONSE", "Lista prodotti vuota o null");
                        }
                    } else {
                        Log.e("API_RESPONSE", "Errore nella risposta API: " + response.code());
                        responseCallback.onFailure("Errore API: " + response.code());
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ProductAPIResponse> call, @NonNull Throwable t) {
                    Log.e("API_ERROR", "Errore di rete: " + t.getMessage());
                    responseCallback.onFailure("Errore di rete: " + t.getMessage());
                }
            });
        }
    }

    @Override
    public void deleteFavoriteProduct(){
        ProductRoomDatabase.databaseWriteExecutor.execute(()-> {
            List<Product> favoriteProducs = productDAO.getLiked();
            for(Product product : favoriteProducs){
                product.setLiked(false);
            }
            responseCallback.onSuccess(productDAO.getLiked(), System.currentTimeMillis());
        });
    }
    @Override
    public void updateProduct(Product product){
        ProductRoomDatabase.databaseWriteExecutor.execute(()->{
            productDAO.insert(product);
        });
    }


    @Override
    public void getFavoriteProduct() {
        ProductRoomDatabase.databaseWriteExecutor.execute(() -> {
            responseCallback.onSuccess(productDAO.getLiked(), System.currentTimeMillis());
        });
    }

    private void saveDataInDatabase(List<Product> apiArticle) {
        ProductRoomDatabase.databaseWriteExecutor.execute(() -> {
            List<Product> localProduct = productDAO.getAll();

            for (Product product : localProduct) {
                if (apiArticle.contains(product)) {
                    apiArticle.set(apiArticle.indexOf(product), product);
                }
            }

            List<Long> insertedProductIds = productDAO.insertProductList(apiArticle);
            for (int i = 0; i < apiArticle.size(); i++) {
                apiArticle.get(i).setUid(insertedProductIds.get(i));
            }

            responseCallback.onSuccess(apiArticle, System.currentTimeMillis());
        });
    }

    private void readDataFromDatabase(long lastUpdate) {
        ProductRoomDatabase.databaseWriteExecutor.execute(() -> {
            responseCallback.onSuccess(productDAO.getAll(), lastUpdate);
        });
}
}
