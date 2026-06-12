package com.ssemi.dummy.factory;

import com.ssemi.dummy.model.Order;
import com.ssemi.dummy.model.OrderStatus;
import com.ssemi.dummy.model.Sample;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 주문(Order) 더미 데이터 팩토리
 *
 * - 주문번호 형식: ORD-YYYYMMDD-NNNN
 * - 등록된 시료 ID 범위 내에서만 sampleId 배정
 * - 기본 생성 시 5가지 상태(RESERVED/CONFIRMED/PRODUCING/RELEASE/REJECTED) 각 1건 이상 포함
 */
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

    private static final OrderStatus[] ALL_STATUSES = OrderStatus.values();
    // 기본 생성 10건의 상태 분포: 5가지 상태 각 최소 1건 보장
    private static final OrderStatus[] GUARANTEED_STATUSES = {
            OrderStatus.RESERVED,
            OrderStatus.CONFIRMED,
            OrderStatus.PRODUCING,
            OrderStatus.RELEASE,
            OrderStatus.REJECTED
    };

    private static final int DEFAULT_ORDER_COUNT = 10;
    private static final int MIN_QUANTITY = 10;
    private static final int MAX_QUANTITY = 500;

    // 주문 날짜 기준: 오늘 날짜 사용
    private static final String ORDER_DATE = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));

    private final Random random;

    public OrderFactory(long seed) {
        this.random = new Random(seed);
    }

    /**
     * 기본 주문 목록 생성 (10건, 5가지 상태 각 최소 1건)
     */
    public List<Order> generateDefaultOrders(List<Sample> registeredSamples) {
        return generateOrders(registeredSamples, DEFAULT_ORDER_COUNT);
    }

    /**
     * 지정한 개수만큼 주문 생성
     * count >= 5 인 경우 5가지 상태 모두 포함되도록 보장한다.
     *
     * @param registeredSamples 유효한 시료 목록 (도메인 제약: 등록된 시료 ID만 허용)
     * @param count             생성 개수
     */
    public List<Order> generateOrders(List<Sample> registeredSamples, int count) {
        if (registeredSamples == null || registeredSamples.isEmpty()) {
            throw new IllegalArgumentException("등록된 시료 목록이 비어 있습니다.");
        }

        List<Order> result = new ArrayList<>();

        // count >= 5면 앞 5건에 상태 배분 보장
        int guaranteedCount = Math.min(GUARANTEED_STATUSES.length, count);
        // 상태 배정 목록 구성
        List<OrderStatus> statusPlan = buildStatusPlan(count, guaranteedCount);

        for (int i = 0; i < count; i++) {
            String orderId = String.format("ORD-%s-%04d", ORDER_DATE, i + 1);
            String sampleId = pickSampleId(registeredSamples);
            String customerName = CUSTOMER_NAMES[random.nextInt(CUSTOMER_NAMES.length)];
            int quantity = MIN_QUANTITY + random.nextInt(MAX_QUANTITY - MIN_QUANTITY + 1);
            OrderStatus status = statusPlan.get(i);

            result.add(new Order(orderId, sampleId, customerName, quantity, status));
        }
        return result;
    }

    // -----------------------------------------------------------------------

    /**
     * 상태 배정 계획: 앞 guaranteedCount 자리에 5가지 상태를 순서대로 배치,
     * 나머지는 무작위 배정.
     */
    private List<OrderStatus> buildStatusPlan(int total, int guaranteedCount) {
        List<OrderStatus> plan = new ArrayList<>();
        // 보장 상태 추가
        for (int i = 0; i < guaranteedCount; i++) {
            plan.add(GUARANTEED_STATUSES[i]);
        }
        // 나머지 무작위
        for (int i = guaranteedCount; i < total; i++) {
            plan.add(ALL_STATUSES[random.nextInt(ALL_STATUSES.length)]);
        }
        return plan;
    }

    private String pickSampleId(List<Sample> samples) {
        return samples.get(random.nextInt(samples.size())).getId();
    }
}
