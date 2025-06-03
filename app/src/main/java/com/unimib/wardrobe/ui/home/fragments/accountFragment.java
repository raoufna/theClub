package com.unimib.wardrobe.ui.home.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.unimib.wardrobe.R;
import com.unimib.wardrobe.adapter.ProductRecycleAdapter;
import com.unimib.wardrobe.database.ProductDAO;
import com.unimib.wardrobe.database.ProductRoomDatabase;
import com.unimib.wardrobe.model.Product;
import com.unimib.wardrobe.model.ProductAPIResponse;
import com.unimib.wardrobe.repository.product.ProductRepository;
import com.unimib.wardrobe.ui.home.viewmodel.ProductViewModel;
import com.unimib.wardrobe.ui.home.viewmodel.ProductViewModelFactory;
import com.unimib.wardrobe.ui.welcome.LoginActivity;
import com.unimib.wardrobe.util.Constants;
import com.unimib.wardrobe.util.JSONParserUtils;
import com.unimib.wardrobe.util.ServiceLocator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class accountFragment extends Fragment {
    private TextView emailTextView, profileInitialView; // TextView per mostrare l'email e l'iniziale
    private FirebaseUser currentUser;   // per ottenere l'email e l'iniziale da Firebase

    private ProductRecycleAdapter adapter;
    private List<Product> productList = new ArrayList<>();
    private ProductViewModel productViewModel;

    public accountFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ProductRepository productRepository =
                ServiceLocator.getInstance().getProductsRepository(
                        requireActivity().getApplication(),
                        requireActivity().getResources().getBoolean(R.bool.debug_mode)
                );

        productViewModel = new ViewModelProvider(
                requireActivity(),
                new ProductViewModelFactory(productRepository)
        ).get(ProductViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_account, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.recycleView);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));

        adapter = new ProductRecycleAdapter(R.layout.card_item_favourites, productList, false);
        recyclerView.setAdapter(adapter);

        // 🔄 Osserva i preferiti da Room
        productViewModel.getLikedProducts().observe(getViewLifecycleOwner(), products -> {
            productList.clear();
            productList.addAll(products);
            adapter.notifyDataSetChanged();
        });

        emailTextView = view.findViewById(R.id.email_text);
        profileInitialView = view.findViewById(R.id.profile_initial);

        // salvo l'user da Firebase
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {
            String email = currentUser.getEmail();
            emailTextView.setText(email); // setto l'email nel TextView

            // Set initial (uppercase first letter)
            if (email != null && !email.isEmpty()) {
                char initial = Character.toUpperCase(email.charAt(0));
                profileInitialView.setText(String.valueOf(initial)); // setto lettera iniziale
            } else {
                profileInitialView.setText("?"); // set di default
            }
        }

        // Logout button click listener
        LinearLayout logoutRow = view.findViewById(R.id.logout_row);
        logoutRow.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut(); //log out effettivo

            // Optional: Redirect to login activity
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            getActivity().finish();
        });
        return view;
    }
}
