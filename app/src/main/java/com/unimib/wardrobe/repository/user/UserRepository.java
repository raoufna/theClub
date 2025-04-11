package com.unimib.wardrobe.repository.user;

import androidx.lifecycle.MutableLiveData;

import com.unimib.wardrobe.model.Product;
import com.unimib.wardrobe.model.ProductAPIResponse;
import com.unimib.wardrobe.model.Result;
import com.unimib.wardrobe.model.User;
import com.unimib.wardrobe.repository.product.ProductCallback;
import com.unimib.wardrobe.source.product.BaseProductLocalDataSource;
import com.unimib.wardrobe.source.user.BaseUserAuthenticationRemoteDataSource;
import com.unimib.wardrobe.source.user.BaseUserDataRemoteDataSource;

import java.util.List;
import java.util.Set;


/**
 * Repository class to get the user information.
 */
public class UserRepository implements IUserRepository, UserResponseCallback, ProductCallback {

    private static final String TAG = UserRepository.class.getSimpleName();

    private final BaseUserAuthenticationRemoteDataSource userRemoteDataSource;
    private final BaseUserDataRemoteDataSource userDataRemoteDataSource;
    private final BaseProductLocalDataSource productLocalDataSource;
    private final MutableLiveData<Result> userMutableLiveData;
    private final MutableLiveData<Result> userFavoriteNewsMutableLiveData;
    private final MutableLiveData<Result> userPreferencesMutableLiveData;

    public UserRepository(BaseUserAuthenticationRemoteDataSource userRemoteDataSource,
                          BaseUserDataRemoteDataSource userDataRemoteDataSource,
                          BaseProductLocalDataSource newsLocalDataSource) {
        this.userRemoteDataSource = userRemoteDataSource;
        this.userDataRemoteDataSource = userDataRemoteDataSource;
        this.productLocalDataSource = newsLocalDataSource;
        this.userMutableLiveData = new MutableLiveData<>();
        this.userPreferencesMutableLiveData = new MutableLiveData<>();
        this.userFavoriteNewsMutableLiveData = new MutableLiveData<>();
        this.userRemoteDataSource.setUserResponseCallback(this);
        this.userDataRemoteDataSource.setUserResponseCallback(this);
        this.productLocalDataSource.setProductCallback(this);
    }

    @Override
    public MutableLiveData<Result> getUser(String email, String password, boolean isUserRegistered) {
        if (isUserRegistered) {
            signIn(email, password);
        } else {
            signUp(email, password);
        }
        return userMutableLiveData;
    }

    @Override
    public MutableLiveData<Result> getGoogleUser(String idToken) {
        signInWithGoogle(idToken);
        return userMutableLiveData;
    }

    @Override
    public MutableLiveData<Result> getUserFavoriteNews(String idToken) {
        userDataRemoteDataSource.getUserFavoriteNews(idToken);
        return userFavoriteNewsMutableLiveData;
    }


    @Override
    public User getLoggedUser() {
        return userRemoteDataSource.getLoggedUser();
    }

    @Override
    public MutableLiveData<Result> logout() {
        userRemoteDataSource.logout();
        return userMutableLiveData;
    }

    @Override
    public void signUp(String email, String password) {
        userRemoteDataSource.signUp(email, password);
    }

    @Override
    public void signIn(String email, String password) {
        userRemoteDataSource.signIn(email, password);
    }

    @Override
    public void signInWithGoogle(String token) {
        userRemoteDataSource.signInWithGoogle(token);
    }


    @Override
    public void onSuccessFromAuthentication(User user) {
        if (user != null) {
            userDataRemoteDataSource.saveUserData(user);
        }
    }

    @Override
    public void onFailureFromAuthentication(String message) {
        Result.Error result = new Result.Error(message);
        userMutableLiveData.postValue(result);
    }

    @Override
    public void onSuccessFromRemoteDatabase(User user) {
        Result.UserSuccess result = new Result.UserSuccess(user);
        userMutableLiveData.postValue(result);
    }

    @Override
    public void onSuccessFromRemoteDatabase(List<Product> productList) {
        productLocalDataSource.insertProducts(productList);
    }

    @Override
    public void onSuccessFromGettingUserPreferences() {
        userPreferencesMutableLiveData.postValue(new Result.UserSuccess(null));
    }

    @Override
    public void onFailureFromRemoteDatabase(String message) {
        Result.Error result = new Result.Error(message);
        userMutableLiveData.postValue(result);
    }

    @Override
    public void onSuccessLogout() {

    }

    @Override
    public void onSuccessFromRemote(ProductAPIResponse productAPIResponse, long lastUpdate) {

    }

    @Override
    public void onFailureFromRemote(Exception exception) {

    }

    @Override
    public void onSuccessFromLocal(List<Product> productsList) {

    }

    @Override
    public void onFailureFromLocal(Exception exception) {

    }

    @Override
    public void onNewsFavoriteStatusChanged(Product product, List<Product> favoriteProducts) {

    }

    @Override
    public void onNewsFavoriteStatusChanged(List<Product> news) {

    }

    @Override
    public void onDeleteFavoriteNewsSuccess(List<Product> favoriteNews) {

    }

    //@Override
    public void onSuccessFromCloudReading(List<Product> newsList) {

    }

    //@Override
    public void onSuccessFromCloudWriting(Product product) {

    }

    //@Override
    public void onFailureFromCloud(Exception exception) {

    }
}
