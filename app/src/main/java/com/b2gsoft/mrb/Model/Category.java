package com.b2gsoft.mrb.Model;

import java.io.Serializable;
import java.util.List;

public class Category implements Serializable {

    int id;
    String name;
    double due;
    int image;
    List<SubCategory> subCategoryList;

    public Category(int id, String name, double due, List<SubCategory> subCategoryList) {
        this.id = id;
        this.name = name;
        this.due = due;
        this.subCategoryList = subCategoryList;
    }

    public Category(int id, String name, int image) {
        this.id = id;
        this.name = name;
        this.image = image;
    }

    public Category() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public double getDue() {
        return due;
    }

    public void setDue(double due) {
        this.due = due;
    }

    public List<SubCategory> getSubCategoryList() {
        return subCategoryList;
    }

    public void setSubCategoryList(List<SubCategory> subCategoryList) {
        this.subCategoryList = subCategoryList;
    }

    @Override
    public String toString() {
        return name;
    }
}
