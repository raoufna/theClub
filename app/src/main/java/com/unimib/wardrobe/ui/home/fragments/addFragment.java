package com.unimib.wardrobe.ui.home.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.unimib.wardrobe.R;
import com.unimib.wardrobe.model.Outfit;
import com.unimib.wardrobe.model.Product;
import com.unimib.wardrobe.ui.home.viewmodel.ProductViewModel;
import com.unimib.wardrobe.ui.home.viewmodel.ProductViewModelFactory;
import com.unimib.wardrobe.util.ServiceLocator;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class addFragment extends Fragment {
    private ImageView ivTshirt, ivJeans, ivSneakers;
    private ImageButton prevT, nextT, prevJ, nextJ, prevS, nextS;
    private Button btnLoad;
    private ProductViewModel productViewModel;

    // Liste e indici per lo scrolling manuale
    private List<Product> tshirts, jeans, sneakers;
    private int tIndex = 0, jIndex = 0, sIndex = 0;
    private final Random random = new Random();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 1) Trova le view
        ivTshirt = view.findViewById(R.id.ivTshirt);
        prevT = view.findViewById(R.id.btnPrevTshirt);
        nextT = view.findViewById(R.id.btnNextTshirt);
        ivJeans = view.findViewById(R.id.ivJeans);
        prevJ = view.findViewById(R.id.btnPrevJeans);
        nextJ = view.findViewById(R.id.btnNextJeans);
        ivSneakers = view.findViewById(R.id.ivSneakers);
        prevS = view.findViewById(R.id.btnPrevSneakers);
        nextS = view.findViewById(R.id.btnNextSneakers);
        btnLoad = view.findViewById(R.id.btnLoad);

        // 2) Inizializza subito il ViewModel CON la factory
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

        // 3) Osserva la lista dei preferiti una sola volta per popolare le liste
        productViewModel.getLikedProducts().observe(getViewLifecycleOwner(), new Observer<List<Product>>() {
            @Override
            public void onChanged(List<Product> all) {
                // Filtra per categoria
                tshirts = filterByTerm(all, "tshirt");
                jeans = filterByTerm(all, "jeans");
                sneakers = filterByTerm(all, "sneakers");

                // Genera il primo outfit solo dopo aver caricato i preferiti
                if (productViewModel.getOutfit().getValue() == null && all != null && !all.isEmpty()) {
                    productViewModel.generateOutfit();
                }
            }
        });

        // 4) Osserva l'Outfit generato per aggiornarlo dopo un click su "Genera" o al ritorno
        productViewModel.getOutfit().observe(getViewLifecycleOwner(), new Observer<Outfit>() {
            @Override
            public void onChanged(Outfit outfit) {
                // Mostra l’outfit (o placeholder se null)
                displayImage(outfit != null ? outfit.getTshirt() : null, ivTshirt);
                displayImage(outfit != null ? outfit.getJeans() : null, ivJeans);
                displayImage(outfit != null ? outfit.getSneakers() : null, ivSneakers);

                // Sincronizza gli indici al prodotto scelto
                if (outfit != null) {
                    tIndex = (tshirts != null && outfit.getTshirt() != null)
                            ? tshirts.indexOf(outfit.getTshirt()) : 0;
                    jIndex = (jeans != null && outfit.getJeans() != null)
                            ? jeans.indexOf(outfit.getJeans()) : 0;
                    sIndex = (sneakers != null && outfit.getSneakers() != null)
                            ? sneakers.indexOf(outfit.getSneakers()) : 0;
                }
            }
        });

        // 5) Bottone “Genera Outfit”
        btnLoad.setOnClickListener(v -> {
            if (FirebaseAuth.getInstance().getCurrentUser() == null) {
                Toast.makeText(requireContext(),
                        "Devi essere loggato", Toast.LENGTH_SHORT).show();
                return;
            }
            productViewModel.generateOutfit();
        });

        // 6) Freccette per scorrere manualmente
        prevT.setOnClickListener(v -> scrollList(ivTshirt, tshirts, -1));
        nextT.setOnClickListener(v -> scrollList(ivTshirt, tshirts, +1));
        prevJ.setOnClickListener(v -> scrollList(ivJeans, jeans, -1));
        nextJ.setOnClickListener(v -> scrollList(ivJeans, jeans, +1));
        prevS.setOnClickListener(v -> scrollList(ivSneakers, sneakers, -1));
        nextS.setOnClickListener(v -> scrollList(ivSneakers, sneakers, +1));
    }

    private List<Product> filterByTerm(List<Product> all, String term) {
        return (all == null)
                ? java.util.Collections.emptyList()
                : all.stream()
                .filter(p -> term.equalsIgnoreCase(p.getSearchTerm()))
                .collect(Collectors.toList());
    }

    private void displayImage(@Nullable Product p, ImageView iv) {
        if (p != null) {
            Glide.with(this)
                    .load(p.getFullImageUrl())
                    .placeholder(android.R.drawable.ic_menu_gallery)
                    .error(android.R.drawable.ic_delete)
                    .into(iv);
        } else {
            iv.setImageResource(android.R.drawable.ic_delete);
        }
    }

    // helper per scroll con freccia
    private void scrollList(ImageView iv, List<Product> list, int delta) {
        if (list != null && !list.isEmpty()) {
            if (iv == ivTshirt) tIndex = (tIndex + delta + list.size()) % list.size();
            else if (iv == ivJeans) jIndex = (jIndex + delta + list.size()) % list.size();
            else if (iv == ivSneakers) sIndex = (sIndex + delta + list.size()) % list.size();

            // scegli l'indice corretto
            int idx = iv == ivTshirt ? tIndex : iv == ivJeans ? jIndex : sIndex;
            displayImage(list.get(idx), iv);
        }
    }
}
