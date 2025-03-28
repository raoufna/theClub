package com.unimib.wardrobe.repository;

import android.app.Application;

import com.unimib.wardrobe.R;
import com.unimib.wardrobe.database.ProductDAO;
import com.unimib.wardrobe.database.ProductRoomDatabase;
import com.unimib.wardrobe.model.Product;
import com.unimib.wardrobe.model.ProductAPIResponse;
import com.unimib.wardrobe.service.ServiceLocator;
import com.unimib.wardrobe.util.JSONParserUtils;
import com.unimib.wardrobe.util.Constants;
import com.unimib.wardrobe.util.ResponseCallback;

import java.io.IOException;
import java.util.List;

public class ProductMockRepository implements IProductRepository {
    private final Application application;
    private final ResponseCallback responseCallback;
    private final ProductDAO ProductDao;

    public ProductMockRepository(Application application, ResponseCallback responseCallback) {
        this.application = application;
        this.responseCallback = responseCallback;
        this.ProductDao = ServiceLocator.getInstance().getProductDB(application).ProductDao();
    }

    @Override
    public void fetchProduct(String country, int page, long lastUpdate) {
        ProductAPIResponse ProductApiResponse = null;

        JSONParserUtils jsonParserUtils = new JSONParserUtils(application.getApplicationContext());

        try {
            ProductApiResponse = jsonParserUtils.parseJSONFileWithGSon(Constants.JsonWardrobe);
            if (ProductApiResponse != null) {
                saveDataInDatabase(ProductApiResponse.getData().getProducts());
            } else {
                responseCallback.onFailure(application.getString(R.string.error_retrieving_news));
            }
        } catch (IOException e) {
            responseCallback.onFailure(application.getString(R.string.error_retrieving_news));
            throw new RuntimeException(e);
        }

    }

    @Override
    public void updateProduct(Product Product) {
    }

    @Override
    public void getFavoriteProduct() {
    }

    @Override
    public void deleteFavoriteProduct() {
    }

    private void saveDataInDatabase(List<Product> ProductList) {
        ProductRoomDatabase.databaseWriteExecutor.execute(() -> {
            // Reads the news from the database
            List<Product> allProducts = ProductDao.getAll();

            // Checks if the news just downloaded has already been downloaded earlier
            // in order to preserve the news status (marked as favorite or not)
            for (Product Product : allProducts) {
                // This check works because News and NewsSource classes have their own
                // implementation of equals(Object) and hashCode() methods

                if (ProductList.contains(Product)) {
                    // The primary key and the favorite status is contained only in the News objects
                    // retrieved from the database, and not in the News objects downloaded from the
                    // Web Service. If the same news was already downloaded earlier, the following
                    // line of code replaces the the News object in newsList with the corresponding
                    // News object saved in the database, so that it has the primary key and the
                    // favorite status.
                    ProductList.set(ProductList.indexOf(Product), Product);
                }
            }

            // Writes the news in the database and gets the associated primary keys
            List<Long> insertedNewsIds = ProductDao.insertNewsList(ProductList);
            for (int i = 0; i < ProductList.size(); i++) {
                // Adds the primary key to the corresponding object News just downloaded so that
                // if the user marks the news as favorite (and vice-versa), we can use its id
                // to know which news in the database must be marked as favorite/not favorite
                ProductList.get(i).setUid(insertedNewsIds.get(i));
            }

            responseCallback.onSuccess(ProductList, System.currentTimeMillis());
        });
    }

    private void readDataFromDatabase(long lastUpdate) {
        ProductRoomDatabase.databaseWriteExecutor.execute(() -> {
            responseCallback.onSuccess(ProductDao.getAll(), lastUpdate);
        });
    }
}