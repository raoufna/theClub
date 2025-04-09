package com.unimib.wardrobe.source;

import com.unimib.wardrobe.database.ProductDAO;
import com.unimib.wardrobe.database.ProductRoomDatabase;
import com.unimib.wardrobe.model.Product;

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
            productCallback.onSuccessFromLocal(productDAO.getAll());
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

    /**
     * Saves the news in the local database.
     * The method is executed with an ExecutorService defined in NewsRoomDatabase class
     * because the database access cannot been executed in the main thread.
     * @param productsList the list of news to be written in the local database.
     */
    @Override
    public void insertProducts(List<Product> productsList) {
        ProductRoomDatabase.databaseWriteExecutor.execute(() -> {
            // Reads the news from the database
            List<Product> allProducts = productDAO.getAll();

            if (productsList != null) {

                // Checks if the news just downloaded has already been downloaded earlier
                // in order to preserve the news status (marked as favorite or not)
                for (Product product : allProducts) {
                    // This check works because News and NewsSource classes have their own
                    // implementation of equals(Object) and hashCode() methods
                    if (productsList.contains(product)) {
                        // The primary key and the favorite status is contained only in the News objects
                        // retrieved from the database, and not in the News objects downloaded from the
                        // Web Service. If the same news was already downloaded earlier, the following
                        // line of code replaces the News object in newsList with the corresponding
                        // line of code replaces the News object in newsList with the corresponding
                        // News object saved in the database, so that it has the primary key and the
                        // favorite status.
                        productsList.set(productsList.indexOf(product), product);
                    }
                }

                // Writes the news in the database and gets the associated primary keys
                List<Long> insertedNewsIds = productDAO.insertProductList(productsList);
                for (int i = 0; i < productsList.size(); i++) {
                    // Adds the primary key to the corresponding object News just downloaded so that
                    // if the user marks the news as favorite (and vice-versa), we can use its id
                    // to know which news in the database must be marked as favorite/not favorite
                    productsList.get(i).setUid(insertedNewsIds.get(i));
                }
                productCallback.onSuccessFromLocal(productsList);
            }
        });
    }
}
