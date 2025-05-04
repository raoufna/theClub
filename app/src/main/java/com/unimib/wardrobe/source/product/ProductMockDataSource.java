package com.unimib.wardrobe.source.product;

import com.unimib.wardrobe.model.Product;
import com.unimib.wardrobe.model.ProductAPIResponse;
import com.unimib.wardrobe.util.Constants;
import com.unimib.wardrobe.util.JSONParserUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ProductMockDataSource extends BaseProductRemoteDataSource {
    private final JSONParserUtils jsonParserUtil;

    public ProductMockDataSource(JSONParserUtils jsonParserUtil) {
        this.jsonParserUtil = jsonParserUtil;
    }

    @Override
    public void getProducts(String searchTerm) {
        ProductAPIResponse responseJackets = null;
        ProductAPIResponse responseTshirts = null;
        ProductAPIResponse responsePants = null;
        ProductAPIResponse response = null;

        try {
            if (searchTerm.equalsIgnoreCase("jeans")) {
                response = jsonParserUtil.parseJSONFileWithGSon(Constants.JsonWardrobePants);
            } else if (searchTerm.equalsIgnoreCase("tshirt")) {
                response = jsonParserUtil.parseJSONFileWithGSon(Constants.JsonWardrobeTshirt);
            } else if (searchTerm.equalsIgnoreCase("sneakers")) {
                response = jsonParserUtil.parseJSONFileWithGSon(Constants.JsonWardrobe);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (response != null) {
            productCallback.onSuccessFromRemote(response, System.currentTimeMillis());
        }

        if (responseJackets != null && responseTshirts != null && responsePants != null) {
            // Unisci i prodotti delle due liste
            List<Product> combinedProducts = new ArrayList<>();
            combinedProducts.addAll(responseJackets.getData().getProducts());
            combinedProducts.addAll(responseTshirts.getData().getProducts());
            combinedProducts.addAll(responsePants.getData().getProducts());

            // Crea una nuova risposta combinata
            ProductAPIResponse combinedResponse = new ProductAPIResponse();
            ProductAPIResponse.Data productData = new ProductAPIResponse.Data();
            productData.setProducts(combinedProducts);
            combinedResponse.setData(productData);

            // Invia una sola risposta combinata
            productCallback.onSuccessFromRemote(combinedResponse, System.currentTimeMillis());
        }
    }

}
