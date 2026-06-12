package com.ssemi.dummy.factory;

import com.ssemi.dummy.model.Sample;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 시료(Sample) 더미 데이터 팩토리
 *
 * - 반도체 도메인에 현실적인 시료명을 사용한다.
 * - avgProductionTime: 0.2 ~ 1.0 min/ea
 * - yieldRate: 0.70 ~ 0.99
 * - stock: 0 포함 다양하게 생성
 */
public class SampleFactory {

    // 반도체 시료 이름 후보 (실제 산업에서 사용하는 품목)
    private static final String[] SEMICONDUCTOR_NAMES = {
            "실리콘 웨이퍼-8인치",
            "GaN 에피택셜-4인치",
            "SiC 파워기판-6인치",
            "포토레지스트-PR7",
            "산화막 웨이퍼-SiO2",
            "실리콘 웨이퍼-12인치",
            "InP 기판-3인치",
            "GaAs 에피택셜-6인치",
            "질화규소막-Si3N4",
            "SOI 웨이퍼-8인치",
            "사파이어 기판-4인치",
            "Ge 기판-6인치"
    };

    // 최소 평균 생산시간 (min/ea)
    private static final double MIN_AVG_PRODUCTION_TIME = 0.2;
    // 최대 평균 생산시간 (min/ea)
    private static final double MAX_AVG_PRODUCTION_TIME = 1.0;
    // 최소 수율
    private static final double MIN_YIELD = 0.70;
    // 최대 수율
    private static final double MAX_YIELD = 0.99;
    // 기본 생성 시료 수
    private static final int DEFAULT_SAMPLE_COUNT = 5;

    private final Random random;

    public SampleFactory(long seed) {
        this.random = new Random(seed);
    }

    /**
     * 기본 시료 목록 생성 (5종)
     */
    public List<Sample> generateDefaultSamples() {
        return generateSamples(DEFAULT_SAMPLE_COUNT);
    }

    /**
     * 지정한 개수만큼 시료 생성
     *
     * @param count 생성 개수
     */
    public List<Sample> generateSamples(int count) {
        List<Sample> result = new ArrayList<>();
        // 이름 후보 인덱스를 섞어서 순서대로 배정 (중복 방지)
        List<String> namePool = buildNamePool(count);

        for (int i = 0; i < count; i++) {
            String id = String.format("S-%03d", i + 1);
            String name = namePool.get(i);
            double avgTime = roundTwoDecimals(
                    MIN_AVG_PRODUCTION_TIME
                            + random.nextDouble() * (MAX_AVG_PRODUCTION_TIME - MIN_AVG_PRODUCTION_TIME));
            double yieldRate = roundTwoDecimals(
                    MIN_YIELD + random.nextDouble() * (MAX_YIELD - MIN_YIELD));
            int stock = generateStock();

            result.add(new Sample(id, name, avgTime, yieldRate, stock));
        }
        return result;
    }

    // -----------------------------------------------------------------------

    /** 이름 풀 빌드: count가 후보 수를 초과하면 순환 사용 */
    private List<String> buildNamePool(int count) {
        List<String> pool = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            pool.add(SEMICONDUCTOR_NAMES[i % SEMICONDUCTOR_NAMES.length]);
        }
        return pool;
    }

    /** 재고: 0~200 범위, 0 포함 다양하게 생성 */
    private int generateStock() {
        int roll = random.nextInt(10);
        if (roll == 0) return 0;           // 10% 확률 고갈
        return 10 + random.nextInt(191);   // 10~200
    }

    private double roundTwoDecimals(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
