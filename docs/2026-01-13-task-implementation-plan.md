# 구현 작업 계획 및 진행 상태

## 작업 원칙
- 모듈 순서: api → plugin-runtime → core → core(web)
- 각 모듈 작업 완료 시 상태 갱신
- 구현 코드 작성 후 유닛 테스트 작성
- 각 Task 종료 후 단위 테스트 + 아키텍처 규칙 테스트 실행
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
- [ ] Spring Web 의존성 추가
- [ ] Repository/File/Indexing 조회용 REST API 설계
- [ ] Controller 구현 및 DTO 매핑
- [ ] 유닛 테스트 작성
- [ ] 변경 사항 반영 후 docs 기록

## Task 5: 아키텍처 규칙 검증
- [ ] 아키텍처 규칙 테스트 실행
- [ ] 결과 기록 및 문서화

## Task 6: Indexer 로컬 파일 트리 구현
- [x] 인덱스 구조(IndexNode/IndexSnapshot) 정의
- [x] 파일 트리 수집(FileTreeIndexer) 구현
- [x] IndexingService에서 스케줄/보류/완료 흐름 연결
- [x] 유닛 테스트 작성
- [x] 변경 사항 반영 후 docs 기록
