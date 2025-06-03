package com.unimib.wardrobe.adapter;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.unimib.wardrobe.R;
import com.unimib.wardrobe.model.Product;
import com.unimib.wardrobe.model.ProductCategory;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    private List<ProductCategory> categoryList;
    private int productItemLayout;

    public CategoryAdapter(List<ProductCategory> categoryList, int productItemLayout) {
        this.categoryList = categoryList;
        this.productItemLayout = productItemLayout;
    }

    public static class CategoryViewHolder extends RecyclerView.ViewHolder {
        TextView categoryTitle;
        RecyclerView recyclerView;

        public CategoryViewHolder(View itemView) {
            super(itemView);
            categoryTitle = itemView.findViewById(R.id.categoryTitle);
            recyclerView = itemView.findViewById(R.id.horizontalRecyclerView);
        }
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        ProductCategory category = categoryList.get(position);
        holder.categoryTitle.setText(category.getCategoryTitle());

        ProductRecycleAdapter productAdapter = new ProductRecycleAdapter(
                productItemLayout,
                category.getProductList(),
                true
        );

        holder.recyclerView.setLayoutManager(
                new LinearLayoutManager(holder.recyclerView.getContext(), LinearLayoutManager.HORIZONTAL, false));
        holder.recyclerView.setAdapter(productAdapter);
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }
}

