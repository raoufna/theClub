package com.unimib.wardrobe.ui.home.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.unimib.wardrobe.R;
import com.unimib.wardrobe.model.Product;
import com.unimib.wardrobe.model.ProductAPIResponse;
import com.unimib.wardrobe.util.Constants;
import com.unimib.wardrobe.util.JSONParserUtils;

import java.io.IOException;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link homeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class homeFragment extends Fragment {

    public static final String TAG = homeFragment.class.getName();
    public homeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        JSONParserUtils jsonParserUtils = new JSONParserUtils(getContext());
        try {
            ProductAPIResponse response = jsonParserUtils.parseJSONFileWithGSon(Constants.JsonWardrobe);
            List<Product> productList = response.getProducts();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return view;
    }
}