# 작업 목적
- OneDrive 디바이스 코드 인증 기본값을 개인 계정(consumer) 기준으로 동작하도록 조정한다.
- 실패 메시지에 원인 정보를 포함해 설정 오류를 빠르게 확인한다.

# 변경/추가된 주요 컴포넌트
- `plugins/onedrive-plugin/src/main/java/plugins/onedrive/OneDriveTokenProvider.java`
  - 기본 clientId/tenant/scope 값을 개인 계정 환경에 맞게 변경.
- `plugins/onedrive-plugin/src/main/java/plugins/onedrive/OneDriveDeviceCodeAuthClient.java`
  - 디바이스 코드 요청 실패 시 응답 본문을 포함한 메시지 반환.

# 설계 규칙 및 스펙 대응
- 플러그인 구현은 api 모듈에만 의존하도록 유지.
- 읽기 전용 v1 정책을 유지하며 인증 흐름만 보완.

# 검증한 아키텍처 규칙 및 결과
- 모듈 의존 방향, BC 간 직접 참조 금지, File ↔ Indexing 단절 규칙은 기존 ArchUnit 테스트로 확인(변경 없음).

# 남은 TODO / 확장 포인트
- 디바이스 코드 흐름 실패 응답의 에러 코드/설명 파싱을 세분화.
