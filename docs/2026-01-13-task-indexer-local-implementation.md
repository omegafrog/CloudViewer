# Indexer 로컬 파일 트리 구현 작업 기록

## 1) 작업 목적
로컬 파일 트리를 인덱싱하여 노드 구조를 생성하고, IndexingService에서 수집 실행을 수행하도록 한다.

## 2) 변경/추가된 주요 컴포넌트 요약
- `core.indexing.IndexNode`: 인덱스 노드 구조(record)
- `core.indexing.IndexSnapshot`: 인덱싱 결과 스냅샷(record)
- `core.indexing.FileTreeIndexer`: 로컬 파일 트리 수집 구현
- `core.indexing.IndexingService`: 스케줄/보류/완료 및 스냅샷 저장
- `core.indexing.IndexingServiceTest`: 로컬 파일 트리 수집 검증

## 3) 적용한 설계 규칙과 스펙과의 대응 관계
- Indexing은 Repository/File에 의존하지 않고 RepositoryDescriptor만 사용
- 수집 경로는 RepositoryDescriptor.config의 `rootPath`에서 해석
- 상태는 SCHEDULED → INDEXED 또는 DEFERRED 흐름으로 관리

## 4) 검증한 아키텍처 규칙 목록과 결과
- 모듈 의존 방향(core ↔ api ↔ plugin-runtime ↔ plugins): 통과
- Bounded Context 간 직접 참조 금지: 통과
- File ↔ Indexing 완전 단절: 통과

## 5) 남은 TODO 또는 확장 포인트(v2 이상)
- 인덱스 갱신 주기/정책 정의
- 스냅샷 저장소 교체(영속화)
# 작업 종료 체크리스트
- 단위 테스트 실행 및 성공할 때까지 수정 반복
- archtest(아키텍처 규칙 테스트) 실행

