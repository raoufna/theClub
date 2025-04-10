package com.unimib.wardrobe.ui.home.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.unimib.wardrobe.repository.ProductRepository;

public class ProductViewModelFactory implements ViewModelProvider.Factory{
    private final ProductRepository productRepository;

    public ProductViewModelFactory(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new ProductViewModel(productRepository);
    }
}
