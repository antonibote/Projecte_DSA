package edu.upc.dsa.models;

public class Item {
    String idItem;
    String name;
    String description;
    int price;
    String image;

    public Item() {}

    public Item(String idItem,String name, String description, int price, String image){
        this.idItem = idItem;
        this.name = name;
        this.description = description;
        this.setPrice(price);
        this.image = image;
    }

    public String getIdItem() {
        return idItem;
    }
    public void setIdItem(String idItem) {
        this.idItem = idItem;
    }
    public String getName(){return name;}
    public void setName(String name){this.name = name;}
    public String getDescription(){return description;}
    public void setDescription(String description){this.description=description;}
    public int getPrice(){return price;}
    public void setPrice(int price){this.price = price;}
    public String getImage() {
        return image;
    }
    public void setImage(String image) {
        this.image = image;
    }
}
