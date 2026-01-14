# 플러그인 로딩 구현 작업 기록

## 1) 작업 목적
앱 실행 시 modules/plugins 디렉토리의 플러그인 JAR을 자동 로딩하도록 구성한다.

## 2) 변경/추가된 주요 컴포넌트 요약
- `core.config.PluginBootstrapper`: 플러그인 JAR 스캔 및 로딩
- `core.config.PluginRuntimeConfig`: PluginLoader/PluginRegistry/로딩 러너 구성
- `PluginBootstrapperTest`: 플러그인 로딩 유닛 테스트

## 3) 적용한 설계 규칙과 스펙과의 대응 관계
- core는 plugin-runtime을 통해서만 플러그인 접근
- api 경계 유지, plugin 구현체 직접 참조 없음

## 4) 검증한 아키텍처 규칙 목록과 결과
- 모듈 의존 방향(core ↔ api ↔ plugin-runtime ↔ plugins): 통과
- Bounded Context 간 직접 참조 금지: 통과
- File ↔ Indexing 완전 단절: 통과

## 5) 남은 TODO 또는 확장 포인트(v2 이상)
- 플러그인 로딩 실패 시 정책(무시/중단) 세분화
# 작업 종료 체크리스트
- 단위 테스트 실행 및 성공할 때까지 수정 반복
- archtest(아키텍처 규칙 테스트) 실행

