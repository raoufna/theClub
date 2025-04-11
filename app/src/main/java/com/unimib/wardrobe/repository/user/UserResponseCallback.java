package com.unimib.wardrobe.repository.user;

import com.unimib.wardrobe.model.Product;
import com.unimib.wardrobe.model.User;

import java.util.List;

public interface UserResponseCallback {
    void onSuccessFromAuthentication(User user);
    void onFailureFromAuthentication(String message);
    void onSuccessFromRemoteDatabase(User user);
    void onSuccessFromRemoteDatabase(List<Product> productsList);
    void onSuccessFromGettingUserPreferences();
    void onFailureFromRemoteDatabase(String message);
    void onSuccessLogout();
}
