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
        Random random = new Random();

        // T-shirt
        productViewModel.getFavoriteProductsBySearchTerm("tshirt")
                .observe(getViewLifecycleOwner(), list -> {
                    if (list != null && !list.isEmpty()) {
                        Product chosen = list.get(random.nextInt(list.size()));
                        loadImage(chosen.getFullImageUrl(), ivTshirt);
                    } else {
                        ivTshirt.setImageResource(android.R.drawable.ic_delete); // la tua X rossa
                    }
                });

        // Jeans
        productViewModel.getFavoriteProductsBySearchTerm("jeans")
                .observe(getViewLifecycleOwner(), list -> {
                    if (list != null && !list.isEmpty()) {
                        Product chosen = list.get(random.nextInt(list.size()));
                        loadImage(chosen.getFullImageUrl(), ivJeans);
                    } else {
                        ivJeans.setImageResource(android.R.drawable.ic_delete);
                    }
                });

        // Sneakers
        productViewModel.getFavoriteProductsBySearchTerm("sneakers")
                .observe(getViewLifecycleOwner(), list -> {
                    if (list != null && !list.isEmpty()) {
                        Product chosen = list.get(random.nextInt(list.size()));
                        loadImage(chosen.getFullImageUrl(), ivSneakers);
                    } else {
                        ivSneakers.setImageResource(android.R.drawable.ic_delete);
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