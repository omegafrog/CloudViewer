# core(web) REST API 구현 작업 기록

## 1) 작업 목적
core 모듈에 REST API를 추가하여 Repository/File/Indexing 조회 기능을 제공한다.

## 2) 변경/추가된 주요 컴포넌트 요약
- `core.web.RepositoryController`: 가용성 조회 및 open 메타 반환
- `core.web.FileController`: 파일 조회/목록/다운로드
- `core.web.IndexingController`: 스케줄/보류/상태/스냅샷 조회
- `core.web.dto.*`: 요청 DTO 및 변환 로직
- Spring Web 의존성 및 테스트 추가

## 3) 적용한 설계 규칙과 스펙과의 대응 관계
- core 서비스만 사용하고 plugin 구현체 직접 참조 없음
- File ↔ Indexing 분리 유지(컨트롤러도 서비스 경계 유지)
- read-only 범위에서 조회/다운로드만 제공

## 4) 검증한 아키텍처 규칙 목록과 결과
- 모듈 의존 방향(core ↔ api ↔ plugin-runtime ↔ plugins): 통과
- Bounded Context 간 직접 참조 금지: 통과
- File ↔ Indexing 완전 단절: 통과

## 5) 남은 TODO 또는 확장 포인트(v2 이상)
- 에러 응답 표준화
- 인덱스 스냅샷 조회 필터링/페이징
