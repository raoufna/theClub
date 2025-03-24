package com.unimib.wardrobe.database;
import java.util.List;
import androidx.room.Dao;
import androidx.room.Query;
import androidx.room.Insert;
import androidx.room.Delete;

import com.unimib.wardrobe.model.Product;


@Dao
public interface ProductDAO {
    @Query("SELECT * FROM product")
    List<Product> getAll();

    @Insert
    void insertAll(Product... users);

    @Delete
    void delete(Product user);

    @Query("DELETE FROM Product WHERE name = :name AND brandName = :brandName")
    void deleteByNameAndBrand(String name, String brandName);


    @Query("DELETE from Product")
    void deleteAll();

}
