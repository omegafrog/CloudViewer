# core 모듈 구현 작업 기록

## 1) 작업 목적
core 모듈의 Repository/File/Indexing 동작을 확인하고 유닛 테스트로 동작을 고정한다.

## 2) 변경/추가된 주요 컴포넌트 요약
- `core.repository.RepositoryServiceTest`: 전제 위반/가용성/정상 연결 검증
- `core.file.FileServiceTest`: 매 호출 전제 확인 및 위임 검증
- `core.indexing.IndexingServiceTest`: 스케줄/보류 상태 검증
- 테스트용 핸들/플러그인 스텁 추가

## 3) 적용한 설계 규칙과 스펙과의 대응 관계
- Repository는 매 호출마다 PluginAvailability를 확인
- File은 Repository 전제 위반 시 즉시 실패하고 FileHandle 위임만 수행
- Indexing은 Repository/File에 의존하지 않고 상태만 관리

## 4) 검증한 아키텍처 규칙 목록과 결과
- 모듈 의존 방향(core ↔ api ↔ plugin-runtime ↔ plugins): 통과
- Bounded Context 간 직접 참조 금지: 통과
- File ↔ Indexing 완전 단절: 통과

## 5) 남은 TODO 또는 확장 포인트(v2 이상)
- 전제 위반 에러 모델 세분화
- 인덱싱 상태 전이 규칙 확장
# 작업 종료 체크리스트
- 단위 테스트 실행 및 성공할 때까지 수정 반복
- archtest(아키텍처 규칙 테스트) 실행

