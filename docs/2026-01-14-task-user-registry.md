# 작업 목적
- 사용자별 repository registry를 도입하기 위한 API 계약을 확장한다.

# 진행 상태
- Task 1 (modules/api) 완료: userId 기반 DTO 추가, 테스트 통과 확인

# 변경/추가된 주요 컴포넌트
- `modules/api/src/main/java/api/common/UserRepositoryRef.java`
- `modules/api/src/main/java/api/common/RepositoryRegistration.java`
- `modules/api/src/test/java/api/common/UserRepositoryRefTest.java`
- `modules/api/src/test/java/api/common/RepositoryRegistrationTest.java`
- `modules/api/build.gradle` (JUnit 테스트 의존성 추가)

# 설계 규칙 및 스펙 대응
- core는 user를 알지 않고 userId만 다루는 구조로 확장 준비.
- 기존 RepositoryDescriptor는 유지하면서 등록 단계에서만 config를 허용.

# 테스트 결과
- 실행 명령: `./gradlew :modules:api:test --no-daemon`
- 결과: 성공

# 남은 TODO / 확장 포인트
- Task 1 테스트 재실행 및 통과 확인
- Task 2~ 이후 진행 (core, user BC, 규칙 검증)
# 작업 종료 체크리스트
- 단위 테스트 실행 및 성공할 때까지 수정 반복
- archtest(아키텍처 규칙 테스트) 실행

