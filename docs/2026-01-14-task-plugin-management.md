# 작업 목적
- 플러그인 목록 조회 및 추가/제거/활성화/비활성화 API를 제공하고, jar 업로드 기반 플러그인 관리 기능을 구현한다.

# 진행 상태
- [x] 플러그인 목록 조회 API 설계/구현
- [x] 플러그인 추가(업로드) API 설계/구현
- [x] 플러그인 제거 API 설계/구현
- [x] 플러그인 활성화/비활성화 API 설계/구현
- [x] 플러그인 저장/상태 관리 컴포넌트 구현
- [x] 플러그인 로더/레지스트리 연동
- [x] 단위 테스트 작성 및 성공할 때까지 수정
- [x] archtest(아키텍처 규칙 테스트) 실행
- [x] clean build 실행 및 성공 확인
- [x] 작업 기록 문서 작성

# 변경/추가 예정 주요 컴포넌트
- `modules/core/src/main/java/core/plugin/**`
- `modules/core/src/main/java/core/web/PluginController.java`
- `modules/core/src/main/java/core/web/dto/**`
- `modules/core/src/test/java/core/plugin/**`
- `modules/core/src/test/java/core/web/PluginControllerTest.java`
- `docs/2026-01-14-task-plugin-management-implementation.md`

# 설계 규칙 및 스펙 대응
- 플러그인은 jar 파일로 업로드되며 로컬 플러그인 저장소에 저장된다.
- 활성화/비활성화 상태는 플러그인 레지스트리에서 분리 관리한다.
- core는 plugin 구현체에 직접 의존하지 않고 plugin-runtime 및 api 계약만 사용한다.

# API 초안
- `GET /plugins` : 로드된 플러그인 목록 조회
- `POST /plugins` : 플러그인 jar 업로드 (multipart)
- `DELETE /plugins/{pluginId}` : 플러그인 제거
- `POST /plugins/{pluginId}/enable` : 활성화
- `POST /plugins/{pluginId}/disable` : 비활성화

# 테스트 및 검증 계획
- 플러그인 목록/업로드/활성화/비활성화/제거에 대한 단위 테스트
- archtest로 모듈 의존 방향 및 core↔plugin 경계 확인
- clean build로 변경 모듈 빌드 확인

# 남은 TODO / 확장 포인트(v2 이상)
- 플러그인 서명/검증
- 플러그인 버전 충돌 정책

# 작업 종료 체크리스트
- 단위 테스트 실행 및 성공할 때까지 수정 반복
- archtest(아키텍처 규칙 테스트) 실행
- clean build 실행
