# 작업 목적
- 플러그인 API에 인덱싱 인터페이스를 추가한다.
- IndexingService가 플러그인 인덱싱 명세를 우선 사용하도록 확장한다.
- 로컬/원격 인덱싱을 동일한 스냅샷 모델로 통합한다.

# 진행 상태
- [x] api.common.IndexNode 도입
- [x] api.repository.PluginIndexingHandle 추가
- [x] IndexingService 원격 인덱싱 지원 및 실패 정책 반영
- [x] 테스트 갱신 및 통과 확인
- [x] archtest 실행 및 결과 기록

# 변경/추가된 주요 컴포넌트
- `modules/api/src/main/java/api/common/IndexNode.java`
- `modules/api/src/main/java/api/repository/PluginIndexingHandle.java`
- `modules/core/src/main/java/core/indexing/IndexingService.java`
- `modules/core/src/main/java/core/indexing/FileTreeIndexer.java`
- `modules/core/src/main/java/core/indexing/IndexSnapshot.java`
- `modules/core/src/main/java/core/web/IndexingController.java`
- `modules/core/src/test/java/core/indexing/IndexingServiceTest.java`
- `modules/core/src/test/java/core/web/IndexingControllerTest.java`

# 설계 규칙 및 스펙 대응
- core.indexing은 core.file/core.repository에 의존하지 않는다.
- 인덱싱은 플러그인 인덱싱 인터페이스를 우선 사용한다.
- Repository는 매 요청마다 PluginAvailability 확인(openRepository 호출).
- Indexing ↔ File 완전 분리 유지.

# 인덱싱 정책
- 원격 인덱싱 기본 depth는 2로 제한한다.
- 인덱싱 실패 시 상태는 DEFERRED로 설정하고 마지막 스냅샷은 유지한다.
- 로컬 인덱싱은 rootPath 기반 FileTreeIndexer를 사용한다.

# 테스트 결과
- 실행 명령: `./gradlew :modules:core:test --no-daemon`
- 결과: 성공

# 아키텍처 규칙 검증 결과
- 실행 명령: `./gradlew --no-daemon architectureRules`
- 결과: 성공

# 남은 TODO 또는 확장 포인트(v2 이상)
- 원격 인덱싱 depth/부분 로딩 정책을 API로 노출
- 스냅샷 영속화 구현
