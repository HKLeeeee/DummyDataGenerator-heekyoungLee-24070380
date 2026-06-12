---
name: poc-dummy-data-generator
description: PoC 4 — Dummy 데이터 생성 Tool Repository 작성 가이드. 테스트용 Dummy Data를 생성해 연결된 DB(저장소)에 추가하는 도구를 구현할 때 반드시 사용. "더미 데이터", "테스트 데이터", "시딩", "dummy" 가 언급되면 이 skill을 참조할 것.
---

# PoC 4: Dummy 데이터 생성 Tool

## 목적 (과제 명세)

Test를 위한 Dummy Data를 생성하는 도구. **생성된 Dummy Data는 연결된 DB(저장소)에 추가**.

## 완료 기준 (Definition of Done)

- [ ] PoC 2와 동일한 저장 방식/스키마로 데이터 실제 기록
- [ ] Sample + Order 두 종류 생성
- [ ] 도메인 제약 준수 (존재하는 시료 ID만 주문에 사용, 수율 0~1, 수량 > 0)
- [ ] 옵션: 생성 개수, 랜덤 시드(재현성), --reset(초기화 후 생성)
- [ ] 생성 후 재로드하여 건수/무결성 검증하는 테스트
- [ ] README.md (사용법, 옵션, 생성 예시)

## 현실적인 데이터 풀 (명세 예시 기반)

- 시료명: 실리콘 웨이퍼-8인치, GaN 에피택셜-4인치, SiC 파워기판-6인치,
  포토레지스트-PR7, 산화막 웨이퍼-SiO2 등 + 변형 조합
- 속성 범위: 평균 생산시간 0.2~1.0 min/ea, 수율 0.70~0.99, 재고 0~1000 (0 포함 → '고갈' 테스트용)
- 고객명: 연구소/팹리스/대학 연구실 풍 (예: ○○전자 파운드리, ○○이노텍, ○○대 반도체연구실)
- 주문 상태 분포 지정 가능: RESERVED / CONFIRMED / PRODUCING / RELEASE / REJECTED
  (모니터링·승인·출고 화면 테스트가 모두 가능하도록 골고루)
- 주문번호: `ORD-YYYYMMDD-NNNN` 순번 증가, 중복 금지

## 사용 예 (CLI)

```
dummy-gen --samples 12 --orders 36 --seed 42
dummy-gen --reset --samples 5 --orders 10
```

## 구현 메모

- 시료를 먼저 생성·저장한 뒤, 그 ID 풀에서만 주문을 생성 (참조 무결성)
- 시드 고정 시 항상 동일 결과 → 테스트 재현성 확보
- 본 프로젝트 Phase 5 시나리오 검증에 이 도구를 그대로 사용
