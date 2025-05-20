package com.unimib.wardrobe.ui.home.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;
import com.unimib.wardrobe.R;
import com.unimib.wardrobe.adapter.CategoryAdapter;
import com.unimib.wardrobe.database.ProductRoomDatabase;
import com.unimib.wardrobe.model.Product;
import com.unimib.wardrobe.model.ProductAPIResponse;
import com.unimib.wardrobe.model.ProductCategory;
import com.unimib.wardrobe.model.Result;
import com.unimib.wardrobe.repository.product.ProductRepository;
import com.unimib.wardrobe.ui.home.viewmodel.ProductViewModel;
import com.unimib.wardrobe.ui.home.viewmodel.ProductViewModelFactory;
import com.unimib.wardrobe.util.ServiceLocator;
import java.util.*;

public class homeFragment extends Fragment {

    private RecyclerView recyclerView;
    private CircularProgressIndicator circularProgressIndicator;
    private CategoryAdapter categoryAdapter;
    private List<ProductCategory> categoryList = new ArrayList<>();
    private ProductRepository productRepository;
    private ProductViewModel productViewModel;

    public homeFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        productRepository = ServiceLocator.getInstance().getProductsRepository(
                requireActivity().getApplication(),
                requireActivity().getApplication().getResources().getBoolean(R.bool.debug_mode)
        );
        productViewModel = new ViewModelProvider(
                requireActivity(),
                new ProductViewModelFactory(productRepository)).get(ProductViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        recyclerView = view.findViewById(R.id.recycleView);
        circularProgressIndicator = view.findViewById(R.id.progressIndicator);

        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        categoryAdapter = new CategoryAdapter(categoryList, R.layout.card_item);
        recyclerView.setAdapter(categoryAdapter);

        circularProgressIndicator.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);

        List<String> searchTerms = Arrays.asList("jeans", "tshirt", "sneakers");
        SharedPreferences prefs = requireContext().getSharedPreferences("my_prefs", Context.MODE_PRIVATE);
        long lastUpdate = prefs.getLong("last_update_timestamp", 0);

        productViewModel.fetchCombinedProducts(searchTerms, lastUpdate);
        productViewModel.getCombinedProducts(searchTerms, lastUpdate).observe(getViewLifecycleOwner(), combinedResult -> {
            if (combinedResult instanceof Result.Success) {
                ProductAPIResponse combinedResponse = ((Result.Success) combinedResult).getData();

                if (combinedResponse != null) {
                    List<Product> allProducts = combinedResponse.getData().getProducts();
                    categoryList.clear();

                    for (String term : searchTerms) {
                        List<Product> filtered = new ArrayList<>();
                        for (Product p : allProducts) {
                            if (p.getSearchTerm().equalsIgnoreCase(term)) {
                                filtered.add(p);
                            }
                        }
                        categoryList.add(new ProductCategory(term.toUpperCase(), filtered));
                    }

                    syncFavoritesWithFirebaseAndRoom(allProducts);
                    categoryAdapter.notifyDataSetChanged();
                    recyclerView.setVisibility(View.VISIBLE);
                    circularProgressIndicator.setVisibility(View.GONE);
                }
            } else {
                Snackbar.make(view, getString(R.string.error_retireving_articles), Snackbar.LENGTH_SHORT).show();
                recyclerView.setVisibility(View.GONE);
                circularProgressIndicator.setVisibility(View.GONE);
            }
        });

        return view;
    }

    private void syncFavoritesWithFirebaseAndRoom(List<Product> allProducts) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            DatabaseReference favoritesRef = FirebaseDatabase.getInstance()
                    .getReference("users")
                    .child(currentUser.getUid())
                    .child("preferiti");

            favoritesRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot productSnapshot : snapshot.getChildren()) {
                        String productName = productSnapshot.child("name").getValue(String.class);
                        for (Product p : allProducts) {
                            if (p.getName().equals(productName)) {
                                p.setLiked(true);
                                ProductRoomDatabase.getDatabase(requireContext()).ProductDao().insert(p);
                            }
                        }
                    }
                    categoryAdapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("FirebaseSync", "Errore nella lettura dei preferiti", error.toException());
                }
            });
        }
    }
}
