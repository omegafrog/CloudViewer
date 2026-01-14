# 작업 목적
- 사용자 등록을 위한 최소한의 user registry를 core에 추가한다.

# 변경/추가된 주요 컴포넌트 요약
- `modules/api/src/main/java/api/common/UserRegistration.java`
- `modules/api/src/test/java/api/common/UserRegistrationTest.java`
- `modules/core/src/main/java/core/user/UserCatalog.java`
- `modules/core/src/main/java/core/web/UserController.java`
- `modules/core/src/main/java/core/web/dto/UserRequest.java`
- `modules/core/src/main/java/core/web/dto/UserRegistrationResponse.java`
- `modules/core/src/test/java/core/user/UserCatalogTest.java`
- `modules/core/src/test/java/core/web/UserControllerTest.java`

# 적용한 설계 규칙과 스펙과의 대응 관계
- core는 userId만 보유하고 내부 registry로 관리하며 외부 의존 없이 in-memory로 처리.
- user 등록/조회는 core/web의 REST API로 제공하고, 계약 타입은 api 모듈에 둔다.

# 검증한 아키텍처 규칙 목록과 결과
- ArchUnit 테스트 실행: 성공
- 실행 명령: `./gradlew :modules:core:test --tests core.ArchitectureRulesTest --no-daemon`

# 남은 TODO 또는 확장 포인트(v2 이상)
- user BC에 대한 도메인 규칙 확정 및 저장소/정책 확장
# 작업 종료 체크리스트
- 단위 테스트 실행 및 성공할 때까지 수정 반복
- archtest(아키텍처 규칙 테스트) 실행

