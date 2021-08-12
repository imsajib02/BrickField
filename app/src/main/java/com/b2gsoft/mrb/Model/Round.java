package com.b2gsoft.mrb.Model;

/**
 * Created by shafi on 13-Sep-20.
 */

public class Round {

    int id;
    String name;
    String startDate;
    String endDate;
    String totalDays;
    String remainingDays;

    public Round(int id, String name, String startDate, String endDate, String totalDays, String remainingDays) {
        this.id = id;
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
        this.totalDays = totalDays;
        this.remainingDays = remainingDays;
    }

    public Round() {
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

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getTotalDays() {
        return totalDays;
    }

    public void setTotalDays(String totalDays) {
        this.totalDays = totalDays;
    }

    public String getRemainingDays() {
        return remainingDays;
    }

    public void setRemainingDays(String remainingDays) {
        this.remainingDays = remainingDays;
    }
}
