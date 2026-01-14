# OneDrive Device Code Flow 구현 작업 기록

## 1) 작업 목적
앱 등록 없이 디바이스 코드 플로우로 OneDrive 접근 토큰을 획득하도록 플러그인을 확장한다.

## 2) 변경/추가된 주요 컴포넌트 요약
- `OneDriveAuthClient`/`OneDriveDeviceCodeAuthClient`: 디바이스 코드/토큰 요청
- `OneDriveTokenProvider`: 토큰 해석 및 인메모리 캐시
- `OneDriveConfig`: clientId/tenant/scope/useDeviceCode 추가
- `OneDrivePlugin`: 토큰 공급자 연계

## 3) 적용한 설계 규칙과 스펙과의 대응 관계
- plugin은 api 인터페이스만 의존하며 core 직접 참조 없음
- read-only 조회 범위 유지
- RepositoryDescriptor.config로 인증/경로를 전달

## 4) 검증한 아키텍처 규칙 목록과 결과
- 모듈 의존 방향(core ↔ api ↔ plugin-runtime ↔ plugins): 통과
- Bounded Context 간 직접 참조 금지: 통과
- File ↔ Indexing 완전 단절: 통과

## 5) 남은 TODO 또는 확장 포인트(v2 이상)
- 토큰 리프레시 처리
- 사용자 안내 메시지 표준화
