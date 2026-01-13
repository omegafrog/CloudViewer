# IndexingService 구현 작업 기록

## 1) 작업 목적
IndexingService를 스펙에 맞게 구현하여 RepositoryDescriptor 기반 스케줄링 상태를 관리한다.

## 2) 변경/추가된 주요 컴포넌트 요약
- `core.indexing.IndexStatus`: DEFERRED 상태 추가
- `core.indexing.IndexingService`: 스케줄링/보류 상태 관리

## 3) 적용한 설계 규칙과 스펙과의 대응 관계
- File/Repository 서비스에 대한 의존/호출 없이 RepositoryDescriptor만 다룸
- 외부에서 스케줄링 여부를 결정하도록 하여 indexing이 원인 판정에 관여하지 않음
- 보류 상태를 DEFERRED로 표현하여 repository 불능 시 처리 방향을 유지

## 4) 검증한 아키텍처 규칙 목록과 결과
- 모듈 의존 방향(core ↔ api ↔ plugin-runtime ↔ plugins): 통과
- Bounded Context 간 직접 참조 금지: 통과
- File ↔ Indexing 완전 단절: 통과

## 5) 남은 TODO 또는 확장 포인트(v2 이상)
- 스케줄링 상태의 만료/정리 정책
- 상태 전이 규칙 정의
