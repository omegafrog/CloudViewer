# OneDrive 플러그인 구현 작업 기록

## 1) 작업 목적
OneDrive 플러그인의 read-only 동작을 구현하고 로컬 파일 기반 인덱싱/조회 흐름을 검증한다.

## 2) 변경/추가된 주요 컴포넌트 요약
- `plugins.onedrive.OneDrivePlugin`: 가용성 판정 및 커넥터 제공
- `plugins.onedrive.OneDriveRepositoryConnector`: rootPath 검증 및 핸들 생성
- `plugins.onedrive.OneDriveRepositoryHandle`: FileHandle 제공
- `plugins.onedrive.OneDriveFileHandle`: 로컬 파일 get/list/download 구현
- `META-INF/services/api.plugin.RepositoryPlugin`: ServiceLoader 등록
- 플러그인 유닛 테스트 추가

## 3) 적용한 설계 규칙과 스펙과의 대응 관계
- plugin은 api 인터페이스만 의존하여 core 직접 참조 금지
- read-only 범위에서 파일 조회/다운로드만 제공
- RepositoryDescriptor.config의 rootPath로 로컬 파일 접근

## 4) 검증한 아키텍처 규칙 목록과 결과
- 모듈 의존 방향(core ↔ api ↔ plugin-runtime ↔ plugins): 통과
- Bounded Context 간 직접 참조 금지: 통과
- File ↔ Indexing 완전 단절: 통과

## 5) 남은 TODO 또는 확장 포인트(v2 이상)
- 에러 코드 표준화
- 페이지네이션 처리
