# 작업 목적
- repository file 조회/생성/삭제/이동 API를 core 모듈에 추가한다.
- userId + repositoryId 기반 소유권 검증 흐름을 명확히 한다.
- 로컬/원격 차이를 숨기는 file facade를 도입한다.
- 인덱서 역할을 명확히 정의한다.

# 진행 상태
- [x] API 스펙 및 요청/응답 DTO 정의
- [x] FileFacade 및 FileService 확장
- [x] FileController 엔드포인트 추가 및 Swagger 설명 보강
- [x] 단위 테스트 작성 및 통과 확인
- [x] archtest 실행 및 결과 기록

# 변경/추가된 주요 컴포넌트
- `modules/api/src/main/java/api/repository/FileCommandHandle.java`
- `modules/core/src/main/java/core/file/FileFacade.java`
- `modules/core/src/main/java/core/file/FileService.java`
- `modules/core/src/main/java/core/web/FileController.java`
- `modules/core/src/main/java/core/web/dto/FileCreateByIdRequest.java`
- `modules/core/src/main/java/core/web/dto/FileDeleteByIdRequest.java`
- `modules/core/src/main/java/core/web/dto/FileDeleteResponse.java`
- `modules/core/src/main/java/core/web/dto/FileMoveByIdRequest.java`
- `modules/core/src/main/java/core/indexing/IndexingService.java`
- `modules/core/src/test/java/core/file/FileServiceTest.java`
- `modules/core/src/test/java/core/web/FileControllerTest.java`
- `modules/core/src/test/java/testsupport/TestFileHandle.java`

# 설계 규칙 및 스펙 대응
- 모듈 의존 방향 유지: core -> api만 사용.
- plugin은 FileCommandHandle로 write 기능을 선택적으로 제공.
- repository는 매 요청마다 PluginAvailability 확인(openRepository 호출).
- 파일 명령은 Indexing과 완전 분리.
- userId + repositoryId로 repository 소유권을 확인.

# 인덱서 역할 정의
- Indexer는 repository의 read-only 스냅샷을 생성한다.
- Indexer는 파일 생성/삭제/이동을 수행하지 않는다.
- 파일 API는 Indexing을 트리거하거나 상태를 공유하지 않는다.

# API 스펙 (요약)
- `POST /files/get-by-id`
- `POST /files/list-by-id`
- `POST /files/download-by-id`
- `POST /files/create-by-id`
- `POST /files/delete-by-id`
- `POST /files/move-by-id`

# 테스트 결과
- 실행 명령: `./gradlew :modules:core:test --no-daemon`
- 결과: 성공

# 아키텍처 규칙 검증 결과
- 실행 명령: `./gradlew :modules:core:test --tests core.ArchitectureRulesTest --no-daemon`
- 결과: 성공

# 남은 TODO 또는 확장 포인트(v2 이상)
- 에러 모델/에러 코드 표준화
- 파일 생성 시 컨텐츠 업로드 정책 확정
