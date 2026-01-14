# RepositoryService 구현 작업 기록

## 1) 작업 목적
RepositoryService의 플러그인 가용성 검증 및 저장소 연결 로직을 스펙에 맞게 확정한다.

## 2) 변경/추가된 주요 컴포넌트
- `core.repository.RepositoryService`: 플러그인 존재 확인, 가용성 검증, 연결 생성 흐름 확정

## 3) 적용한 설계 규칙과 스펙 대응
- `openRepository`: 매 호출마다 `PluginAvailability`를 확인하고 전제 위반 시 즉시 실패
- `checkAvailability`: 플러그인 판정 결과만 감싼 값을 반환
- Plugin 의존은 `PluginRegistry` 경유로만 수행하여 BC 경계 유지

## 4) 검증한 아키텍처 규칙 목록과 결과
- 모듈 의존 방향(core ↔ api ↔ plugin-runtime ↔ plugins): 통과
- Bounded Context 간 직접 참조 금지: 통과
- File ↔ Indexing 완전 단절: 통과

## 5) 남은 TODO / 확장 포인트(v2+)
- 전제 위반 예외 타입 분리 정책
- 가용성 사유 코드 표준화
# 작업 종료 체크리스트
- 단위 테스트 실행 및 성공할 때까지 수정 반복
- archtest(아키텍처 규칙 테스트) 실행

