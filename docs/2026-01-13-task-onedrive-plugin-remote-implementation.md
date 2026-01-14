# OneDrive 플러그인 원격 조회 구현 작업 기록

## 1) 작업 목적
OneDrive 플러그인이 실제 클라우드 저장소를 조회하도록 HTTP 기반 클라이언트를 구현한다.

## 2) 변경/추가된 주요 컴포넌트 요약
- `OneDriveApiClient`/`OneDriveApiClientFactory`: 클라이언트 추상화
- `OneDriveHttpApiClient`: Microsoft Graph API 호출 구현
- `OneDriveConfig`: accessToken/driveId/rootPath 구성
- `OneDriveFileHandle`: 원격 get/list/download 위임
- 플러그인 테스트를 원격 의존 없이 동작하도록 스텁화

## 3) 적용한 설계 규칙과 스펙과의 대응 관계
- plugin은 api 인터페이스만 의존하며 core 직접 참조 없음
- RepositoryDescriptor.config에 인증/경로 정보를 저장하여 read-only 조회 수행
- File ↔ Indexing 단절 규칙 유지

## 4) 검증한 아키텍처 규칙 목록과 결과
- 모듈 의존 방향(core ↔ api ↔ plugin-runtime ↔ plugins): 통과
- Bounded Context 간 직접 참조 금지: 통과
- File ↔ Indexing 완전 단절: 통과

## 5) 남은 TODO 또는 확장 포인트(v2 이상)
- 페이지네이션 처리
- 오류 코드 표준화
# 작업 종료 체크리스트
- 단위 테스트 실행 및 성공할 때까지 수정 반복
- archtest(아키텍처 규칙 테스트) 실행

