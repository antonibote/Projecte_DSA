package edu.upc.dsa.models;

public class Insignias {
    String name;
    String image;
    public Insignias() {}
    public Insignias(String name, String image)
    {
        this.name = name;
        this.image = image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public String getName() {
        return name;
    }
}
