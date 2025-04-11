package com.unimib.wardrobe.source.product;

import android.util.Log;

import androidx.annotation.NonNull;

import com.unimib.wardrobe.model.Product;
import com.unimib.wardrobe.model.ProductAPIResponse;
import com.unimib.wardrobe.repository.product.ProductCallback;
import com.unimib.wardrobe.service.ProductAPIService;
import com.unimib.wardrobe.util.Constants;
import com.unimib.wardrobe.util.ServiceLocator;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductRemoteDataSource extends BaseProductRemoteDataSource {
    private final ProductAPIService productAPIService;
    private final String apiKey;

    public ProductRemoteDataSource(String apiKey) {
        this.apiKey = apiKey;
        this.productAPIService = ServiceLocator.getInstance().getProductAPIService();
    }

    @Override
    public void getProducts(String searchTerm) {
        Log.d("DEBUG", "📞 getProducts chiamato - callback attuale: " + productCallback);
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
                if (response.isSuccessful()) {
                    Log.d("DEBUG", "Risposta API ricevuta con successo.");

                    if (response.body() != null && response.isSuccessful()) {

                        Log.d("DEBUG", "Corpo della risposta: " + response.body().toString());
                        Log.d("DEBUG", "Numero di prodotti ricevuti: " + response.body().getData().getProducts().size());
                        for (Product product : response.body().getData().getProducts()) {
                            Log.d("DEBUG", "Prodotto: " + product.getName());
                        }
                        // 🚨 Verifica se productCallback è NULL prima di usarlo!
                        if (productCallback != null) {
                            productCallback.onSuccessFromRemote(response.body(), System.currentTimeMillis());
                            Log.d("DEBUG", "productCallback chiamato con successo.");
                        } else {
                            Log.e("DEBUG", "ERRORE: productCallback è NULL in onResponse!");
                        }
                    } else {
                        Log.d("DEBUG", "Corpo della risposta vuoto.");
                    }
                } else {
                    Log.e("DEBUG", "Errore nella risposta API: " + response.code());

                    if (productCallback != null) {
                        productCallback.onFailureFromRemote(new Exception("API_KEY_ERROR"));
                    } else {
                        Log.e("DEBUG", "ERRORE: productCallback è NULL in onResponse (API_KEY_ERROR)!");
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ProductAPIResponse> call, @NonNull Throwable t) {
                productCallback.onFailureFromRemote(new Exception("RETROFIT_ERROR"));
            }
        });
    }
    public void setProductCallback(ProductCallback callback) {
        Log.d("DEBUG", "✅ setProductCallback chiamato con: " + productCallback);
        this.productCallback = callback;
        Log.d("DEBUG", "ProductCallback assegnato correttamente.");
    }
}
