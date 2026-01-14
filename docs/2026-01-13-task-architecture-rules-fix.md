# 아키텍처 규칙 실행 오류 수정 기록

## 1) 작업 목적
architectureRules 실행 시 발생한 exec 호출 오류를 수정한다.

## 2) 변경/추가된 주요 컴포넌트 요약
- `build.gradle`: verifyPluginJarDependencies에서 ExecOperations 사용

## 3) 적용한 설계 규칙과 스펙과의 대응 관계
- 규칙 검증 로직 자체는 유지하고 실행 방식만 수정

## 4) 검증한 아키텍처 규칙 목록과 결과
- 모듈 의존 방향(core ↔ api ↔ plugin-runtime ↔ plugins): 통과
- Bounded Context 간 직접 참조 금지: 통과
- File ↔ Indexing 완전 단절: 통과

## 5) 남은 TODO 또는 확장 포인트(v2 이상)
- 아키텍처 규칙 실행 속도 개선
