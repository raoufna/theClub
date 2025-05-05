package com.unimib.wardrobe.repository.product;

import static com.unimib.wardrobe.util.Constants.FRESH_TIMEOUT;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.unimib.wardrobe.model.Product;
import com.unimib.wardrobe.model.ProductAPIResponse;
import com.unimib.wardrobe.model.Result;
import com.unimib.wardrobe.source.product.BaseProductLocalDataSource;
import com.unimib.wardrobe.source.product.BaseProductRemoteDataSource;

import java.util.List;

public class ProductRepository implements ProductCallback {

        private static final String TAG = ProductRepository.class.getSimpleName();

        private final MutableLiveData<Result> allProductsMutableLiveData;
        private final MutableLiveData<Result> favoriteProductsMutableLiveData;
        private final BaseProductRemoteDataSource ProductRemoteDataSource;
        private final BaseProductLocalDataSource ProductLocalDataSource;

        public ProductRepository(BaseProductRemoteDataSource ProductRemoteDataSource,
                                 BaseProductLocalDataSource ProductLocalDataSource) {

            allProductsMutableLiveData = new MutableLiveData<>();
            favoriteProductsMutableLiveData = new MutableLiveData<>();
            this.ProductRemoteDataSource = ProductRemoteDataSource;
            this.ProductLocalDataSource = ProductLocalDataSource;
            this.ProductRemoteDataSource.setProductCallback(this);
            Log.d("DEBUG", "ProductRepository creato con callback: " + this);
            this.ProductLocalDataSource.setProductCallback(this);
        }

    public LiveData<Result> fetchProducts(String searchTerm, int page, long lastUpdate) {
        MutableLiveData<Result> resultLiveData = new MutableLiveData<>();
        long currentTime = System.currentTimeMillis();

        if (currentTime - lastUpdate > FRESH_TIMEOUT) {
            Log.d("DEBUG", "fetchProducts chiamato con: " + searchTerm);

            // Imposta il callback prima di avviare la richiesta
            ProductRemoteDataSource.setProductCallback(new ProductCallback() {
                @Override
                public void onSuccessFromRemote(ProductAPIResponse productAPIResponse, long lastUpdate) {
                    Log.d("DEBUG", "✅ SUCCESS: prodotti ricevuti");
                    resultLiveData.postValue(new Result.Success(productAPIResponse));
                }

                @Override
                public void onFailureFromRemote(Exception exception) {
                    Log.e("DEBUG", "❌ FAILURE: errore nella chiamata", exception);
                    resultLiveData.postValue(new Result.Error(exception.getMessage()));
                }

                @Override
                public void onSuccessFromLocal(List<Product> productsList) {
                    // Implementazione vuota per non utilizzare questo metodo
                }

                @Override
                public void onFailureFromLocal(Exception exception) {
                    // Implementazione vuota per non utilizzare questo metodo
                }

                @Override
                public void onNewsFavoriteStatusChanged(Product news, List<Product> favoriteNews) {
                    // Implementazione vuota per non utilizzare questo metodo
                }

                @Override
                public void onNewsFavoriteStatusChanged(List<Product> news) {
                    // Implementazione vuota per non utilizzare questo metodo
                }

                @Override
                public void onDeleteFavoriteNewsSuccess(List<Product> favoriteNews) {
                    // Implementazione vuota per non utilizzare questo metodo
                }
            });

            // Avvia la chiamata
            ProductRemoteDataSource.getProducts(searchTerm);

        } else {
            Log.d("DEBUG", "Prendo dati in locale");
            // Anche qui stessa cosa: LiveData va popolata da ProductLocalDataSource
            ProductLocalDataSource.setProductCallback(new ProductCallback() {
                @Override
                public void onSuccessFromLocal(List<Product> productsList) {
                    // Crea un ProductAPIResponse e inserisci la lista di prodotti in "Data"
                    ProductAPIResponse productAPIResponse = new ProductAPIResponse();
                    ProductAPIResponse.Data data = new ProductAPIResponse.Data();
                    data.setProducts(productsList); // Imposta i prodotti ricevuti
                    productAPIResponse.setData(data); // Imposta i dati nel response

                    // Ora puoi passare l'oggetto ProductAPIResponse
                    resultLiveData.postValue(new Result.Success(productAPIResponse));// Assicurati di passare i prodotti correttamente
                }

                @Override
                public void onFailureFromLocal(Exception exception) {
                    Log.e("DEBUG", "❌ FAILURE LOCAL: errore nella lettura locale", exception);
                    resultLiveData.postValue(new Result.Error(exception.getMessage()));
                }

                @Override
                public void onSuccessFromRemote(ProductAPIResponse productAPIResponse, long lastUpdate) {
                    // Implementazione vuota per non utilizzare questo metodo
                }

                @Override
                public void onFailureFromRemote(Exception exception) {
                    // Implementazione vuota per non utilizzare questo metodo
                }

                @Override
                public void onNewsFavoriteStatusChanged(Product news, List<Product> favoriteNews) {
                    // Implementazione vuota per non utilizzare questo metodo
                }

                @Override
                public void onNewsFavoriteStatusChanged(List<Product> news) {
                    // Implementazione vuota per non utilizzare questo metodo
                }

                @Override
                public void onDeleteFavoriteNewsSuccess(List<Product> favoriteNews) {
                    // Implementazione vuota per non utilizzare questo metodo
                }
            });

            ProductLocalDataSource.getProducts();
        }

        return resultLiveData;
    }




    public MutableLiveData<Result> getFavoriteNews() {

            ProductLocalDataSource.getFavoriteProducts();
            return favoriteProductsMutableLiveData;
        }

        public void updateProduct(Product Product) {
            ProductLocalDataSource.updateProduct(Product);
        }

        public void deleteFavoriteProducts() {
            ProductLocalDataSource.deleteFavoriteProducts();
        }

        public void onSuccessFromRemote(ProductAPIResponse ProductApiResponse, long lastUpdate) {
            Log.d("DEBUG", "onSuccessFromRemote chiamato con i dati ricevuti.");
            ProductLocalDataSource.insertProducts(ProductApiResponse.getData().getProducts());
        }

        public void onFailureFromRemote(Exception exception) {
            Result.Error result = new Result.Error(exception.getMessage());
            Log.d("DEBUG", "onFailureFromRemote chiamato con errore: " + exception.getMessage());
            allProductsMutableLiveData.postValue(result);
        }

        public void onSuccessFromLocal(List<Product> ProductList) {
            Result.Success result = new Result.Success(new ProductAPIResponse(ProductList));
            allProductsMutableLiveData.postValue(result);
        }

        public void onFailureFromLocal(Exception exception) {
            Result.Error resultError = new Result.Error(exception.getMessage());
            allProductsMutableLiveData.postValue(resultError);
            favoriteProductsMutableLiveData.postValue(resultError);
        }


        public void onNewsFavoriteStatusChanged(Product Product, List<Product> favoriteProducts) {
            Result allNewsResult = allProductsMutableLiveData.getValue();

            if (allNewsResult != null && allNewsResult.isSuccess()) {
                List<Product> oldAllProducts = ((Result.Success)allNewsResult).getData().getData().getProducts();
                if (oldAllProducts.contains(Product)) {
                    oldAllProducts.set(oldAllProducts.indexOf(Product), Product);
                    allProductsMutableLiveData.postValue(allNewsResult);
                }
            }
            favoriteProductsMutableLiveData.postValue(new Result.Success(new ProductAPIResponse(favoriteProducts)));
        }

        public void onNewsFavoriteStatusChanged(List<Product> favoriteProducts) {
            favoriteProductsMutableLiveData.postValue(new Result.Success(new ProductAPIResponse(favoriteProducts)));
        }

        public void onDeleteFavoriteNewsSuccess(List<Product> favoriteProducts) {
            Result allNewsResult = allProductsMutableLiveData.getValue();

            if (allNewsResult != null && allNewsResult.isSuccess()) {
                List<Product> oldAllNews = ((Result.Success) allNewsResult).getData().getData().getProducts();
                for (Product Product : favoriteProducts) {
                    if (oldAllNews.contains(Product)) {
                        oldAllNews.set(oldAllNews.indexOf(Product), Product);
                    }
                }
                allProductsMutableLiveData.postValue(allNewsResult);
            }

            if (favoriteProductsMutableLiveData.getValue() != null &&
                    favoriteProductsMutableLiveData.getValue().isSuccess()) {
                favoriteProducts.clear();
                Result.Success result = new Result.Success(new ProductAPIResponse(favoriteProducts));
                favoriteProductsMutableLiveData.postValue(result);
            }
        }
    public LiveData<List<Product>> getLikedProducts() {
        return ProductLocalDataSource.getLikedProducts();
    }
}
