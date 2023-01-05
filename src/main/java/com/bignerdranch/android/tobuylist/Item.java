package com.bignerdranch.android.tobuylist;

import java.util.Date;
import java.util.UUID;

public class Item { // model object class

    // instance variables
    private UUID mId;
    private String mName;
    private Date mDate;
    private Date mTime;
    private int mQuantity;
    private boolean mBought;
    private String mHelper;

    // constructor
    public Item() {
        this(UUID.randomUUID());
    }

    public Item(UUID id) {
        mId = id;
        mDate = new Date();
        mTime = new Date();
        mQuantity = 1;
    }

    // getters and setters
    public UUID getId() {
        return mId;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public Date getDate() {
        return mDate;
    }

    public void setDate(Date date) {
        mDate = date;
    }

    public Date getTime() { return mTime; }

    public void setTime(Date time) { mTime = time; }

    public int getQuantity() {
        return mQuantity;
    }

    public void setQuantity(int quantity) {
        mQuantity = quantity;
    }

    public boolean isBought() {
        return mBought;
    }

    public void setBought(Boolean bought) {
        mBought = bought;
    }

    public String getHelper() {
        return mHelper;
    }

    public void setHelper(String helper) {
        mHelper = helper;
    }

    public String getPhotoFilename() {
        return "IMG_" + getId().toString() + ".jpg";
    }
}
