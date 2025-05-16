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

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
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
                Product product = productList.get(position);
            Log.d("ADAPTER", "Pos: " + position + " - Name: " + product.getName() + " - Term: " + product.getSearchTerm());

                viewHolder.getTextViewTitle().setText(product.getName());
                viewHolder.getTextViewDescription().setText(product.getBrandName());

                Glide.with(viewHolder.getImageView().getContext())
                        .load(product.getFullImageUrl())
                        .into(viewHolder.getImageView());

                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user == null) return;

                String uid = user.getUid();
                String productId = product.getName().replace(" ", "_"); // Meglio se usi un vero ID

                DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference()
                        .child("users").child(uid).child("preferiti").child(productId);

                // 🔄 Disabilita listener temporaneamente per evitare cicli
                viewHolder.getFavoriteCheckBox().setOnCheckedChangeListener(null);

                // 📡 Controlla se il prodotto è nei preferiti su Firebase
                dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        boolean isFavorite = snapshot.exists();
                        viewHolder.getFavoriteCheckBox().setChecked(isFavorite);
                        product.setLiked(isFavorite);

                        // ✅ Listener CUORE
                        viewHolder.getFavoriteCheckBox().setOnCheckedChangeListener((buttonView, isChecked) -> {
                            product.setLiked(isChecked);
                            Context context = viewHolder.getTextViewDescription().getContext();

                            if (isChecked) {
                                // ✅ Salva su Firebase
                                dbRef.setValue(product)
                                        .addOnSuccessListener(unused -> Log.d("FIREBASE", "Salvato su Firebase"))
                                        .addOnFailureListener(e -> Log.e("FIREBASE", "Errore Firebase", e));

                                // ✅ Salva su Room
                                ProductRoomDatabase.getDatabase(context).ProductDao().insert(product);
                            } else {
                                // ❌ Rimuovi da Firebase
                                dbRef.removeValue()
                                        .addOnSuccessListener(unused -> Log.d("FIREBASE", "Rimosso da Firebase"))
                                        .addOnFailureListener(e -> Log.e("FIREBASE", "Errore Firebase", e));

                                // ❌ Rimuovi da Room
                                ProductRoomDatabase.getDatabase(context).ProductDao().delete(product);
                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("FIREBASE", "Errore lettura preferiti", error.toException());
                    }
                });
            }


            // Return the size of your dataset (invoked by the layout manager)
        @Override
        public int getItemCount() {
            return productList.size();
        }
    }
