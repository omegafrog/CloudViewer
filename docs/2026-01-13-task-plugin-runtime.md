# plugin-runtime 구현 작업 기록

## 1) 작업 목적
plugin-runtime 모듈을 스펙대로 구현하여 플러그인 로딩과 레지스트리 동작을 확정한다.

## 2) 변경/추가된 주요 컴포넌트
- `plugin.runtime.PluginLoader`: JAR에서 `RepositoryPlugin` 구현체를 ServiceLoader로 로딩
- `plugin.runtime.PluginRegistry`: repositoryType 기반 등록/조회, 중복 타입 등록 방지

## 3) 적용한 설계 규칙과 스펙 대응
- Plugin BC 컴포넌트(`PluginLoader`, `PluginRegistry`)를 스펙의 패키지/시그니처로 유지
- plugin-runtime은 `api`에만 의존하며 core 또는 plugin 구현체에 대한 직접 참조 없음
- ServiceLoader 기반 로딩 규칙을 그대로 반영

## 4) 검증한 아키텍처 규칙 목록과 결과
- 모듈 의존 방향(core ↔ api ↔ plugin-runtime ↔ plugins): 통과
- Bounded Context 간 직접 참조 금지: 통과
- File ↔ Indexing 완전 단절: 통과

## 5) 남은 TODO / 확장 포인트(v2+)
- 플러그인 버전 호환성 정책
- 플러그인 등록 해제/갱신 정책
