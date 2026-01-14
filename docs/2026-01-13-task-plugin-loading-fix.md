# 플러그인 로딩 경로 보정 작업 기록

## 1) 작업 목적
실행 위치에 따라 modules/plugins를 찾지 못하는 문제를 해결한다.

## 2) 변경/추가된 주요 컴포넌트 요약
- `core.config.PluginBootstrapper`: 플러그인 디렉토리 경로 보정 및 탐색 로직 추가

## 3) 적용한 설계 규칙과 스펙과의 대응 관계
- core는 plugin-runtime을 통해서만 플러그인 접근
- 플러그인 로딩 실패 시 핵심 비즈니스 로직 변경 없음

## 4) 검증한 아키텍처 규칙 목록과 결과
- 모듈 의존 방향(core ↔ api ↔ plugin-runtime ↔ plugins): 통과
- Bounded Context 간 직접 참조 금지: 통과
- File ↔ Indexing 완전 단절: 통과

## 5) 남은 TODO 또는 확장 포인트(v2 이상)
- 플러그인 로딩 결과를 관측 가능한 상태로 노출
