package com.ssemi.dummy;

import com.ssemi.dummy.factory.OrderFactory;
import com.ssemi.dummy.factory.SampleFactory;
import com.ssemi.dummy.model.Order;
import com.ssemi.dummy.model.Sample;
import com.ssemi.dummy.repository.JsonRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JsonRepository 단위 테스트 — 저장 후 재조회 무결성 검증
 */
class JsonRepositoryTest {

    @TempDir
    Path tempDir;

    private JsonRepository repository;
    private List<Sample> samples;
    private List<Order> orders;

    @BeforeEach
    void setUp() {
        repository = new JsonRepository(
                tempDir.resolve("samples.json").toString(),
                tempDir.resolve("orders.json").toString()
        );
        SampleFactory sf = new SampleFactory(7L);
        OrderFactory of = new OrderFactory(7L);
        samples = sf.generateDefaultSamples();
        orders = of.generateDefaultOrders(samples);
    }

    @AfterEach
    void tearDown() {
        repository.reset();
    }

    @Test
    @DisplayName("시료를 저장한 후 읽으면 동일한 건수가 반환되어야 한다")
    void saveSamples_thenLoad_shouldReturnSameCount() {
        repository.saveSamples(samples);
        List<Sample> loaded = repository.loadSamples();
        assertEquals(samples.size(), loaded.size(), "저장/조회 시료 건수 불일치");
    }

    @Test
    @DisplayName("주문을 저장한 후 읽으면 동일한 건수가 반환되어야 한다")
    void saveOrders_thenLoad_shouldReturnSameCount() {
        repository.saveOrders(orders);
        List<Order> loaded = repository.loadOrders();
        assertEquals(orders.size(), loaded.size(), "저장/조회 주문 건수 불일치");
    }

    @Test
    @DisplayName("저장된 시료의 핵심 필드가 일치해야 한다")
    void savedSample_fieldsShouldMatch() {
        repository.saveSamples(samples);
        List<Sample> loaded = repository.loadSamples();
        for (int i = 0; i < samples.size(); i++) {
            assertEquals(samples.get(i).getId(), loaded.get(i).getId());
            assertEquals(samples.get(i).getName(), loaded.get(i).getName());
            assertEquals(samples.get(i).getYieldRate(), loaded.get(i).getYieldRate(), 1e-9);
            assertEquals(samples.get(i).getAvgProductionTime(), loaded.get(i).getAvgProductionTime(), 1e-9);
            assertEquals(samples.get(i).getStock(), loaded.get(i).getStock());
        }
    }

    @Test
    @DisplayName("저장된 주문의 핵심 필드가 일치해야 한다")
    void savedOrder_fieldsShouldMatch() {
        repository.saveOrders(orders);
        List<Order> loaded = repository.loadOrders();
        for (int i = 0; i < orders.size(); i++) {
            assertEquals(orders.get(i).getOrderId(), loaded.get(i).getOrderId());
            assertEquals(orders.get(i).getSampleId(), loaded.get(i).getSampleId());
            assertEquals(orders.get(i).getStatus(), loaded.get(i).getStatus());
            assertEquals(orders.get(i).getQuantity(), loaded.get(i).getQuantity());
        }
    }

    @Test
    @DisplayName("reset() 호출 후 파일이 삭제되어야 한다")
    void reset_shouldDeleteFiles() {
        repository.saveSamples(samples);
        repository.saveOrders(orders);
        repository.reset();
        assertTrue(repository.loadSamples().isEmpty(), "reset 후 시료 목록이 비어야 한다");
        assertTrue(repository.loadOrders().isEmpty(), "reset 후 주문 목록이 비어야 한다");
    }

    @Test
    @DisplayName("파일이 없을 때 load는 빈 목록을 반환해야 한다")
    void loadWithNoFile_shouldReturnEmptyList() {
        assertTrue(repository.loadSamples().isEmpty());
        assertTrue(repository.loadOrders().isEmpty());
    }
}
