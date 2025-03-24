package com.unimib.wardrobe.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.unimib.wardrobe.database.ProductRoomDatabase;
import com.unimib.wardrobe.model.Product;
import com.unimib.wardrobe.R;
import com.unimib.wardrobe.model.Product;

import java.util.List;


public class ProductRecycleAdapter extends RecyclerView.Adapter<ProductRecycleAdapter.ViewHolder> {

        private int layout;
        private List<Product> productList;
        private boolean heartVisible;

        public static class ViewHolder extends RecyclerView.ViewHolder {
            private final TextView TextViewTitle;
            private final TextView TextViewDescription;
            private final ImageView ImageItem;
            private final CheckBox FavoriteCheckBox;

            public ViewHolder(View view) {
                super(view);
                // Define click listener for the ViewHolder's View

                TextViewTitle = view.findViewById(R.id.TextViewTitle);
                TextViewDescription = view.findViewById(R.id.TextViewDescription);
                ImageItem = view.findViewById(R.id.ImageItem);
                FavoriteCheckBox = view.findViewById(R.id.favoriteButton);
            }

            public TextView getTextViewTitle() {
                return TextViewTitle;
            }

            public TextView getTextViewDescription() {
                return TextViewDescription;
            }

            public ImageView getImageView() {
                return ImageItem;
            }
            public CheckBox getFavoriteCheckBox() {
                return FavoriteCheckBox;
            }
        }


        public ProductRecycleAdapter(int layout, List<Product> productList, boolean heartVisible) {
            this.layout = layout;
            this.productList = productList;
            this.heartVisible = heartVisible;
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
            Glide.with(viewHolder.getImageView().getContext())
                    .load(productList.get(position).getImageUrl())
                    .into(viewHolder.ImageItem);
            viewHolder.getTextViewTitle().setText(productList.get(position).getName());
            viewHolder.getTextViewDescription().setText(productList.get(position).getBrandName());
            viewHolder.getFavoriteCheckBox().setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if (b){
                        ProductRoomDatabase.getDatabase(viewHolder.getTextViewDescription().getContext()).
                                ProductDao().insertAll(productList.get(position));

                    }else{
                        ProductRoomDatabase.getDatabase(viewHolder.getTextViewDescription().getContext()).
                                ProductDao().delete(productList.get(position));

                    }
                }
            });

            if(heartVisible == false){
                viewHolder.getFavoriteCheckBox().setVisibility(View.INVISIBLE);
            }
        }

        // Return the size of your dataset (invoked by the layout manager)
        @Override
        public int getItemCount() {
            return productList.size();
        }
    }
