# 아키텍처 규칙 검증 작업 기록

## 1) 작업 목적
모듈 의존 방향과 BC 경계, File ↔ Indexing 단절 규칙을 검증한다.

## 2) 변경/추가된 주요 컴포넌트 요약
- 변경 없음(검증 실행만 수행)

## 3) 적용한 설계 규칙과 스펙과의 대응 관계
- core는 api를 통해서만 plugin과 연결
- plugin 구현체는 core에 의존하지 않음
- indexing은 file/repository와 완전 단절

## 4) 검증한 아키텍처 규칙 목록과 결과
- 모듈 의존 방향(core ↔ api ↔ plugin-runtime ↔ plugins): 통과
- Bounded Context 간 직접 참조 금지: 통과
- File ↔ Indexing 완전 단절: 통과

## 5) 남은 TODO 또는 확장 포인트(v2 이상)
- 규칙 위반 시 자동 리포트 포맷 정리
# 작업 종료 체크리스트
- 단위 테스트 실행 및 성공할 때까지 수정 반복
- archtest(아키텍처 규칙 테스트) 실행

