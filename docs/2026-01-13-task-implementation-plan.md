# 구현 작업 계획 및 진행 상태

## 작업 원칙
- 모듈 순서: api → plugin-runtime → core → core(web)
- 각 모듈 작업 완료 시 상태 갱신
- 구현 코드 작성 후 유닛 테스트 작성
- 각 Task 종료 후 단위 테스트 + archtest(아키텍처 규칙 테스트) 실행
- 작업 기록 문서는 별도 생성

## Task 1: api 모듈 보강
- [x] DTO/인터페이스 스펙 재확인 및 누락 여부 검토
- [x] null 정책/불변 정책 일관성 점검 및 정리
- [x] 변경 사항 반영 후 docs 기록

## Task 2: plugin-runtime 모듈 보강
- [x] PluginLoader 로딩 안정성 점검(다중 구현/없음 처리)
- [x] PluginRegistry 중복 타입 처리 정책 재확인
- [x] 유닛 테스트 작성
- [x] 변경 사항 반영 후 docs 기록

## Task 3: core 모듈 실구현
- [x] RepositoryService 전제 위반/가용성 처리 재확인
- [x] FileService read-only 처리 및 확장 포인트 정리
- [x] IndexingService 실제 인덱싱 스케줄/상태 관리 구현
- [x] 유닛 테스트 작성
- [x] 변경 사항 반영 후 docs 기록

## Task 4: core(web) REST API 추가
- [x] Spring Web 의존성 추가
- [x] Repository/File/Indexing 조회용 REST API 설계
- [x] Controller 구현 및 DTO 매핑
- [x] 유닛 테스트 작성
- [x] 변경 사항 반영 후 docs 기록

## Task 5: 아키텍처 규칙 검증
- [x] 아키텍처 규칙 테스트 실행
- [x] 결과 기록 및 문서화

## Task 6: Indexer 로컬 파일 트리 구현
- [x] 인덱스 구조(IndexNode/IndexSnapshot) 정의
- [x] 파일 트리 수집(FileTreeIndexer) 구현
- [x] IndexingService에서 스케줄/보류/완료 흐름 연결
- [x] 유닛 테스트 작성
- [x] 변경 사항 반영 후 docs 기록

## Task 7: OneDrive 플러그인 구현
- [x] 플러그인 프로젝트 스캐폴딩(독립 빌드)
- [x] RepositoryConnector/Handle 구현
- [x] FileHandle 원격 OneDrive 조회 구현
- [x] ServiceLoader 등록
- [x] 유닛 테스트 작성
- [x] 변경 사항 반영 후 docs 기록

## Task 8: OneDrive Device Code Flow 추가
- [x] 디바이스 코드 인증 클라이언트 추가
- [x] 토큰 공급자 및 캐시 구현
- [x] 플러그인 연계 및 테스트 보강
- [x] 변경 사항 반영 후 docs 기록

## Task 9: 플러그인 자동 로딩
- [x] 플러그인 디렉토리 스캔 로직 추가
- [x] 스프링 부트 시작 시 로딩 연계
- [x] 유닛 테스트 작성
- [x] 변경 사항 반영 후 docs 기록

## Task 10: 플러그인 로딩 경로 보정
- [x] 실행 위치 기반 경로 탐색 로직 추가
- [x] 변경 사항 반영 후 docs 기록

## Task 11: repository file 명령 API
- [x] file 생성/삭제/이동 API 설계 및 구현
- [x] FileFacade 도입 및 소유권 검증 흐름 확정
- [x] 유닛 테스트 작성
- [x] archtest 실행
- [x] 변경 사항 반영 후 docs 기록

## Task 12: 원격 인덱싱 인터페이스
- [x] api 인덱싱 인터페이스 추가
- [x] IndexingService 원격 인덱싱 지원
- [x] 유닛 테스트 작성
- [x] archtest 실행
- [x] 변경 사항 반영 후 docs 기록

## Task 13: 인덱싱 핸들 이름 정리
- [x] 플러그인 전용 인덱싱 인터페이스 명확화
- [x] 유닛 테스트 작성
- [x] archtest 실행
- [x] 변경 사항 반영 후 docs 기록

## Task 14: OneDrive 플러그인 인덱싱
- [x] OneDriveFileHandle 인덱싱 구현
- [x] 유닛 테스트 작성
- [x] archtest 실행
- [x] 변경 사항 반영 후 docs 기록
# 작업 종료 체크리스트
- 단위 테스트 실행 및 성공할 때까지 수정 반복
- archtest(아키텍처 규칙 테스트) 실행

