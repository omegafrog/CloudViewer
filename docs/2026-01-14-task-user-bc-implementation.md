# 작업 목적
- User BC의 회원가입/로그인/관리 기능을 세션 기반으로 구현한다.

# 변경/추가된 주요 컴포넌트 요약
- `modules/api/src/main/java/api/user/UserId.java`
- `modules/api/src/main/java/api/user/PersonalInfo.java`
- `modules/api/src/main/java/api/user/UserProfile.java`
- `modules/api/src/main/java/api/user/UserRole.java`
- `modules/api/src/main/java/api/user/UserStatus.java`
- `modules/api/src/main/java/api/user/UserSession.java`
- `modules/core/src/main/java/core/user/UserAccount.java`
- `modules/core/src/main/java/core/user/UserCatalog.java`
- `modules/core/src/main/java/core/user/UserService.java`
- `modules/core/src/main/java/core/user/SessionCatalog.java`
- `modules/core/src/main/java/core/user/PasswordHasher.java`
- `modules/core/src/main/java/core/user/oauth/OAuthProvider.java`
- `modules/core/src/main/java/core/user/oauth/OAuthProviderRegistry.java`
- `modules/core/src/main/java/core/user/oauth/GoogleOAuthProvider.java`
- `modules/core/src/main/java/core/user/oauth/OAuthAuthorizationRequest.java`
- `modules/core/src/main/java/core/user/oauth/OAuthCallbackRequest.java`
- `modules/core/src/main/java/core/user/oauth/OAuthStateCatalog.java`
- `modules/core/src/main/java/core/user/oauth/GoogleOAuthProperties.java`
- `modules/core/src/main/resources/application.yml`
- `modules/core/src/main/resources/application-secret.yml`
- `modules/core/src/main/java/core/web/UserController.java`
- `modules/core/src/main/java/core/web/dto/UserRegisterRequest.java`
- `modules/core/src/main/java/core/web/dto/UserLoginRequest.java`
- `modules/core/src/main/java/core/web/dto/OAuthAuthorizeRequest.java`
- `modules/core/src/main/java/core/web/dto/OAuthAuthorizeResponse.java`
- `modules/core/src/main/java/core/web/dto/OAuthLoginRequest.java`
- `modules/core/src/main/java/core/web/dto/UserSessionRequest.java`
- `modules/core/src/main/java/core/web/dto/UserUpdateRequest.java`
- `modules/core/src/main/java/core/web/dto/PersonalInfoUpdateRequest.java`
- `modules/core/src/main/java/core/web/dto/AdminUserUpdateRequest.java`
- `modules/core/src/main/java/core/web/dto/AdminUserDeactivateRequest.java`
- `modules/core/src/main/java/core/web/dto/UserProfileResponse.java`
- `modules/core/src/main/java/core/web/dto/UserSessionResponse.java`
- `modules/core/src/test/java/core/user/UserServiceTest.java`
- `modules/core/src/test/java/core/user/UserCatalogTest.java`
- `modules/core/src/test/java/core/web/UserControllerTest.java`
- `modules/api/src/test/java/api/user/PersonalInfoTest.java`
- `modules/api/src/test/java/api/user/UserProfileTest.java`
- `modules/api/src/test/java/api/user/UserSessionTest.java`

# 적용한 설계 규칙과 스펙과의 대응 관계
- 개인정보는 `PersonalInfo` VO로 묶고 `UserAccount`가 이를 보유하도록 구성했다.
- 로그인은 세션 기반으로 구현하고, 세션은 `SessionCatalog`에서 관리한다.
- username/password와 Google OAuth 로그인을 모두 지원하며, OAuth 공급자는 추상화 계층(`OAuthProvider`)으로 분리했다.
- OAuth는 authorize → callback 흐름으로 구성하고 state 검증(`OAuthStateCatalog`)을 포함한다. 콜백은 GET `/users/oauth/google/callback`에서 처리하며 로그인 시 세션 쿠키를 발급한다.
- 회원탈퇴는 `UserStatus.DELETED`로 soft delete 처리한다.
- 관리자 권한 검증 후 일반 사용자 관리 API를 제공한다.

# 검증한 아키텍처 규칙 목록과 결과
- ArchUnit 테스트 실행: 성공
- 실행 명령: `./gradlew :modules:core:test --tests core.ArchitectureRulesTest --no-daemon`

# 남은 TODO 또는 확장 포인트(v2 이상)
- OAuth 공급자 추가 및 정책 확장
- 관리자 권한 세분화 및 감사 로그

# 작업 종료 체크리스트
- 단위 테스트 실행 및 성공할 때까지 수정 반복
- archtest(아키텍처 규칙 테스트) 실행
