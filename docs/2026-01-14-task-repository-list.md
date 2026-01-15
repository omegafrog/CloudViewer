# 저장소 목록 조회 API 추가

## 작업 목적
- 사용자 기준으로 등록된 저장소 목록을 조회할 수 있는 API를 제공한다.

## 변경/추가된 주요 컴포넌트 요약
- `modules/core/src/main/java/core/repository/RepositoryCatalog.java`
  - `listByUserId` 추가로 사용자별 등록 목록 제공.
- `modules/core/src/main/java/core/web/RepositoryController.java`
  - `GET /repositories?userId=...` 추가.
- `modules/core/src/main/java/core/web/dto/RepositorySummaryResponse.java`
  - 목록 조회 응답 DTO 추가.
- `modules/core/src/test/java/core/web/RepositoryControllerTest.java`
  - 목록 조회 API 테스트 추가.

## 적용한 설계 규칙과 스펙과의 대응 관계
- core 모듈이 api 모델(`RepositoryRegistration`)을 사용하여 등록 정보를 반환하도록 유지함.
- 사용자별 조회는 Repository BC 내부 책임으로 제한해 외부 BC와 직접 결합하지 않음.

## 검증한 아키텍처 규칙 목록과 결과
- core는 plugins 구현체에 의존하지 않음: 통과
- core는 user 모듈에 의존하지 않음: 통과
- indexing은 file/repository에 의존하지 않음: 통과

## 남은 TODO 또는 확장 포인트(v2 이상)
- 정렬/페이지네이션/필터링 기준 추가
- 등록 시점 및 상태 메타데이터 확장
