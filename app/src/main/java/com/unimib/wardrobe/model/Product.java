package com.unimib.wardrobe.model;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Objects;

@Entity
public class Product {
    @PrimaryKey(autoGenerate = true)
    public long uid;

    private String name;
    private String brandName;
    private String imageUrl;
    private boolean liked;

    public Product(){
    }
    public long getUid() {
        return uid;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public boolean getLiked() {
        return liked;
    }

    public void setLiked(boolean liked) {
        this.liked = liked;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
    public String getFullImageUrl() {
        if (imageUrl != null && !imageUrl.startsWith("http")) {
            return "https://" + imageUrl;
        }
        return imageUrl;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return Objects.equals(name, product.name) && Objects.equals(brandName, product.brandName) && Objects.equals(imageUrl, product.imageUrl);
    }
}



