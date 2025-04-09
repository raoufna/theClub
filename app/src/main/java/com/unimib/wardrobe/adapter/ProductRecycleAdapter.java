package com.unimib.wardrobe.adapter;

import android.content.Context;
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

    public interface OnItemClickListener {
        void onFavoriteButtonPressed(int position);
    }
        private int layout;
        private List<Product> productList;
        private boolean heartVisible;
        private Context context;
        //private final OnItemClickListener onItemClickListener;

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

                //FavoriteCheckBox.setOnClickListener(this);
                //view.setOnClickListener(this);
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

            public CheckBox getFavoriteCheckbox() { return FavoriteCheckBox; }

            /*@Override
            public void onClick(View v) {
                if (v.getId() == R.id.favoriteButton) {
                    //setImageViewFavoriteNews(!newsList.get(getAdapterPosition()).isFavorite());
                    OnItemClickListener.onFavoriteButtonPressed(getAdapterPosition());
                }
            }*/
        }


        public ProductRecycleAdapter(int layout, List<Product> productList, boolean heartVisible/*, OnItemClickListener onItemClickListener*/) {
            this.layout = layout;
            this.productList = productList;
            this.heartVisible = heartVisible;
            //this.onItemClickListener = onItemClickListener;
        }

        // Create new views (invoked by the layout manager)
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            // Create a new view, which defines the UI of the list item
            View view = LayoutInflater.from(viewGroup.getContext())
                    .inflate(layout, viewGroup, false);

            if (this.context == null) this.context = viewGroup.getContext();

            return new ViewHolder(view);
        }

        // Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(ViewHolder viewHolder, final int position) {
            Glide.with(viewHolder.getImageView().getContext())
                    .load(productList.get(position).getFullImageUrl())
                    .into(viewHolder.ImageItem);
            viewHolder.getTextViewTitle().setText(productList.get(position).getName());
            viewHolder.getTextViewDescription().setText(productList.get(position).getBrandName());
            viewHolder.getFavoriteCheckBox().setChecked(productList.get(position).getLiked());
            viewHolder.getFavoriteCheckBox().setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    Product currentProduct = productList.get(viewHolder.getAdapterPosition());
                    if (b){
                        currentProduct.setLiked(b);
                        ProductRoomDatabase.getDatabase(viewHolder.getTextViewDescription().getContext()).
                                ProductDao().insert(currentProduct);

                    }else{
                        ProductRoomDatabase.getDatabase(viewHolder.getTextViewDescription().getContext()).
                                ProductDao().delete(productList.get(position));

                    }
                }
            });

        }

        // Return the size of your dataset (invoked by the layout manager)
        @Override
        public int getItemCount() {
            return productList.size();
        }
    }
