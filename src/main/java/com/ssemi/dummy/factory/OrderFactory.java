package com.ssemi.dummy.factory;

import com.ssemi.dummy.model.Order;
import com.ssemi.dummy.model.OrderStatus;
import com.ssemi.dummy.model.Sample;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class OrderFactory {

    private static final String[] CUSTOMER_NAMES = {
            "삼성전자 반도체연구소",
            "SK하이닉스 공정개발팀",
            "LG이노텍 기판사업부",
            "현대오토에버 전력반도체팀",
            "DB하이텍 파운드리사업부",
            "원익IPS 소재연구팀",
            "아이씨텍 MEMS연구소",
            "에스앤에스텍 웨이퍼가공팀",
            "TSMC Korea 기술지원팀",
            "인텔코리아 소자개발팀",
            "마이크론코리아 패키징팀",
            "키옥시아코리아 메모리사업부"
    };

    // 5가지 상태 각 1건 이상을 보장하는 고정 순서 배열
    private static final OrderStatus[] GUARANTEED_STATUSES = {
            OrderStatus.RESERVED,
            OrderStatus.CONFIRMED,
            OrderStatus.PRODUCING,
            OrderStatus.RELEASE,
            OrderStatus.REJECTED
    };
    private static final OrderStatus[] ALL_STATUSES = OrderStatus.values();

    private static final int DEFAULT_ORDER_COUNT = 10;
    private static final int MIN_QUANTITY = 10;
    private static final int MAX_QUANTITY = 500;

    private static final String ORDER_DATE =
            LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));

    private final Random random;

    public OrderFactory(long seed) {
        this.random = new Random(seed);
    }

    public List<Order> generateDefaultOrders(List<Sample> registeredSamples) {
        return generateOrders(registeredSamples, DEFAULT_ORDER_COUNT);
    }

    /**
     * count >= 5 인 경우 5가지 상태(RESERVED/CONFIRMED/PRODUCING/RELEASE/REJECTED)가
     * 모두 포함되도록 보장한다.
     */
    public List<Order> generateOrders(List<Sample> registeredSamples, int count) {
        if (registeredSamples == null || registeredSamples.isEmpty()) {
            throw new IllegalArgumentException("등록된 시료 목록이 비어 있습니다.");
        }

        List<OrderStatus> statusPlan = buildStatusPlan(count);
        List<Order> result = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            String orderId = String.format("ORD-%s-%04d", ORDER_DATE, i + 1);
            String sampleId = pickSampleId(registeredSamples);
            String customerName = CUSTOMER_NAMES[random.nextInt(CUSTOMER_NAMES.length)];
            int quantity = MIN_QUANTITY + random.nextInt(MAX_QUANTITY - MIN_QUANTITY + 1);

            result.add(new Order(orderId, sampleId, customerName, quantity, statusPlan.get(i)));
        }
        return result;
    }

    private List<OrderStatus> buildStatusPlan(int total) {
        List<OrderStatus> plan = new ArrayList<>();
        int guaranteedCount = Math.min(GUARANTEED_STATUSES.length, total);
        for (int i = 0; i < guaranteedCount; i++) {
            plan.add(GUARANTEED_STATUSES[i]);
        }
        for (int i = guaranteedCount; i < total; i++) {
            plan.add(ALL_STATUSES[random.nextInt(ALL_STATUSES.length)]);
        }
        return plan;
    }

    private String pickSampleId(List<Sample> samples) {
        return samples.get(random.nextInt(samples.size())).getId();
    }
}
