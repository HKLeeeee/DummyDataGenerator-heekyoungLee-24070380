package com.ssemi.dummy.repository;

import com.ssemi.dummy.model.Order;
import com.ssemi.dummy.model.Sample;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

/**
 * JSON 파일 기반 저장소
 *
 * 외부 라이브러리 없이 직접 JSON 직렬화/역직렬화를 수행한다.
 * PoC 2(DataPersistence) 와 동일한 파일 스키마를 따른다.
 */
public class JsonRepository {

    private final String samplesPath;
    private final String ordersPath;

    public JsonRepository(String samplesPath, String ordersPath) {
        this.samplesPath = samplesPath;
        this.ordersPath = ordersPath;
    }

    // -----------------------------------------------------------------------
    // Sample CRUD
    // -----------------------------------------------------------------------

    public void saveSamples(List<Sample> samples) {
        ensureParentDir(samplesPath);
        StringBuilder sb = new StringBuilder("[\n");
        for (int i = 0; i < samples.size(); i++) {
            sb.append(toJson(samples.get(i)));
            if (i < samples.size() - 1) sb.append(",");
            sb.append("\n");
        }
        sb.append("]");
        writeFile(samplesPath, sb.toString());
    }

    public List<Sample> loadSamples() {
        String json = readFile(samplesPath);
        if (json == null || json.isBlank()) return new ArrayList<>();
        return parseSamples(json);
    }

    // -----------------------------------------------------------------------
    // Order CRUD
    // -----------------------------------------------------------------------

    public void saveOrders(List<Order> orders) {
        ensureParentDir(ordersPath);
        StringBuilder sb = new StringBuilder("[\n");
        for (int i = 0; i < orders.size(); i++) {
            sb.append(toJson(orders.get(i)));
            if (i < orders.size() - 1) sb.append(",");
            sb.append("\n");
        }
        sb.append("]");
        writeFile(ordersPath, sb.toString());
    }

    public List<Order> loadOrders() {
        String json = readFile(ordersPath);
        if (json == null || json.isBlank()) return new ArrayList<>();
        return parseOrders(json);
    }

    // -----------------------------------------------------------------------
    // Reset
    // -----------------------------------------------------------------------

    /** 파일 삭제. 파일이 없으면 무시한다. */
    public void reset() {
        deleteIfExists(samplesPath);
        deleteIfExists(ordersPath);
    }

    // -----------------------------------------------------------------------
    // Serialization helpers
    // -----------------------------------------------------------------------

    private String toJson(Sample s) {
        return "  {"
                + "\"id\":\"" + escape(s.getId()) + "\","
                + "\"name\":\"" + escape(s.getName()) + "\","
                + "\"avgProductionTime\":" + s.getAvgProductionTime() + ","
                + "\"yieldRate\":" + s.getYieldRate() + ","
                + "\"stock\":" + s.getStock()
                + "}";
    }

    private String toJson(Order o) {
        return "  {"
                + "\"orderId\":\"" + escape(o.getOrderId()) + "\","
                + "\"sampleId\":\"" + escape(o.getSampleId()) + "\","
                + "\"customerName\":\"" + escape(o.getCustomerName()) + "\","
                + "\"quantity\":" + o.getQuantity() + ","
                + "\"status\":\"" + escape(o.getStatus()) + "\""
                + "}";
    }

    // -----------------------------------------------------------------------
    // Parsing helpers — simple line-by-line object parser
    // -----------------------------------------------------------------------

    private List<Sample> parseSamples(String json) {
        List<Sample> list = new ArrayList<>();
        for (String obj : splitObjects(json)) {
            Sample s = new Sample();
            s.setId(extractString(obj, "id"));
            s.setName(extractString(obj, "name"));
            s.setAvgProductionTime(extractDouble(obj, "avgProductionTime"));
            s.setYieldRate(extractDouble(obj, "yieldRate"));
            s.setStock(extractInt(obj, "stock"));
            list.add(s);
        }
        return list;
    }

    private List<Order> parseOrders(String json) {
        List<Order> list = new ArrayList<>();
        for (String obj : splitObjects(json)) {
            Order o = new Order();
            o.setOrderId(extractString(obj, "orderId"));
            o.setSampleId(extractString(obj, "sampleId"));
            o.setCustomerName(extractString(obj, "customerName"));
            o.setQuantity(extractInt(obj, "quantity"));
            o.setStatus(extractString(obj, "status"));
            list.add(o);
        }
        return list;
    }

    /**
     * JSON 배열 문자열에서 각 객체({...})를 추출한다.
     * 단순 단일 depth 객체만 지원 (중첩 없음).
     */
    private List<String> splitObjects(String json) {
        List<String> objects = new ArrayList<>();
        int depth = 0;
        int start = -1;
        for (int i = 0; i < json.length(); i++) {
            char c = json.charAt(i);
            if (c == '{') {
                if (depth == 0) start = i;
                depth++;
            } else if (c == '}') {
                depth--;
                if (depth == 0 && start >= 0) {
                    objects.add(json.substring(start, i + 1));
                    start = -1;
                }
            }
        }
        return objects;
    }

    private String extractString(String obj, String key) {
        String pattern = "\"" + key + "\":\"";
        int idx = obj.indexOf(pattern);
        if (idx < 0) return "";
        int start = idx + pattern.length();
        int end = obj.indexOf("\"", start);
        if (end < 0) return "";
        return unescape(obj.substring(start, end));
    }

    private double extractDouble(String obj, String key) {
        String pattern = "\"" + key + "\":";
        int idx = obj.indexOf(pattern);
        if (idx < 0) return 0.0;
        int start = idx + pattern.length();
        int end = start;
        while (end < obj.length() && (Character.isDigit(obj.charAt(end)) || obj.charAt(end) == '.' || obj.charAt(end) == '-')) {
            end++;
        }
        try {
            return Double.parseDouble(obj.substring(start, end));
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    private int extractInt(String obj, String key) {
        return (int) Math.round(extractDouble(obj, key));
    }

    private String escape(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    private String unescape(String s) {
        if (s == null) return "";
        return s.replace("\\\"", "\"").replace("\\\\", "\\");
    }

    // -----------------------------------------------------------------------
    // File I/O helpers
    // -----------------------------------------------------------------------

    private void writeFile(String path, String content) {
        try {
            Files.writeString(Path.of(path), content, StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException("파일 저장 실패: " + path, e);
        }
    }

    private String readFile(String path) {
        try {
            Path p = Path.of(path);
            if (!Files.exists(p)) return null;
            return Files.readString(p, StandardCharsets.UTF_8);
        } catch (IOException e) {
            return null;
        }
    }

    private void deleteIfExists(String path) {
        try {
            Files.deleteIfExists(Path.of(path));
        } catch (IOException e) {
            // 무시
        }
    }

    private void ensureParentDir(String path) {
        try {
            Path parent = Path.of(path).getParent();
            if (parent != null) Files.createDirectories(parent);
        } catch (IOException e) {
            throw new RuntimeException("디렉토리 생성 실패: " + path, e);
        }
    }
}
