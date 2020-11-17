package com.xesnet.runui.registry;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


/**
 * @author Pierre PINON
 */
public class TokenRegistry {

    private final Integer tokenTimeout;
    private final Map<String, TokenInfo> loginToTokenInfos;

    public TokenRegistry(Integer tokenTimeout) {
        this.tokenTimeout = tokenTimeout;

        this.loginToTokenInfos = new HashMap<>();
    }

    public synchronized TokenInfo login(String login) {
        cleanExpiredToken();

        String token = UUID.randomUUID().toString();
        TokenInfo tokenInfo = new TokenInfo(token, login);
        loginToTokenInfos.put(token, tokenInfo);

        return tokenInfo;
    }

    public synchronized void logout(String token) {
        loginToTokenInfos.remove(token);
    }

    public synchronized TokenInfo getTokenInfo(String token) {
        TokenInfo tokenInfo = loginToTokenInfos.get(token);
        if (tokenInfo != null) {
            if (tokenInfo.getLastDateTime().plusSeconds(tokenTimeout).isAfter(LocalDateTime.now())) {
                return tokenInfo;
            } else {
                loginToTokenInfos.remove(token);
            }
        }

        return null;
    }

    private synchronized void cleanExpiredToken() {
        LocalDateTime now = LocalDateTime.now();
        this.loginToTokenInfos.entrySet().removeIf(entry -> entry.getValue().getLastDateTime().plusSeconds(tokenTimeout).isBefore(now));
    }

    public static class TokenInfo {

        private final String token;
        private final String login;
        private LocalDateTime lastDateTime;

        public TokenInfo(String token, String login) {
            this.token = token;
            this.login = login;

            resetLastDateTime();
        }

        public String getToken() {
            return token;
        }

        public String getLogin() {
            return login;
        }

        public LocalDateTime getLastDateTime() {
            return lastDateTime;
        }

        public void resetLastDateTime() {
            lastDateTime = LocalDateTime.now();
        }
    }
}
