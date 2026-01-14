# plugin-runtime 구현 작업 기록

## 1) 작업 목적
plugin-runtime 모듈의 로더/레지스트리를 실사용 기준으로 점검하고, 유닛 테스트로 동작을 고정한다.

## 2) 변경/추가된 주요 컴포넌트 요약
- `plugin.runtime.PluginLoaderTest`: ServiceLoader 기반 JAR 로딩 성공/실패 케이스 검증
- `plugin.runtime.PluginRegistryTest`: repositoryType 등록/중복 처리 검증
- 테스트용 플러그인 구현체 및 핸들 스켈레톤 추가

## 3) 적용한 설계 규칙과 스펙과의 대응 관계
- PluginLoader는 ServiceLoader 기반 로딩만 수행하도록 유지
- PluginRegistry는 repositoryType 기반 등록/조회 정책 유지
- 테스트 구현체는 api 인터페이스만 의존하여 경계 위반 없음

## 4) 검증한 아키텍처 규칙 목록과 결과
- 모듈 의존 방향(core ↔ api ↔ plugin-runtime ↔ plugins): 통과
- Bounded Context 간 직접 참조 금지: 통과
- File ↔ Indexing 완전 단절: 통과

## 5) 남은 TODO 또는 확장 포인트(v2 이상)
- 플러그인 버전 호환 정책
- 로더 캐싱/라이프사이클 정책
# 작업 종료 체크리스트
- 단위 테스트 실행 및 성공할 때까지 수정 반복
- archtest(아키텍처 규칙 테스트) 실행

