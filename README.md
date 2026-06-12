# DummyDataGenerator — S-Semi PoC 4

반도체 시료 생산주문관리 시스템(SampleOrderSystem)의 PoC 4.
테스트용 Dummy 데이터를 생성하여 JSON 파일(영속 저장소)에 저장하는 CLI 도구.

---

## 사용법

### 빌드 및 실행

```bash
# 테스트 실행
./gradlew test

# 애플리케이션 실행 (대화형 메뉴)
./gradlew run
```

### 메뉴 구성

```
[1] 기본 Dummy 데이터 생성 (시료 5종, 주문 10건)
[2] 대량 Dummy 데이터 생성 (시료 10종, 주문 50건)
[3] 데이터 초기화 (파일 삭제)
[0] 종료
```

### 출력 경로

| 파일 | 내용 |
|---|---|
| `data/samples.json` | 시료 목록 |
| `data/orders.json` | 주문 목록 |

---

## 생성 데이터 스펙

### 시료 (Sample)

| 필드 | 설명 | 범위/형식 |
|---|---|---|
| `id` | 시료 고유 ID | `S-001`, `S-002`, ... |
| `name` | 시료명 (반도체 실제 품목) | 예: `SiC 파워기판-6인치` |
| `avgProductionTime` | 평균 생산시간 (min/ea) | 0.20 ~ 1.00 |
| `yieldRate` | 수율 | 0.70 ~ 0.99 |
| `stock` | 현재 재고 (ea) | 0 포함, 0 ~ 200 |

반도체 시료 후보 목록:
- 실리콘 웨이퍼-8인치
- GaN 에피택셜-4인치
- SiC 파워기판-6인치
- 포토레지스트-PR7
- 산화막 웨이퍼-SiO2
- 실리콘 웨이퍼-12인치
- InP 기판-3인치
- GaAs 에피택셜-6인치
- 질화규소막-Si3N4
- SOI 웨이퍼-8인치

### 주문 (Order)

| 필드 | 설명 | 형식/범위 |
|---|---|---|
| `orderId` | 주문번호 | `ORD-YYYYMMDD-NNNN` |
| `sampleId` | 시료 ID (등록된 시료만) | `S-001` 등 |
| `customerName` | 고객명 | 실존풍 반도체 기업명 |
| `quantity` | 주문 수량 (ea) | 10 ~ 500 |
| `status` | 주문 상태 | RESERVED / CONFIRMED / PRODUCING / RELEASE / REJECTED |

기본 생성(10건) 시 5가지 상태 각 1건 이상 포함 보장.

---

## 생성 데이터 예시

### samples.json

```json
[
  {"id":"S-001","name":"실리콘 웨이퍼-8인치","avgProductionTime":0.63,"yieldRate":0.87,"stock":142},
  {"id":"S-002","name":"GaN 에피택셜-4인치","avgProductionTime":0.45,"yieldRate":0.93,"stock":0},
  {"id":"S-003","name":"SiC 파워기판-6인치","avgProductionTime":0.81,"yieldRate":0.76,"stock":57},
  {"id":"S-004","name":"포토레지스트-PR7","avgProductionTime":0.29,"yieldRate":0.98,"stock":200},
  {"id":"S-005","name":"산화막 웨이퍼-SiO2","avgProductionTime":0.72,"yieldRate":0.84,"stock":33}
]
```

### orders.json

```json
[
  {"orderId":"ORD-20260612-0001","sampleId":"S-003","customerName":"삼성전자 반도체연구소","quantity":120,"status":"RESERVED"},
  {"orderId":"ORD-20260612-0002","sampleId":"S-001","customerName":"SK하이닉스 공정개발팀","quantity":85,"status":"CONFIRMED"},
  {"orderId":"ORD-20260612-0003","sampleId":"S-005","customerName":"LG이노텍 기판사업부","quantity":300,"status":"PRODUCING"},
  {"orderId":"ORD-20260612-0004","sampleId":"S-002","customerName":"DB하이텍 파운드리사업부","quantity":50,"status":"RELEASE"},
  {"orderId":"ORD-20260612-0005","sampleId":"S-004","customerName":"원익IPS 소재연구팀","quantity":200,"status":"REJECTED"}
]
```

---

## 옵션

`DummyDataService.generate(sampleCount, orderCount, seed)` API를 통해 개수와 시드를 직접 지정할 수 있다.

| 파라미터 | 설명 | 기본값 |
|---|---|---|
| `sampleCount` | 생성할 시료 수 | 5 (기본) / 10 (대량) |
| `orderCount` | 생성할 주문 수 | 10 (기본) / 50 (대량) |
| `seed` | 랜덤 시드 (재현성) | `System.currentTimeMillis()` |

---

## 프로젝트 구조

```
src/
  main/java/com/ssemi/dummy/
    model/
      Sample.java          # 시료 도메인 모델
      Order.java           # 주문 도메인 모델
      OrderStatus.java     # 주문 상태 열거형
    factory/
      SampleFactory.java   # 시료 더미 데이터 팩토리
      OrderFactory.java    # 주문 더미 데이터 팩토리
    repository/
      JsonRepository.java  # JSON 파일 저장소 (저장/조회/초기화)
    DummyDataService.java  # 서비스 계층 (팩토리 + 저장소 조합)
    Main.java              # CLI 진입점
  test/java/com/ssemi/dummy/
    SampleFactoryTest.java # 시료 팩토리 단위 테스트 (8건)
    OrderFactoryTest.java  # 주문 팩토리 단위 테스트 (8건)
    JsonRepositoryTest.java# 저장소 무결성 테스트 (6건)
```

---

## 테스트

```bash
./gradlew test
```

| 테스트 클래스 | 테스트 수 | 검증 내용 |
|---|---|---|
| `SampleFactoryTest` | 8 | ID 형식, 수율 범위, 생산시간 범위, 재고 음수 방지, 재현성 |
| `OrderFactoryTest` | 8 | 주문번호 형식, 등록 시료 ID 제약, 상태 분포, 수량 범위 |
| `JsonRepositoryTest` | 6 | 저장/조회 건수, 필드 무결성, reset, 파일 없을 때 빈 목록 |
