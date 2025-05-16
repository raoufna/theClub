package com.unimib.wardrobe.database;
import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Insert;
import androidx.room.Delete;
import androidx.room.Update;

import com.unimib.wardrobe.model.Product;


@Dao
public interface ProductDAO { @Query("SELECT * FROM Product")
List<Product> getAll();

    @Query("SELECT * FROM product WHERE uid = :id")
    Product getProduct(long id);

    @Query("SELECT * FROM Product WHERE liked = 1")
    List<Product> getLiked();

    @Query("SELECT * FROM Product WHERE liked = 1")
    LiveData<List<Product>> getLikedLive();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Product... products);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<Product> products);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    List<Long> insertProductList(List<Product> newsList);

    @Update
    int updateProduct(Product product);

    @Update
    int updateListFavoriteProducts(List<Product> products);

    @Delete
    void delete(Product user);

    @Query("DELETE from Product WHERE liked = 0")
    void deleteCached();

    @Query("DELETE from Product")
    void deleteAll();

    @Query("SELECT * FROM Product WHERE searchTerm = :searchTerm AND liked = 1")
    LiveData<List<Product>> getFavoriteProductsBySearchTerm(String searchTerm);

}
