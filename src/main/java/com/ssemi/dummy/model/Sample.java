package com.ssemi.dummy.model;

public class Sample {

    private String id;
    private String name;
    private double avgProductionTime;
    private double yieldRate;
    private int stock;

    public Sample() {}

    public Sample(String id, String name, double avgProductionTime, double yieldRate, int stock) {
        this.id = id;
        this.name = name;
        this.avgProductionTime = avgProductionTime;
        this.yieldRate = yieldRate;
        this.stock = stock;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public double getAvgProductionTime() { return avgProductionTime; }
    public void setAvgProductionTime(double avgProductionTime) { this.avgProductionTime = avgProductionTime; }

    public double getYieldRate() { return yieldRate; }
    public void setYieldRate(double yieldRate) { this.yieldRate = yieldRate; }

    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }

    @Override
    public String toString() {
        return "Sample{id='" + id + "', name='" + name + "', avgProductionTime=" + avgProductionTime
                + ", yieldRate=" + yieldRate + ", stock=" + stock + "}";
    }
}
