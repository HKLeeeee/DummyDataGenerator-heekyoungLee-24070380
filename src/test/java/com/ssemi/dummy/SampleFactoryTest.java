package com.ssemi.dummy;

import com.ssemi.dummy.factory.SampleFactory;
import com.ssemi.dummy.model.Sample;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * SampleFactory 단위 테스트
 * TDD: 팩토리 구현 전에 기대 동작을 명세한다.
 */
class SampleFactoryTest {

    private SampleFactory factory;

    @BeforeEach
    void setUp() {
        factory = new SampleFactory(42L); // 재현성을 위한 고정 시드
    }

    @Test
    @DisplayName("기본 시료 5종이 생성되어야 한다")
    void generateDefaultSamples_shouldReturnAtLeastFiveSamples() {
        List<Sample> samples = factory.generateDefaultSamples();
        assertTrue(samples.size() >= 5, "기본 시료는 5종 이상이어야 한다");
    }

    @Test
    @DisplayName("시료 ID는 S-NNN 형식이어야 한다")
    void sampleId_shouldMatchFormat() {
        List<Sample> samples = factory.generateDefaultSamples();
        for (Sample s : samples) {
            assertNotNull(s.getId(), "시료 ID는 null이 아니어야 한다");
            assertTrue(s.getId().matches("S-\\d{3}"),
                    "시료 ID 형식 위반: " + s.getId());
        }
    }

    @Test
    @DisplayName("수율은 0.0 이상 1.0 이하이어야 한다")
    void yieldRate_shouldBeInValidRange() {
        List<Sample> samples = factory.generateDefaultSamples();
        for (Sample s : samples) {
            double yr = s.getYieldRate();
            assertTrue(yr >= 0.0 && yr <= 1.0,
                    "수율 범위 위반: " + yr + " (시료: " + s.getId() + ")");
        }
    }

    @Test
    @DisplayName("평균 생산시간은 0.2 이상 1.0 이하이어야 한다")
    void avgProductionTime_shouldBeInValidRange() {
        List<Sample> samples = factory.generateDefaultSamples();
        for (Sample s : samples) {
            double apt = s.getAvgProductionTime();
            assertTrue(apt >= 0.2 && apt <= 1.0,
                    "평균 생산시간 범위 위반: " + apt);
        }
    }

    @Test
    @DisplayName("재고는 0 이상이어야 한다")
    void stock_shouldBeNonNegative() {
        List<Sample> samples = factory.generateDefaultSamples();
        for (Sample s : samples) {
            assertTrue(s.getStock() >= 0, "재고 음수 위반: " + s.getStock());
        }
    }

    @Test
    @DisplayName("시료 이름은 반도체 관련 이름이어야 한다 (null/빈 값 불가)")
    void sampleName_shouldNotBeNullOrEmpty() {
        List<Sample> samples = factory.generateDefaultSamples();
        for (Sample s : samples) {
            assertNotNull(s.getName(), "시료 이름은 null이 아니어야 한다");
            assertFalse(s.getName().isBlank(), "시료 이름은 빈 값이 아니어야 한다");
        }
    }

    @Test
    @DisplayName("대량 생성 시 지정한 개수만큼 시료가 생성되어야 한다")
    void generateBulkSamples_shouldReturnRequestedCount() {
        List<Sample> samples = factory.generateSamples(10);
        assertEquals(10, samples.size(), "요청한 개수와 생성 개수가 일치해야 한다");
    }

    @Test
    @DisplayName("동일 시드를 사용하면 동일한 데이터가 생성되어야 한다 (재현성)")
    void sameSeed_shouldProduceSameData() {
        SampleFactory f1 = new SampleFactory(99L);
        SampleFactory f2 = new SampleFactory(99L);
        List<Sample> s1 = f1.generateDefaultSamples();
        List<Sample> s2 = f2.generateDefaultSamples();
        assertEquals(s1.size(), s2.size());
        for (int i = 0; i < s1.size(); i++) {
            assertEquals(s1.get(i).getId(), s2.get(i).getId());
            assertEquals(s1.get(i).getName(), s2.get(i).getName());
            assertEquals(s1.get(i).getYieldRate(), s2.get(i).getYieldRate());
        }
    }
}
