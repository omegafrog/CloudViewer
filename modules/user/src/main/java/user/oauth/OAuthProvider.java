package user.oauth;

public interface OAuthProvider {
    String providerId();

    String defaultScope();

    String buildAuthorizationUrl(OAuthAuthorizationRequest request);

    OAuthUserInfo exchange(OAuthCallbackRequest request);
}
