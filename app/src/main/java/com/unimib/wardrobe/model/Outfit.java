package com.unimib.wardrobe.model;


public class Outfit {
    private final Product tshirt, jeans, sneakers;

    public Outfit(Product tshirt, Product jeans, Product sneakers) {
        this.tshirt = tshirt;
        this.jeans = jeans;
        this.sneakers = sneakers;
    }

    public Product getTshirt()  { return tshirt; }
    public Product getJeans()   { return jeans; }
    public Product getSneakers(){ return sneakers; }
}

