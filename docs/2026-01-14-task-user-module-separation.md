# 작업 목적
- User BC를 core 모듈에서 분리해 user 모듈로 이동하고, core가 user에 의존하지 않도록 구성한다.

# 진행 상태
- [x] user 모듈 추가 및 빌드 설정
- [x] user 도메인/웹/API 구현 이동
- [x] CloudViewerApplication 이동 및 component scan 재설정
- [x] core 아키텍처 규칙에 user 의존 금지 추가
- [x] 단위 테스트 작성 및 성공할 때까지 수정
- [x] archtest(아키텍처 규칙 테스트) 실행
- [x] 작업 기록 문서 작성

# 변경/추가된 주요 컴포넌트 요약
- `modules/user/build.gradle`
- `modules/app/src/main/java/app/CloudViewerApplication.java`
- `modules/user/src/main/java/user/**`
- `modules/user/src/main/java/user/oauth/**`
- `modules/user/src/main/java/user/web/**`
- `modules/user/src/main/java/user/web/dto/**`
- `modules/user/src/test/java/user/**`
- `modules/user/src/test/java/user/web/**`
- `modules/user/src/main/resources/application.yml`
- `modules/user/src/main/resources/application-secret.yml`
- `modules/core/src/test/java/core/ArchitectureRulesTest.java`
- `settings.gradle`

# 적용한 설계 규칙과 스펙과의 대응 관계
- user 모듈을 신설하고 core는 user를 참조하지 않도록 의존 방향을 분리.
- Spring Boot 애플리케이션은 user 모듈로 이동하고 `core`, `user` 패키지를 스캔하도록 구성.

# 검증한 아키텍처 규칙 목록과 결과
- ArchUnit 테스트 실행: 성공
- 실행 명령: `./gradlew :modules:core:test --tests core.ArchitectureRulesTest --no-daemon`

# 남은 TODO 또는 확장 포인트(v2 이상)
- 멀티 모듈 배포 시 app 모듈 분리 여부 검토

# 작업 종료 체크리스트
- 단위 테스트 실행 및 성공할 때까지 수정 반복
- archtest(아키텍처 규칙 테스트) 실행
