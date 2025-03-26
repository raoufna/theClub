package com.unimib.wardrobe.service;

import com.unimib.wardrobe.model.ProductAPIResponse;
import com.unimib.wardrobe.util.Constants;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

public interface ProductAPIService {
    @GET(Constants.TOP_HEADLINES_ENDPOINT)
    Call<ProductAPIResponse> getHeadLinesProduct(
            @Query("searchTerm") String searchTerm,
            @Query("country") String country,
            @Query("store") String store,
            @Query("languageShort") String languageShort,
            @Query("sizeSchema") String sizeSchema,
            @Query("limit") int limit,
            @Query("offset") int offset,
            @Query("sort") String sort,
            @Header("x-rapidapi-host") String host,
            @Header("x-rapidapi-key") String apiKey
    );
}