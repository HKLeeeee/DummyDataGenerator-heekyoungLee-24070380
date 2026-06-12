package com.ssemi.dummy;

import com.ssemi.dummy.factory.OrderFactory;
import com.ssemi.dummy.factory.SampleFactory;
import com.ssemi.dummy.model.Order;
import com.ssemi.dummy.model.OrderStatus;
import com.ssemi.dummy.model.Sample;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

/**
 * OrderFactory 단위 테스트
 */
class OrderFactoryTest {

    private List<Sample> samples;
    private OrderFactory factory;

    @BeforeEach
    void setUp() {
        samples = new SampleFactory(42L).generateDefaultSamples();
        factory = new OrderFactory(42L);
    }

    @Test
    @DisplayName("기본 주문 10건이 생성되어야 한다")
    void generateDefaultOrders_shouldReturnAtLeastTenOrders() {
        List<Order> orders = factory.generateDefaultOrders(samples);
        assertTrue(orders.size() >= 10, "기본 주문은 10건 이상이어야 한다");
    }

    @Test
    @DisplayName("주문번호는 ORD-YYYYMMDD-NNNN 형식이어야 한다")
    void orderId_shouldMatchFormat() {
        List<Order> orders = factory.generateDefaultOrders(samples);
        for (Order o : orders) {
            assertNotNull(o.getOrderId());
            assertTrue(o.getOrderId().matches("ORD-\\d{8}-\\d{4}"),
                    "주문번호 형식 위반: " + o.getOrderId());
        }
    }

    @Test
    @DisplayName("주문에 사용된 시료 ID는 등록된 시료 ID여야 한다")
    void order_sampleId_shouldBeRegistered() {
        List<Order> orders = factory.generateDefaultOrders(samples);
        Set<String> validIds = samples.stream().map(Sample::getId).collect(Collectors.toSet());
        for (Order o : orders) {
            assertTrue(validIds.contains(o.getSampleId()),
                    "미등록 시료 ID 사용: " + o.getSampleId());
        }
    }

    @Test
    @DisplayName("주문 수량은 1 이상이어야 한다")
    void orderQuantity_shouldBePositive() {
        List<Order> orders = factory.generateDefaultOrders(samples);
        for (Order o : orders) {
            assertTrue(o.getQuantity() >= 1, "주문 수량 위반: " + o.getQuantity());
        }
    }

    @Test
    @DisplayName("모든 주문 상태가 유효한 OrderStatus 값이어야 한다")
    void orderStatus_shouldBeValid() {
        List<Order> orders = factory.generateDefaultOrders(samples);
        Set<String> validStatuses = Arrays.stream(OrderStatus.values())
                .map(Enum::name)
                .collect(Collectors.toSet());
        for (Order o : orders) {
            assertTrue(validStatuses.contains(o.getStatus()),
                    "유효하지 않은 상태값: " + o.getStatus());
        }
    }

    @Test
    @DisplayName("기본 생성 시 5가지 상태가 모두 포함되어야 한다")
    void defaultOrders_shouldCoverAllStatuses() {
        List<Order> orders = factory.generateDefaultOrders(samples);
        Set<String> statuses = orders.stream().map(Order::getStatus).collect(Collectors.toSet());
        for (OrderStatus s : OrderStatus.values()) {
            assertTrue(statuses.contains(s.name()),
                    "누락된 상태: " + s.name());
        }
    }

    @Test
    @DisplayName("고객명은 null 또는 빈 값이 아니어야 한다")
    void customerName_shouldNotBeNullOrEmpty() {
        List<Order> orders = factory.generateDefaultOrders(samples);
        for (Order o : orders) {
            assertNotNull(o.getCustomerName());
            assertFalse(o.getCustomerName().isBlank(), "고객명이 비어 있음");
        }
    }

    @Test
    @DisplayName("대량 생성 시 지정한 개수만큼 주문이 생성되어야 한다")
    void generateBulkOrders_shouldReturnRequestedCount() {
        List<Order> orders = factory.generateOrders(samples, 50);
        assertEquals(50, orders.size());
    }
}
