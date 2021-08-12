package com.b2gsoft.mrb.Model;

import java.io.Serializable;

/**
 * Created by imsajib02 on 31-Aug-20.
 */

public class SubCategory implements Serializable {

    int id;
    double due;
    double potTotal;
    String name;

    public SubCategory(int id, String name, double due, double potTotal) {
        this.id = id;
        this.name = name;
        this.due = due;
        this.potTotal = potTotal;
    }

    public SubCategory() {
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

    public double getDue() {
        return due;
    }

    public void setDue(double due) {
        this.due = due;
    }

    public double getPotTotal() {
        return potTotal;
    }

    public void setPotTotal(double potTotal) {
        this.potTotal = potTotal;
    }

    @Override
    public String toString() {
        return name;
    }
}
