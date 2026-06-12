package com.ssemi.dummy.model;

/**
 * 주문(Order) 도메인 모델
 */
public class Order {

    private String orderId;        // 예: ORD-20260612-0001
    private String sampleId;       // 등록된 시료 ID
    private String customerName;   // 고객명
    private int quantity;          // 주문 수량
    private String status;         // OrderStatus 문자열

    public Order() {}

    public Order(String orderId, String sampleId, String customerName, int quantity, OrderStatus status) {
        this.orderId = orderId;
        this.sampleId = sampleId;
        this.customerName = customerName;
        this.quantity = quantity;
        this.status = status.name();
    }

    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }

    public String getSampleId() { return sampleId; }
    public void setSampleId(String sampleId) { this.sampleId = sampleId; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    @Override
    public String toString() {
        return "Order{orderId='" + orderId + "', sampleId='" + sampleId
                + "', customerName='" + customerName + "', quantity=" + quantity
                + ", status='" + status + "'}";
    }
}
