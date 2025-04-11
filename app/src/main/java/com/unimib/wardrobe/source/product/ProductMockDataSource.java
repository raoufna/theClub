package com.unimib.wardrobe.source.product;

import com.unimib.wardrobe.model.ProductAPIResponse;
import com.unimib.wardrobe.util.Constants;
import com.unimib.wardrobe.util.JSONParserUtils;

import java.io.IOException;

public class ProductMockDataSource extends BaseProductRemoteDataSource {
    private final JSONParserUtils jsonParserUtil;

    public ProductMockDataSource(JSONParserUtils jsonParserUtil) {
        this.jsonParserUtil = jsonParserUtil;
    }

    @Override
    public void getProducts(String searchTerm) {
        ProductAPIResponse articleAPIResponse = null;

        try {
            articleAPIResponse = jsonParserUtil.parseJSONFileWithGSon(Constants.JsonWardrobe);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (articleAPIResponse != null) {
            productCallback.onSuccessFromRemote(articleAPIResponse, System.currentTimeMillis());
        } else {
            productCallback.onFailureFromRemote(new Exception("API_KEY_ERROR"));
        }
    }
}
