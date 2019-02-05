package com.efunhub.pickpriceadmin.Model;

/**
 * Created by Admin on 18-09-2018.
 */

public class DashboardItemModel {

    private String catName, catCount;
    private int catImage;

    public String getCatName() {
        return catName;
    }

    public void setCatName(String catName) {
        this.catName = catName;
    }

    public String getCatCount() {
        return catCount;
    }

    public void setCatCount(String catCount) {
        this.catCount = catCount;
    }

    public int getCatImage() {
        return catImage;
    }

    public void setCatImage(int catImage) {
        this.catImage = catImage;
    }
}
