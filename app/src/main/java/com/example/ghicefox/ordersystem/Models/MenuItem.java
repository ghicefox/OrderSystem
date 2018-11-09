package com.example.ghicefox.ordersystem.Models;

public class MenuItem {
    private int Id;
    private String itemName;
    private int count;
    private int price;

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    private int orderId;

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }


    public void setCount(int count) {
        this.count = count;
    }

    public int getCount() {
        return count;
    }

    public MenuItem(String name,int price,int count,int Id,int orderId) {
        this.Id = Id;
        this.itemName = name;
        this.count = count;
        this.price = price;
        this.orderId = orderId;
    }
}
