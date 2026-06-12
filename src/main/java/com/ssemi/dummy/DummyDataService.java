package com.ssemi.dummy;

import com.ssemi.dummy.factory.OrderFactory;
import com.ssemi.dummy.factory.SampleFactory;
import com.ssemi.dummy.model.Order;
import com.ssemi.dummy.model.Sample;
import com.ssemi.dummy.repository.JsonRepository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Dummy 데이터 생성 서비스
 * 팩토리, 저장소를 조합하여 데이터 생성/초기화/요약 출력을 담당한다.
 */
public class DummyDataService {

    private static final String SAMPLES_PATH = "data/samples.json";
    private static final String ORDERS_PATH  = "data/orders.json";

    private final JsonRepository repository;

    public DummyDataService() {
        this.repository = new JsonRepository(SAMPLES_PATH, ORDERS_PATH);
    }

    // -----------------------------------------------------------------------
    // Public API
    // -----------------------------------------------------------------------

    /**
     * 기본 더미 데이터 생성 (시료 5종, 주문 10건, 랜덤 시드)
     */
    public void generateDefault() {
        long seed = System.currentTimeMillis();
        generate(5, 10, seed);
    }

    /**
     * 대량 더미 데이터 생성 (시료 10종, 주문 50건, 랜덤 시드)
     */
    public void generateBulk() {
        long seed = System.currentTimeMillis();
        generate(10, 50, seed);
    }

    /**
     * 지정 옵션으로 더미 데이터 생성
     *
     * @param sampleCount  시료 생성 수
     * @param orderCount   주문 생성 수
     * @param seed         랜덤 시드 (재현성)
     */
    public void generate(int sampleCount, int orderCount, long seed) {
        SampleFactory sf = new SampleFactory(seed);
        OrderFactory of = new OrderFactory(seed);

        List<Sample> samples = sf.generateSamples(sampleCount);
        List<Order>  orders  = of.generateOrders(samples, orderCount);

        repository.saveSamples(samples);
        repository.saveOrders(orders);

        printSummary(samples, orders, seed);
    }

    /**
     * 저장된 데이터 파일 삭제 (초기화)
     */
    public void reset() {
        repository.reset();
        System.out.println("[초기화] data/samples.json, data/orders.json 삭제 완료");
    }

    // -----------------------------------------------------------------------
    // Private helpers
    // -----------------------------------------------------------------------

    private void printSummary(List<Sample> samples, List<Order> orders, long seed) {
        System.out.println("==========================================");
        System.out.println("  Dummy 데이터 생성 완료");
        System.out.println("==========================================");
        System.out.printf("  랜덤 시드  : %d%n", seed);
        System.out.printf("  시료 저장  : %s (%d건)%n", "data/samples.json", samples.size());
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
        System.out.printf("  주문 저장  : %s (%d건)%n", "data/orders.json", orders.size());
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
