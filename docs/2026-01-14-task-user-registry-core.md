# 작업 목적
- core 계층에서 사용자별 repository registry를 지원하도록 API 계약을 반영한다.

# 변경/추가된 주요 컴포넌트 요약
- `modules/core/src/main/java/core/repository/RepositoryCatalog.java`
- `modules/core/src/main/java/core/file/FileService.java`
- `modules/core/src/main/java/core/web/RepositoryController.java`
- `modules/core/src/main/java/core/web/FileController.java`
- `modules/core/src/main/java/core/web/dto/RepositoryRequest.java`
- `modules/core/src/main/java/core/web/dto/RepositoryIdRequest.java`
- `modules/core/src/main/java/core/web/dto/RepositoryRegistrationResponse.java`
- `modules/core/src/main/java/core/web/dto/FileByIdRequest.java`
- `modules/core/src/main/java/core/web/dto/FileListByIdRequest.java`
- `modules/core/src/test/java/core/repository/RepositoryCatalogTest.java`
- `modules/core/src/test/java/core/web/RepositoryControllerTest.java`
- `modules/core/src/test/java/core/web/FileControllerTest.java`
- `modules/core/src/test/java/core/web/IndexingControllerTest.java`

# 적용한 설계 규칙과 스펙과의 대응 관계
- repository registry는 사용자 단위(`userId`)로 구분되도록 core에서 분리해 관리하도록 반영.
- api에 정의된 `UserRepositoryRef`, `RepositoryRegistration` 계약을 core/web DTO에서 직접 사용해 일관성을 유지.
- file/indexing 경계는 기존 방식(RepositoryDescriptor 기반) 그대로 유지하여 BC 간 전제 확산을 최소화.

# 검증한 아키텍처 규칙 목록과 결과
- ArchUnit 테스트 실행: 성공
- 실행 명령: `./gradlew :modules:core:test --tests core.ArchitectureRulesTest --no-daemon`

# 남은 TODO 또는 확장 포인트(v2 이상)
- Task 3 이후(core/user BC 세부 기능, 규칙 검증) 진행
