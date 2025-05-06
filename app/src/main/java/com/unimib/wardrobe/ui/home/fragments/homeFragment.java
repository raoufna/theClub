package com.unimib.wardrobe.ui.home.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.unimib.wardrobe.R;
import com.unimib.wardrobe.adapter.ProductRecycleAdapter;
import com.unimib.wardrobe.database.ProductRoomDatabase;
import com.unimib.wardrobe.model.Product;
import com.unimib.wardrobe.model.ProductAPIResponse;
import com.unimib.wardrobe.model.Result;
import com.unimib.wardrobe.repository.product.ProductRepository;
import com.unimib.wardrobe.ui.home.viewmodel.ProductViewModel;
import com.unimib.wardrobe.ui.home.viewmodel.ProductViewModelFactory;
import com.unimib.wardrobe.util.ServiceLocator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class homeFragment extends Fragment {

    public static final String TAG = homeFragment.class.getName();
    private RecyclerView recyclerView;
    private CircularProgressIndicator circularProgressIndicator;
    private ProductRepository productRepository;
    private List<Product> productList;
    private ProductRecycleAdapter adapter;
    private ProductViewModel productViewModel;

    public homeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        productRepository  =
                ServiceLocator.getInstance().getProductsRepository(
                        requireActivity().getApplication(),
                        requireActivity().getApplication().getResources().getBoolean(R.bool.debug_mode)
                );
        productViewModel = new ViewModelProvider(
                requireActivity(),
                new ProductViewModelFactory(productRepository)).get(ProductViewModel.class);

        productList = new ArrayList<>();
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        recyclerView = view.findViewById(R.id.recycleView);
        circularProgressIndicator = view.findViewById(R.id.progressIndicator);

        // Impostiamo il layout della RecyclerView
        recyclerView.setLayoutManager(new GridLayoutManager(view.getContext(), 2));

        adapter = new ProductRecycleAdapter(R.layout.card_item, productList, true);
        recyclerView.setAdapter(adapter);

        // Mostriamo il progress indicator mentre carichiamo i dati
        circularProgressIndicator.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);

        List<String> searchTerms = Arrays.asList("jeans", "tshirt", "sneakers");

        SharedPreferences prefs = requireContext().getSharedPreferences("my_prefs", Context.MODE_PRIVATE);
        long lastUpdate = prefs.getLong("last_update_timestamp", 0);
        Log.d("ProductLocalDataSource", "DEBUG MIO ");

        productViewModel.fetchCombinedProducts(searchTerms, lastUpdate);
        productViewModel.getCombinedProducts().observe(getViewLifecycleOwner(), combinedResult -> {
                    if (combinedResult != null) {
                        Log.d("ProductLocalDataSource", "DEBUG tipo result: " + combinedResult.getClass().getSimpleName());
                    } else {
                        Log.d("ProductLocalDataSource", "DEBUG tipo result: NULL");
                    }
                    if (combinedResult instanceof Result.Success) {
                        ProductAPIResponse combinedResponse = ((Result.Success) combinedResult).getData();

                        if (combinedResponse != null) {
                            List<Product> allProducts = combinedResponse.getData().getProducts();

                            // Aggiungi i nuovi prodotti alla lista
                            Log.d("ProductLocalDataSource", "Prodotti ricevuti dalla LiveData: " + allProducts.size());
                            productList.clear();
                            productList.addAll(allProducts);

                            // Sincronizza i preferiti con Firebase e Room
                            syncFavoritesWithFirebaseAndRoom(allProducts);

                            // Aggiorna la RecyclerView
                            adapter.notifyDataSetChanged();

                            // Nascondi il progress indicator e mostra la RecyclerView
                            recyclerView.setVisibility(View.VISIBLE);
                            circularProgressIndicator.setVisibility(View.GONE);

                            Log.d("ProductLocalDataSource", "Dati caricati e aggiornati");
                        }
                    } else if (combinedResult instanceof Result.Error) {
                        Snackbar.make(view,
                                getString(R.string.error_retireving_articles),
                                Snackbar.LENGTH_SHORT).show();
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

                        // Sincronizza con i prodotti della lista
                        for (Product p : allProducts) {
                            if (p.getName().equals(productName)) {
                                p.setLiked(true);

                                // Sincronizza anche con Room
                                ProductRoomDatabase.getDatabase(requireContext())
                                        .ProductDao().insert(p);
                            }
                        }
                    }

                    // Dopo aver sincronizzato i preferiti, aggiorna la UI
                    adapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("FirebaseSync", "Errore nella lettura dei preferiti", error.toException());
                }
            });
        }
    }
    /*
    @Override
    public void onResume() {
        super.onResume();

        List<String> searchTerms = Arrays.asList("jeans", "tshirt", "sneakers");

        SharedPreferences prefs = requireContext().getSharedPreferences("my_prefs", Context.MODE_PRIVATE);
        long lastUpdate = prefs.getLong("last_update_timestamp", 0);
        productViewModel.getCombinedProducts(searchTerms, lastUpdate)
                .observe(getViewLifecycleOwner(), combinedResult -> {
                    if (combinedResult instanceof Result.Success) {
                        ProductAPIResponse combinedResponse = ((Result.Success) combinedResult).getData();

                        if (combinedResponse != null) {
                            List<Product> allProducts = combinedResponse.getData().getProducts();
                            Log.d("ProductLocalDataSource", "Prodotti ricevuti dalla LiveData: " + allProducts.size());

                            productList.clear();
                            productList.addAll(allProducts);

                            syncFavoritesWithFirebaseAndRoom(allProducts);
                            adapter.notifyDataSetChanged();

                            recyclerView.setVisibility(View.VISIBLE);
                            circularProgressIndicator.setVisibility(View.GONE);
                            Log.d("ProductLocalDataSource", "Dati caricati e aggiornati");
                        }
                    } else if (combinedResult instanceof Result.Error) {
                        recyclerView.setVisibility(View.GONE);
                        circularProgressIndicator.setVisibility(View.GONE);
                    }
                });
    }*/
}