package com.meet.shared_inventory.Models;

import com.google.firebase.database.Exclude;

public class itemModel {
    String itemName, Qty;
    @Exclude
    private String key;

    itemModel(){}

    public itemModel(String itemName, String qty) {
        this.itemName = itemName;
        Qty = qty;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getQty() {
        return Qty;
    }

    public void setQty(String qty) {
        Qty = qty;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
