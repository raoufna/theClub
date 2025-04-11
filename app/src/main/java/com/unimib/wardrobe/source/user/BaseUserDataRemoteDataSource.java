package com.unimib.wardrobe.source.user;


import com.unimib.wardrobe.model.User;
import com.unimib.wardrobe.repository.user.UserResponseCallback;

import java.util.Set;

public abstract class BaseUserDataRemoteDataSource {
    protected UserResponseCallback userResponseCallback;

    public void setUserResponseCallback(UserResponseCallback userResponseCallback) {
        this.userResponseCallback = userResponseCallback;
    }

    public abstract void saveUserData(User user);

    public abstract void getUserFavoriteNews(String idToken);


}