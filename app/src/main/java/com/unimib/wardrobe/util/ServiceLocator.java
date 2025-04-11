package com.unimib.wardrobe.util;

import android.app.Application;

import com.unimib.wardrobe.R;
import com.unimib.wardrobe.database.ProductRoomDatabase;
import com.unimib.wardrobe.repository.product.ProductRepository;
import com.unimib.wardrobe.repository.user.IUserRepository;
import com.unimib.wardrobe.repository.user.UserRepository;
import com.unimib.wardrobe.service.ProductAPIService;
import com.unimib.wardrobe.source.product.BaseProductLocalDataSource;
import com.unimib.wardrobe.source.product.BaseProductRemoteDataSource;
import com.unimib.wardrobe.source.product.ProductMockDataSource;
import com.unimib.wardrobe.source.product.ProductRemoteDataSource;
import com.unimib.wardrobe.source.product.ProductLocalDataSource;
import com.unimib.wardrobe.source.user.BaseUserAuthenticationRemoteDataSource;
import com.unimib.wardrobe.source.user.BaseUserDataRemoteDataSource;
import com.unimib.wardrobe.source.user.UserAuthenticationFirebaseDataSource;
import com.unimib.wardrobe.source.user.UserFirebaseDataSource;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ServiceLocator {

    private static volatile ServiceLocator INSTANCE = null;

    private ServiceLocator() {}

    /**
     * Returns an instance of ServiceLocator class.
     * @return An instance of ServiceLocator.
     */
    public static ServiceLocator getInstance() {
        if (INSTANCE == null) {
            synchronized(ServiceLocator.class) {
                if (INSTANCE == null) {
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
                        .build();
                return chain.proceed(request);
            })
            .build();

    /**
     * Returns an instance of NewsApiService class using Retrofit.
     * @return an instance of NewsApiService.
     */
    public ProductAPIService getProductAPIService() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.RAPID_API_BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create()).build();
        return retrofit.create(ProductAPIService.class);
    }

    /**
     * Returns an instance of NewsRoomDatabase class to manage Room database.
     * @param application Param for accessing the global application state.
     * @return An instance of NewsRoomDatabase.
     */
    public ProductRoomDatabase getProductsDao(Application application) {
        return ProductRoomDatabase.getDatabase(application);
    }

    /**
     * Returns an instance of INewsRepositoryWithLiveData.
     * @param application Param for accessing the global application state.
     * @param debugMode Param to establish if the application is run in debug mode.
     * @return An instance of INewsRepositoryWithLiveData.
     */
    public ProductRepository getProductsRepository(Application application, boolean debugMode) {
        BaseProductRemoteDataSource productsRemoteDataSource;
        BaseProductLocalDataSource newsLocalDataSource;

        if (debugMode) {
            JSONParserUtils jsonParserUtil = new JSONParserUtils(application);
            productsRemoteDataSource =
                    new ProductMockDataSource(jsonParserUtil);
        } else {
            productsRemoteDataSource =
                    new ProductRemoteDataSource(application.getString(R.string.rapidapi_key));
        }

        newsLocalDataSource = new ProductLocalDataSource(getProductsDao(application));

        return new ProductRepository(productsRemoteDataSource, newsLocalDataSource);
    }

    public IUserRepository getUserRepository(Application application) {

        BaseUserAuthenticationRemoteDataSource userRemoteAuthenticationDataSource =
                new UserAuthenticationFirebaseDataSource();

        BaseUserDataRemoteDataSource userDataRemoteDataSource =
                new UserFirebaseDataSource();

        BaseProductLocalDataSource newsLocalDataSource =
                new ProductLocalDataSource(getProductsDao(application));

        return new UserRepository(userRemoteAuthenticationDataSource,
                userDataRemoteDataSource, newsLocalDataSource);
    }
}
