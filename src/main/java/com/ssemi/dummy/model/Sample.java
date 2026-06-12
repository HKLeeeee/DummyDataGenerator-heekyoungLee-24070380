package com.ssemi.dummy.model;

/**
 * 시료(Sample) 도메인 모델
 * 반도체 시료의 기본 정보를 보유한다.
 */
public class Sample {

    private String id;               // 예: S-001
    private String name;             // 예: SiC 파워기판-6인치
    private double avgProductionTime; // 단위: min/ea
    private double yieldRate;        // 수율 (0.0 ~ 1.0)
    private int stock;               // 현재 재고 (ea)

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
