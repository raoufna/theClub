package com.unimib.wardrobe.database;


import static com.unimib.wardrobe.util.Constants.DATABASE_VERSION;

import android.content.Context;
import com.unimib.wardrobe.util.Constants;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.unimib.wardrobe.model.Product;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {Product.class}, version = DATABASE_VERSION, exportSchema = true)
public abstract class ProductRoomDatabase extends RoomDatabase {

    public abstract ProductDAO ProductDao();

    private static volatile ProductRoomDatabase INSTANCE;
    public static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    public static ProductRoomDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (ProductRoomDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    ProductRoomDatabase.class, Constants.SAVED_ProductS_DATABASE)
                            .fallbackToDestructiveMigration()
                            .allowMainThreadQueries().build();
                }
            }
        }
        return INSTANCE;
    }
}
