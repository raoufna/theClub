package com.unimib.wardrobe.source.product;

import android.util.Log;

import androidx.lifecycle.LiveData;

import com.unimib.wardrobe.database.ProductDAO;
import com.unimib.wardrobe.database.ProductRoomDatabase;
import com.unimib.wardrobe.model.Product;

import java.util.ArrayList;
import java.util.List;

public class ProductLocalDataSource extends BaseProductLocalDataSource {
    private final ProductDAO productDAO;

    public ProductLocalDataSource(ProductRoomDatabase RoomDatabase) {
        this.productDAO = RoomDatabase.ProductDao();
    }

    /**
     * Gets the news from the local database.
     * The method is executed with an ExecutorService defined in NewsRoomDatabase class
     * because the database access cannot been executed in the main thread.
     */
    @Override
    public void getProducts() {
        ProductRoomDatabase.databaseWriteExecutor.execute(() -> {
            List<Product> products = productDAO.getAll();
            if (products != null && !products.isEmpty()) {
                productCallback.onSuccessFromLocal(products);
                Log.d("ProductLocalDataSource", " prodotti trovato nel database locale.");

            } else {
                Log.d("ProductLocalDataSource", "Nessun prodotto trovato nel database locale.");
                productCallback.onSuccessFromLocal(new ArrayList<>()); // O eventualmente un altro tipo di gestione
            }
        });
    }

    @Override
    public void getFavoriteProducts() {
        ProductRoomDatabase.databaseWriteExecutor.execute(() -> {
            List<Product> favoriteNews = productDAO.getLiked();
            productCallback.onNewsFavoriteStatusChanged(favoriteNews);
        });
    }

    @Override
    public void updateProduct(Product product) {
        ProductRoomDatabase.databaseWriteExecutor.execute(() -> {
            int rowUpdatedCounter = productDAO.updateProduct(product);

            // It means that the update succeeded because only one row had to be updated
            if (rowUpdatedCounter == 1) {
                Product updatedNews = productDAO.getProduct(product.getUid());
                productCallback.onNewsFavoriteStatusChanged(updatedNews, productDAO.getLiked());
            } else {
                productCallback.onFailureFromLocal(new Exception("UNEXPECTED_ERROR"));
            }
        });
    }

    @Override
    public void deleteFavoriteProducts() {
        ProductRoomDatabase.databaseWriteExecutor.execute(() -> {
            List<Product> favoriteProducts = productDAO.getLiked();
            for (Product product : favoriteProducts) {
                product.setLiked(false);
            }
            int updatedRowsNumber = productDAO.updateListFavoriteProducts(favoriteProducts);

            // It means that the update succeeded because the number of updated rows is
            // equal to the number of the original favorite news
            if (updatedRowsNumber == favoriteProducts.size()) {
                productCallback.onDeleteFavoriteNewsSuccess(favoriteProducts);
            } else {
                productCallback.onFailureFromLocal(new Exception("UNEXPECTED_ERROR"));
            }
        });
    }

    public LiveData<List<Product>> getLikedProducts() {
        return productDAO.getLikedLive();
    }

    /**
     * Saves the news in the local database.
     * The method is executed with an ExecutorService defined in NewsRoomDatabase class
     * because the database access cannot been executed in the main thread.
     * @param productsList the list of news to be written in the local database.
     */
    @Override
    public void insertProducts(List<Product> productsList) {
        Log.d("ProductLocalDataSource", "Metodo insertProducts chiamato");
        ProductRoomDatabase.databaseWriteExecutor.execute(() -> {
            // Reads the news from the database
            Log.d("ProductLocalDataSource", "Inizio salvataggio prodotti");
            List<Product> allProducts = productDAO.getAll();

            if (productsList != null) {
                Log.d("ProductLocalDataSource", "Prodotti da inserire: " + productsList.size());
                for (Product product : allProducts) {
                    if (productsList.contains(product)) {
                        productsList.set(productsList.indexOf(product), product);
                    }
                }
                List<Long> insertedNewsIds = productDAO.insertProductList(productsList);
                for (int i = 0; i < productsList.size(); i++) {
                    productsList.get(i).setUid(insertedNewsIds.get(i));
                }
                Log.d("ProductLocalDataSource", "Prodotti inseriti: " + productsList.size());
                productCallback.onSuccessFromLocal(productsList);
            } else {
                Log.e("ProductLocalDataSource", "La lista di prodotti è nulla");
            }
        });
    }
}
