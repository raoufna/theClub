package com.unimib.wardrobe.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;
import com.unimib.wardrobe.model.Product;
import com.unimib.wardrobe.R;
import com.unimib.wardrobe.model.Product;

import java.util.List;


public class ProductRecycleAdapter extends RecyclerView.Adapter<ProductRecycleAdapter.ViewHolder> {

        private int layout;
        private List<Product> productList;

        public static class ViewHolder extends RecyclerView.ViewHolder {
            private final TextView TextViewTitle;
            private final TextView TextViewDescription;

            public ViewHolder(View view) {
                super(view);
                // Define click listener for the ViewHolder's View

                TextViewTitle = view.findViewById(R.id.TextViewTitle);
                TextViewDescription = view.findViewById(R.id.TextViewDescription);
            }

            public TextView getTextViewTitle() {
                return TextViewTitle;
            }

            public TextView getTextViewDescription() {
                return TextViewDescription;
            }
        }


        public ProductRecycleAdapter(int layout, List<Product> productList) {
            this.layout = layout;
            this.productList = productList;
        }

        // Create new views (invoked by the layout manager)
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            // Create a new view, which defines the UI of the list item
            View view = LayoutInflater.from(viewGroup.getContext())
                    .inflate(layout, viewGroup, false);

            return new ViewHolder(view);
        }

        // Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(ViewHolder viewHolder, final int position) {
            viewHolder.getTextViewTitle().setText(productList.get(position).getName());
            viewHolder.getTextViewDescription().setText(productList.get(position).getBrandName());
        }

        // Return the size of your dataset (invoked by the layout manager)
        @Override
        public int getItemCount() {
            return productList.size();
        }
    }
