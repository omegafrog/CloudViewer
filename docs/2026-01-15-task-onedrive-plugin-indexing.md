# 작업 목적
- OneDrive 플러그인이 PluginIndexingHandle을 구현해 원격 인덱싱을 지원한다.

# 진행 상태
- [x] OneDriveFileHandle에 PluginIndexingHandle 구현
- [x] 인덱싱 트리 생성 로직 추가
- [x] 단위 테스트 작성 및 통과 확인
- [x] archtest 실행 및 결과 기록

# 변경/추가된 주요 컴포넌트
- `plugins/onedrive-plugin/src/main/java/plugins/onedrive/OneDriveFileHandle.java`
- `plugins/onedrive-plugin/src/test/java/plugins/onedrive/OneDrivePluginTest.java`

# 설계 규칙 및 스펙 대응
- 플러그인 구현체는 api의 PluginIndexingHandle을 통해 인덱싱을 제공한다.
- core.indexing은 플러그인 구현체에 직접 의존하지 않는다.

# 테스트 결과
- 실행 명령: `./gradlew :plugins:onedrive-plugin:test --no-daemon`
- 결과: 성공

# 아키텍처 규칙 검증 결과
- 실행 명령: `./gradlew --no-daemon architectureRules`
- 결과: 성공

# 남은 TODO 또는 확장 포인트(v2 이상)
- OneDrive 페이지네이션 기반 인덱싱 깊이 제어
