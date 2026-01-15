# 작업 목적
- 플러그인 목록 조회 및 추가/제거/활성화/비활성화 API를 제공하고, jar 업로드 기반 플러그인 관리 기능을 구현한다.

# 변경/추가된 주요 컴포넌트 요약
- `modules/core/src/main/java/core/plugin/PluginManager.java`
- `modules/core/src/main/java/core/plugin/PluginRecord.java`
- `modules/core/src/main/java/core/plugin/PluginStatus.java`
- `modules/core/src/main/java/core/web/PluginController.java`
- `modules/core/src/main/java/core/web/dto/PluginSummaryResponse.java`
- `modules/core/src/main/java/core/config/PluginRuntimeConfig.java`
- `modules/core/src/main/java/core/repository/RepositoryService.java`
- `modules/core/src/test/java/core/plugin/PluginManagerTest.java`
- `modules/core/src/test/java/core/plugin/StubMultipartFile.java`
- `modules/core/src/test/java/core/web/PluginControllerTest.java`
- `modules/core/src/test/java/core/repository/RepositoryServiceTest.java`
- `modules/core/src/test/java/core/file/FileServiceTest.java`

# 적용한 설계 규칙과 스펙과의 대응 관계
- 플러그인은 jar로 업로드하고 로컬 플러그인 디렉토리에 저장한다.
- 활성화/비활성화 상태는 레지스트리와 분리한 관리 상태로 처리한다.
- core는 plugin 구현체에 직접 의존하지 않고 plugin-runtime 및 api 계약만 사용한다.

# 검증한 아키텍처 규칙 목록과 결과
- ArchUnit 테스트 실행: 성공
- 실행 명령: `./gradlew :modules:core:test --tests core.ArchitectureRulesTest --no-daemon`

# 남은 TODO 또는 확장 포인트(v2 이상)
- 플러그인 서명/검증
- 플러그인 버전 충돌 정책
- repository 등록 해제 API와 플러그인 상태 연계 정책 보강

# 작업 종료 체크리스트
- 단위 테스트 실행 및 성공할 때까지 수정 반복
- archtest(아키텍처 규칙 테스트) 실행
- clean build 실행
