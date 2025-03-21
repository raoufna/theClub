package com.unimib.wardrobe.ui.home.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.unimib.wardrobe.R;
import com.unimib.wardrobe.adapter.ProductRecycleAdapter;
import com.unimib.wardrobe.model.Product;
import com.unimib.wardrobe.model.ProductAPIResponse;
import com.unimib.wardrobe.util.Constants;
import com.unimib.wardrobe.util.JSONParserUtils;

import java.io.IOException;
import java.util.List;

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

        RecyclerView recyclerView = view.findViewById(R.id.recycleView);
        //recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        recyclerView.setLayoutManager(new GridLayoutManager(view.getContext(), 2));
        JSONParserUtils jsonParserUtils = new JSONParserUtils(getContext());
        try {
            ProductAPIResponse response = jsonParserUtils.parseJSONFileWithGSon(Constants.JsonWardrobe);
            List<Product> productList = response.getProducts();

            ProductRecycleAdapter adapter = new ProductRecycleAdapter(R.layout.card_item, productList);

            recyclerView.setAdapter(adapter);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return view;
    }
}