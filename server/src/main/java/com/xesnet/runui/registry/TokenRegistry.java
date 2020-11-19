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

        String id = UUID.randomUUID().toString();
        TokenInfo tokenInfo = new TokenInfo(id, login);
        loginToTokenInfos.put(id, tokenInfo);

        return tokenInfo;
    }

    public synchronized void logout(String token) {
        loginToTokenInfos.remove(token);
    }

    public synchronized TokenInfo getTokenInfo(String id) {
        TokenInfo tokenInfo = loginToTokenInfos.get(id);
        if (tokenInfo != null) {
            if (tokenInfo.getLastDateTime().plusSeconds(tokenTimeout).isAfter(LocalDateTime.now())) {
                return tokenInfo;
            } else {
                loginToTokenInfos.remove(id);
            }
        }

        return null;
    }

    private synchronized void cleanExpiredToken() {
        LocalDateTime now = LocalDateTime.now();
        this.loginToTokenInfos.entrySet().removeIf(entry -> entry.getValue().getLastDateTime().plusSeconds(tokenTimeout).isBefore(now));
    }

    public static class TokenInfo {

        private final String id;
        private final String login;
        private LocalDateTime lastDateTime;

        public TokenInfo(String id, String login) {
            this.id = id;
            this.login = login;

            resetLastDateTime();
        }

        public String getId() {
            return id;
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
