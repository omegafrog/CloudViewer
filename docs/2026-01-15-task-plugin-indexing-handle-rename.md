# 작업 목적
- 플러그인 전용 인덱싱 핸들러임을 명확히 드러내는 이름으로 변경한다.

# 진행 상태
- [x] api.repository.PluginIndexingHandle로 이름 변경
- [x] IndexingService 및 테스트 갱신
- [x] 문서 갱신
- [x] 단위 테스트 작성 및 통과 확인
- [x] archtest 실행 및 결과 기록

# 변경/추가된 주요 컴포넌트
- `modules/api/src/main/java/api/repository/PluginIndexingHandle.java`
- `modules/core/src/main/java/core/indexing/IndexingService.java`
- `modules/core/src/test/java/core/indexing/IndexingServiceTest.java`
- `docs/2026-01-15-task-remote-indexing.md`

# 설계 규칙 및 스펙 대응
- 플러그인 구현 전용 인덱싱 핸들러임을 명확히 해서 오해를 방지한다.
- core.indexing은 core.file/core.repository에 의존하지 않는다.

# 테스트 결과
- 실행 명령: `./gradlew :modules:api:test :modules:core:test --no-daemon`
- 결과: 성공

# 아키텍처 규칙 검증 결과
- 실행 명령: `./gradlew --no-daemon architectureRules`
- 결과: 성공
