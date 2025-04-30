package com.unimib.wardrobe.ui.home.fragments;

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
import com.unimib.wardrobe.model.Result;
import com.unimib.wardrobe.repository.product.ProductRepository;
import com.unimib.wardrobe.ui.home.viewmodel.ProductViewModel;
import com.unimib.wardrobe.ui.home.viewmodel.ProductViewModelFactory;
import com.unimib.wardrobe.util.ServiceLocator;

import java.util.ArrayList;
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

        //recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        recyclerView.setLayoutManager(new GridLayoutManager(view.getContext(), 2));

        adapter = new ProductRecycleAdapter(R.layout.card_item,
                productList, true/*, new ProductRecycleAdapter.OnItemClickListener() {*/




         );
         /*@Override
            public void onFavoriteButtonPressed(int position) {
                productList.get(position).setLiked(!productList.get(position).getLiked());
                ProductViewModel.updateProduct(productList.get(position));
            }*/

        //productRepository.getFavoriteProduct();
        circularProgressIndicator.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);

        recyclerView.setAdapter(adapter);

      productRepository.fetchProducts("jeans", 10, 1000);



        String lastUpdate = System.currentTimeMillis() + "";
        Log.d("homeFragment", "Chiamata API iniziata");
        productViewModel.getProducts("jeans", Long.parseLong(lastUpdate)).observe(getViewLifecycleOwner(),
                result -> {
                    Log.d("homeFragment", "Risultato della chiamata API ricevuto");
                    if (result.isSuccess()) {
                        int initialSize = this.productList.size();
                        this.productList.clear();
                        this.productList.addAll(((Result.Success) result).getData().getData().getProducts());
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

                                        for (Product p : productList) {
                                            if (p.getName().equals((productName))) {
                                                p.setLiked(true);

                                                // Sincronizza anche su Room
                                                ProductRoomDatabase.getDatabase(requireContext())
                                                        .ProductDao().insert(p);
                                            }
                                        }
                                    }
                                    adapter.notifyDataSetChanged();
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Log.e("FirebaseSync", "Errore nella lettura dei preferiti", error.toException());
                                }
                            });
                        }
                        adapter.notifyItemRangeInserted(initialSize, this.productList.size());
                        recyclerView.setVisibility(View.VISIBLE);
                        circularProgressIndicator.setVisibility(View.GONE);
                        Log.d("homeFragment", "Dati caricati, cerchiolino nascosto");
                    } else {
                        Snackbar.make(view,
                                getString(R.string.error_retireving_articles),
                                Snackbar.LENGTH_SHORT).show();
                    }
                });

        return view;
    }
}
