package com.example.shoppy.Model;

public class Cart {
    private String Name;
    private String Price;
    private String Quantity;
    private String Id;

    public Cart() {
    }

    public Cart(String name, String price, String quantity, String id) {
        Name = name;
        Price = price;
        Quantity = quantity;
        Id = id;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getPrice() {
        return Price;
    }

    public void setPrice(String price) {
        Price = price;
    }

    public String getQuantity() {
        return Quantity;
    }

    public void setQuantity(String quantity) {
        Quantity = quantity;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }
}