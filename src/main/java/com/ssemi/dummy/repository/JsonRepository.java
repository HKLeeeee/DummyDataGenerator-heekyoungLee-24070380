package com.ssemi.dummy.repository;

import com.ssemi.dummy.model.Order;
import com.ssemi.dummy.model.Sample;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

public class JsonRepository {

    private final String samplesPath;
    private final String ordersPath;

    public JsonRepository(String samplesPath, String ordersPath) {
        this.samplesPath = samplesPath;
        this.ordersPath = ordersPath;
    }

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

    public void reset() {
        deleteIfExists(samplesPath);
        deleteIfExists(ordersPath);
    }

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

    /** 단순 단일 depth 객체({...})만 지원 — 중첩 구조 없음 */
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
        // 이스케이프된 \" 를 값의 끝으로 오인하지 않도록 \\ 선행 여부를 검사한다
        int end = start;
        while (end < obj.length()) {
            int quote = obj.indexOf("\"", end);
            if (quote < 0) return "";
            if (quote > 0 && obj.charAt(quote - 1) == '\\') {
                end = quote + 1;
            } else {
                end = quote;
                break;
            }
        }
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
            // Files.deleteIfExists는 삭제 대상 파일이 없을 때 예외를 던지지 않으므로
            // 여기에 도달하는 경우는 OS 레벨 권한 오류뿐이며 초기화 목적상 무시한다
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
