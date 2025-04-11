package com.unimib.wardrobe.util;

public class Constants {

    public static final String JsonWardrobe = "JsonWardrobe.json";
    public static final int DATABASE_VERSION = 3;
    public static final String SAVED_ProductS_DATABASE = "saved_db";
    public static final int FRESH_TIMEOUT = 1000 * 60;

    public static final String RAPID_API_BASE_URL = "https://asos10.p.rapidapi.com/api/v1/";
    public static final String TOP_HEADLINES_ENDPOINT = "getProductListBySearchTerm";
    public static final String TOP_HEADLINES_SEARCHTERM_PARAMETER = "searchTerm";
    public static final String TOP_HEADLINES_APIKEY = "rapidapi_key";
    public static final String host = "asos10.p.rapidapi.com";  // <-- Assicurati che sia giusto
    public static final String apiKey = "ebbf6e112emshc6a6e893aec837fp1d90a4jsnbe5fae02d0c3";

    public static final String INVALID_USER_ERROR = "invalidUserError";
    public static final String INVALID_CREDENTIALS_ERROR = "invalidCredentials";
    public static final String USER_COLLISION_ERROR = "userCollisionError";
    public static final String WEAK_PASSWORD_ERROR = "passwordIsWeak";
    public static final String UNEXPECTED_ERROR = "unexpected_error";

    public static final String FIREBASE_REALTIME_DATABASE = "https://wardrobe-12435-default-rtdb.europe-west1.firebasedatabase.app/";
    public static final String FIREBASE_USERS_COLLECTION = "users";
    public static final String FIREBASE_FAVORITE_NEWS_COLLECTION = "favorite_news";
}
