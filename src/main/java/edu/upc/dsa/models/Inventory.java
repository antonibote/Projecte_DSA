package edu.upc.dsa.models;

public class Inventory {
    //String idPurchase;
    public String idItem;
    public String idUser;
    public Inventory() {}
    public Inventory(String idItem, String idUser)
    {
        this.idItem = idItem;
        this.idUser = idUser;
    }

    public String getIdItem()
    {
        return idItem;
    }
    public String getIdUser()
    {
        return idUser;
    }
    public void setidItem(String idItem){
        this.idItem = idItem;
    }
    public void setidUser(String idUser)
    {
        this.idUser = idUser;
    }
    /**public void setQuantity(int quantity){ this.quantity=quantity;}**/
}
