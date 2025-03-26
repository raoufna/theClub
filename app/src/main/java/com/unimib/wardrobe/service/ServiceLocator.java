package com.unimib.wardrobe.service;

import android.app.Application;
import android.util.Log;

import com.unimib.wardrobe.database.ProductRoomDatabase;
import com.unimib.wardrobe.util.Constants;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ServiceLocator {
    private static volatile ServiceLocator INSTANCE = null;

    private ServiceLocator(){

    }

    public static ServiceLocator getInstance(){
        if(INSTANCE == null){
            synchronized (ServiceLocator.class){
                if (INSTANCE == null){
                    INSTANCE = new ServiceLocator();
                }
            }
        }
        return INSTANCE;
    }

    OkHttpClient client = new OkHttpClient.Builder()
            .addInterceptor(chain -> {
                Request request = chain.request().newBuilder()
                        .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36")
                        .header("x-rapidapi-host", Constants.host)  // Corretto: 'x-rapidapi-host'
                        .header("x-rapidapi-key", Constants.apiKey)
                        .build();
                return chain.proceed(request);
            })
            .build();

    public ProductAPIService getProductAPIService(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.RAPID_API_BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create()).build();
        Log.d("API_REQUEST", "Host: " + Constants.host);
        Log.d("API_REQUEST", "API Key: " + Constants.apiKey);
        return retrofit.create(ProductAPIService.class);
    }

    public ProductRoomDatabase getProductDB(Application application){
        return ProductRoomDatabase.getDatabase(application);
    }
}
