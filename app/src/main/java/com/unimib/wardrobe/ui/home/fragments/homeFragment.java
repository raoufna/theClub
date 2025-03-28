package com.unimib.wardrobe.ui.home.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.snackbar.Snackbar;
import com.unimib.wardrobe.R;
import com.unimib.wardrobe.adapter.ProductRecycleAdapter;
import com.unimib.wardrobe.model.Product;
import com.unimib.wardrobe.model.ProductAPIResponse;
import com.unimib.wardrobe.repository.IProductRepository;
import com.unimib.wardrobe.repository.ProductAPIRepository;
import com.unimib.wardrobe.repository.ProductMockRepository;
import com.unimib.wardrobe.util.Constants;
import com.unimib.wardrobe.util.JSONParserUtils;
import com.unimib.wardrobe.util.ResponseCallback;

import java.io.IOException;
import java.sql.Array;
import java.util.ArrayList;
import java.util.List;

public class homeFragment extends Fragment implements ResponseCallback {

    public static final String TAG = homeFragment.class.getName();
    private RecyclerView recyclerView;
    private CircularProgressIndicator circularProgressIndicator;
    private IProductRepository productRepository;
    private List<Product> productList;
    private ProductRecycleAdapter adapter;


    public homeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        productList = new ArrayList<>();

        if(requireActivity().getResources().getBoolean(R.bool.debug_mode)){
            productRepository = new ProductMockRepository(requireActivity().getApplication(), this);
        }else{
            productRepository = new ProductAPIRepository(requireActivity().getApplication(), this);
        }
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        recyclerView = view.findViewById(R.id.recycleView);
        circularProgressIndicator = view.findViewById(R.id.progressIndicator);

        //recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        recyclerView.setLayoutManager(new GridLayoutManager(view.getContext(), 2));

        adapter = new ProductRecycleAdapter(R.layout.card_item,
                productList, true);

        //productRepository.getFavoriteProduct();

        recyclerView.setAdapter(adapter);

        productRepository.fetchProduct("jeans", 10, 1000);

        return view;
    }

    @Override
    public void onSuccess(List<Product> newProductList, long lastUpdate) {
        productList.clear();
        productList.addAll(newProductList);
        requireActivity().runOnUiThread(new Runnable(){
                @Override
                public void run(){
                    adapter.notifyDataSetChanged();
                    recyclerView.setVisibility(View.VISIBLE);
                    circularProgressIndicator.setVisibility(View.GONE);
                }
        });
    }

    @Override
    public void onFailure(String errorMessage) {
        Log.e("API_ERROR", "Errore nella chiamata API: " + errorMessage);
        Snackbar.make(recyclerView, errorMessage, Snackbar.LENGTH_LONG).show();
    }

}
