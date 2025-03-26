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
    }

    @Override
    public void fetchProduct(String searchTerm, int page, long lastUpdate) {
        long currentTime = System.currentTimeMillis();
        if(true){
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

            Log.d("API_REQUEST", "Chiamata API: " + productAPIResponseCall.request().url());
            productAPIResponseCall.enqueue(new Callback<ProductAPIResponse>(){
                @Override
                public void onResponse(@NonNull Call<ProductAPIResponse> call,
                                       @NonNull Response<ProductAPIResponse> response) {
                    Log.d("API_RESPONSE", "Codice risposta: " + response.code());
                    Log.d("API_RESPONSE", "Risposta API: " + response.body());
                    Log.d("API_RESPONSE", "Contenuto JSON: " + new Gson().toJson(response.body()));
                    Log.d("API_REQUEST", "Parametri inviati: " + queryParams.toString());
                    Log.d("API_RESPONSE", "Raw Response: " + response.raw().toString());
                    Log.d("API_RESPONSE", "Headers: " + response.headers());
                    if (response.isSuccessful() && response.body() != null) {
                        String jsonResponse = new Gson().toJson(response.body());
                        Log.d("API_RESPONSE", "Risposta API (JSON): " + jsonResponse);
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
        } else {
            readDataFromDatabase(lastUpdate);
        }
    }

    @Override
    public void getFavoritsProduct() {
        ProductRoomDatabase.databaseWriteExecutor.execute(() -> {
            responseCallback.onSuccess(productDAO.getLiked(), System.currentTimeMillis());
        });
    }

    private void saveDataInDatabase(List<Product> apiArticle) {
        ProductRoomDatabase.databaseWriteExecutor.execute(() -> {
            // Reads the news from the database
            List<Product> localProduct = productDAO.getAll();

            // Checks if the news just downloaded has already been downloaded earlier
            // in order to preserve the news status (marked as favorite or not)
            for (Product product : localProduct) {
                // This check works because News and NewsSource classes have their own
                // implementation of equals(Object) and hashCode() methods
                if (apiArticle.contains(product)) {
                    // The primary key and the favorite status is contained only in the News objects
                    // retrieved from the database, and not in the News objects downloaded from the
                    // Web Service. If the same news was already downloaded earlier, the following
                    // line of code replaces the the News object in newsList with the corresponding
                    // News object saved in the database, so that it has the primary key and the
                    // favorite status.
                    apiArticle.set(apiArticle.indexOf(product), product);
                }
            }

            // Writes the news in the database and gets the associated primary keys
            List<Long> insertedProductIds = productDAO.insertProductList(apiArticle);
            for (int i = 0; i < apiArticle.size(); i++) {
                // Adds the primary key to the corresponding object News just downloaded so that
                // if the user marks the news as favorite (and vice-versa), we can use its id
                // to know which news in the database must be marked as favorite/not favorite
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
