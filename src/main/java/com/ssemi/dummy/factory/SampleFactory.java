package com.ssemi.dummy.factory;

import com.ssemi.dummy.model.Sample;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SampleFactory {

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

    private static final double MIN_AVG_PRODUCTION_TIME = 0.2;
    private static final double MAX_AVG_PRODUCTION_TIME = 1.0;
    private static final double MIN_YIELD = 0.70;
    private static final double MAX_YIELD = 0.99;
    private static final int DEFAULT_SAMPLE_COUNT = 5;
    private static final int MIN_STOCK = 10;
    private static final int MAX_STOCK = 200;
    // 재고 고갈(0) 확률을 10%로 고정하기 위한 분모
    private static final int STOCK_DEPLETION_DENOMINATOR = 10;

    private final Random random;

    public SampleFactory(long seed) {
        this.random = new Random(seed);
    }

    public List<Sample> generateDefaultSamples() {
        return generateSamples(DEFAULT_SAMPLE_COUNT);
    }

    public List<Sample> generateSamples(int count) {
        List<Sample> result = new ArrayList<>();
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

    /** count가 후보 수를 초과하면 순환 사용 */
    private List<String> buildNamePool(int count) {
        List<String> pool = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            pool.add(SEMICONDUCTOR_NAMES[i % SEMICONDUCTOR_NAMES.length]);
        }
        return pool;
    }

    private int generateStock() {
        if (random.nextInt(STOCK_DEPLETION_DENOMINATOR) == 0) return 0;
        return MIN_STOCK + random.nextInt(MAX_STOCK - MIN_STOCK + 1);
    }

    private double roundTwoDecimals(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
