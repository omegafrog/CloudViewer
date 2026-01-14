# 작업 목적
- User BC를 추가해 사용자 등록/로그인/관리 흐름을 구현한다.

# 진행 상태
- [x] User 도메인 모델(개인정보 VO 포함) 정의
- [x] username/password 회원가입/로그인 구현
- [x] Google OAuth 회원가입/로그인 구현
- [x] OAuth 공급자 확장용 추상화 계층 설계/적용
- [x] 세션 기반 로그인 처리
- [x] 관리자/일반 사용자 권한 모델 적용
- [x] 사용자 관리(관리자가 일반 사용자 관리) API 구현
- [x] 회원정보 수정 API 구현
- [x] 회원탈퇴(soft delete) 구현
- [x] 단위 테스트 작성 및 성공할 때까지 수정
- [x] archtest(아키텍처 규칙 테스트) 실행
- [x] 작업 기록 문서 작성

# 변경/추가 예정 주요 컴포넌트
- `modules/api/src/main/java/api/user/...`
- `modules/core/src/main/java/core/user/...`
- `modules/core/src/main/java/core/web/UserController.java`
- `modules/core/src/main/java/core/web/dto/...`
- `modules/core/src/test/java/core/user/...`
- `modules/core/src/test/java/core/web/...`

# 설계 규칙 및 스펙 대응
- 개인정보는 Value Object로 묶고 User가 이를 속성으로 보유한다.
- 로그인은 세션 기반으로 처리한다.
- 회원탈퇴는 soft delete로 처리한다.
- 관리자와 일반 사용자 권한을 구분하고, 관리자는 일반 사용자를 관리한다.
- username/password + Google OAuth 로그인/회원가입을 모두 지원한다.
- OAuth 공급자는 추상화 계층을 통해 확장 가능하게 설계한다.

# 테스트 및 검증 계획
- 기능 구현 후 단위 테스트 작성
- 테스트가 통과할 때까지 반복 수정
- archtest(아키텍처 규칙 테스트) 실행

# 남은 TODO / 확장 포인트(v2 이상)
- 사용자 정책 확장(권한 세분화, 감사 로그 등)
- OAuth 공급자 추가

# 작업 종료 체크리스트
- 단위 테스트 실행 및 성공할 때까지 수정 반복
- archtest(아키텍처 규칙 테스트) 실행
