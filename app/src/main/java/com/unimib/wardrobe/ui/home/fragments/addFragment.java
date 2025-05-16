package com.unimib.wardrobe.ui.home.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.unimib.wardrobe.R;
import com.unimib.wardrobe.model.Product;
import com.unimib.wardrobe.ui.home.viewmodel.ProductViewModel;
import com.unimib.wardrobe.ui.home.viewmodel.ProductViewModelFactory;
import com.unimib.wardrobe.util.ServiceLocator;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class addFragment extends Fragment {
        private ImageView ivTshirt, ivJeans, ivSneakers;
        private Button btnLoad;
        private ProductViewModel productViewModel;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            return inflater.inflate(R.layout.fragment_add, container, false);
        }

        @Override
        public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);

            ivTshirt = view.findViewById(R.id.ivTshirt);
            ivJeans = view.findViewById(R.id.ivJeans);
            ivSneakers = view.findViewById(R.id.ivSneakers);
            btnLoad = view.findViewById(R.id.btnLoad);

            productViewModel = new ViewModelProvider(
                    requireActivity(),
                    new ProductViewModelFactory(
                            ServiceLocator.getInstance()
                                    .getProductsRepository(
                                            requireActivity().getApplication(),
                                            requireActivity().getResources().getBoolean(R.bool.debug_mode)
                                    )
                    )
            ).get(ProductViewModel.class);

            btnLoad.setOnClickListener(v -> loadFavoriteProducts());
        }

        private void loadFavoriteProducts() {
            // Carica T-Shirt preferiti
            productViewModel.getFavoriteProductsBySearchTerm("tshirt").observe(getViewLifecycleOwner(), products -> {
                if (products != null && !products.isEmpty()) {
                    loadImage(products.get(0).getImageUrl(), ivTshirt);
                }
            });

            // Carica Jeans preferiti
            productViewModel.getFavoriteProductsBySearchTerm("jeans").observe(getViewLifecycleOwner(), products -> {
                if (products != null && !products.isEmpty()) {
                    loadImage(products.get(0).getImageUrl(), ivJeans);
                }
            });

            // Carica Sneakers preferiti
            productViewModel.getFavoriteProductsBySearchTerm("sneakers").observe(getViewLifecycleOwner(), products -> {
                if (products != null && !products.isEmpty()) {
                    loadImage(products.get(0).getImageUrl(), ivSneakers);
                }
            });
        }

        private void loadImage(String imageUrl, ImageView imageView) {
            Glide.with(requireContext())
                    .load(imageUrl)
                    .placeholder(android.R.drawable.ic_menu_gallery)
                    .error(android.R.drawable.ic_delete)
                    .into(imageView);
        }
    }