package com.ssemi.dummy;

import com.ssemi.dummy.factory.OrderFactory;
import com.ssemi.dummy.factory.SampleFactory;
import com.ssemi.dummy.model.Order;
import com.ssemi.dummy.model.Sample;
import com.ssemi.dummy.repository.JsonRepository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DummyDataService {

    private static final String SAMPLES_PATH = "data/samples.json";
    private static final String ORDERS_PATH  = "data/orders.json";

    // 재현성을 위한 고정 기본 시드 — 동일 환경에서 동일 결과를 보장한다
    private static final long DEFAULT_SEED = 42L;

    private final JsonRepository repository;

    public DummyDataService() {
        this.repository = new JsonRepository(SAMPLES_PATH, ORDERS_PATH);
    }

    public void generateDefault() {
        generate(5, 10, DEFAULT_SEED);
    }

    public void generateBulk() {
        generate(10, 50, DEFAULT_SEED);
    }

    public void generate(int sampleCount, int orderCount, long seed) {
        // SampleFactory와 OrderFactory에 파생 시드를 부여해 난수 열의 독립성을 보장한다
        SampleFactory sf = new SampleFactory(seed);
        OrderFactory of = new OrderFactory(seed + 1);

        List<Sample> samples = sf.generateSamples(sampleCount);
        List<Order>  orders  = of.generateOrders(samples, orderCount);

        repository.saveSamples(samples);
        repository.saveOrders(orders);

        printSummary(samples, orders, seed);
    }

    public void reset() {
        repository.reset();
        System.out.println("[초기화] " + SAMPLES_PATH + ", " + ORDERS_PATH + " 삭제 완료");
    }

    private void printSummary(List<Sample> samples, List<Order> orders, long seed) {
        System.out.println("==========================================");
        System.out.println("  Dummy 데이터 생성 완료");
        System.out.println("==========================================");
        System.out.printf("  랜덤 시드  : %d%n", seed);
        System.out.printf("  시료 저장  : %s (%d건)%n", SAMPLES_PATH, samples.size());
        System.out.println();
        System.out.println("  [시료 목록]");
        for (Sample s : samples) {
            System.out.printf("    %-6s | %-24s | 생산시간 %.2f min/ea | 수율 %.0f%% | 재고 %3d ea%n",
                    s.getId(), s.getName(),
                    s.getAvgProductionTime(),
                    s.getYieldRate() * 100,
                    s.getStock());
        }
        System.out.println();
        System.out.printf("  주문 저장  : %s (%d건)%n", ORDERS_PATH, orders.size());
        System.out.println();
        System.out.println("  [상태별 주문 건수]");
        Map<String, Long> statusCount = orders.stream()
                .collect(Collectors.groupingBy(Order::getStatus, Collectors.counting()));
        String[] statusOrder = {"RESERVED", "CONFIRMED", "PRODUCING", "RELEASE", "REJECTED"};
        for (String st : statusOrder) {
            System.out.printf("    %-10s : %d건%n", st, statusCount.getOrDefault(st, 0L));
        }
        System.out.println("==========================================");
    }
}
