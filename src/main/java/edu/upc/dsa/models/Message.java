package edu.upc.dsa.models;
public class Message {
    String message;
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }

    public Message(){

    }
    public Message(String message){
        setMessage(message);
    }
}