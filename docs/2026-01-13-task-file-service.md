# FileService 구현 작업 기록

## 1) 작업 목적
FileService가 매 호출마다 Repository 전제 확인을 수행하고 파일 접근을 위임하도록 스펙에 맞게 구현한다.

## 2) 변경/추가된 주요 컴포넌트 요약
- `core.file.FileService`: 매 작업 전 Repository 전제 확인 후 FileHandle 위임

## 3) 적용한 설계 규칙과 스펙과의 대응 관계
- 모든 메서드에서 `RepositoryService.openRepository`로 전제 확인을 수행
- Repository 불능 시 즉시 실패하는 흐름 유지
- v1 read-only 기준으로 추가 불변 검증은 확장 포인트 주석만 유지

## 4) 검증한 아키텍처 규칙 목록과 결과
- 모듈 의존 방향(core ↔ api ↔ plugin-runtime ↔ plugins): 통과
- Bounded Context 간 직접 참조 금지: 통과
- File ↔ Indexing 완전 단절: 통과

## 5) 남은 TODO 또는 확장 포인트(v2 이상)
- 파일 불변 검증 규칙 구체화
- 에러 모델 세분화
# 작업 종료 체크리스트
- 단위 테스트 실행 및 성공할 때까지 수정 반복
- archtest(아키텍처 규칙 테스트) 실행

