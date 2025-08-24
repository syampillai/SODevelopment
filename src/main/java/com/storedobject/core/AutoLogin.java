package com.storedobject.core;

import com.storedobject.core.annotation.*;
import java.math.BigDecimal;
import java.util.*;

public class AutoLogin extends StoredObject {

    private static final String[] loginMethodValues =
            new String[] {
                    "JWT (JSON Web Token)",
            };
    private String via;
    private String signingSecret, encryptionSecret;
    private Id systemUserId;
    private int loginMethod = 0;

    public AutoLogin() {
    }

    public static void columns(Columns columns) {
    }

    public void setVia(String via) {
        this.via = via;
    }

    @Column(order = 100)
    public String getVia() {
        return via;
    }

    public void setSigningSecret(String signingSecret) {
        this.signingSecret = signingSecret;
    }

    @Column(style = "(large)", order = 300)
    public String getSigningSecret() {
        return signingSecret;
    }

    public void setEncryptionSecret(String encryptionSecret) {
        this.encryptionSecret = encryptionSecret;
    }

    @Column(required = false, order = 350)
    public String getEncryptionSecret() {
        return encryptionSecret;
    }

    public void setSystemUser(Id systemUserId) {
        this.systemUserId = systemUserId;
    }

    public void setSystemUser(BigDecimal idValue) {
        setSystemUser(new Id(idValue));
    }

    public void setSystemUser(SystemUser systemUser) {
        setSystemUser(systemUser == null ? null : systemUser.getId());
    }

    @Column(order = 400)
    public Id getSystemUserId() {
        return systemUserId;
    }

    public SystemUser getSystemUser() {
        return getRelated(SystemUser.class, systemUserId);
    }

    public void setLoginMethod(int loginMethod) {
        this.loginMethod = loginMethod;
    }

    @Column(order = 500)
    public int getLoginMethod() {
        return loginMethod;
    }

    public static String[] getLoginMethodValues() {
        return loginMethodValues;
    }

    public static String getLoginMethodValue(int value) {
        String[] s = getLoginMethodValues();
        return s[value % s.length];
    }

    public String getLoginMethodValue() {
        return getLoginMethodValue(loginMethod);
    }

    public Map<String, Object> getPayload() {
        return new HashMap<>();
    }

    public static ObjectIterator<AutoLogin> listLogin4JWT(SystemUser su) {
        return list(AutoLogin.class, "SystemUser=" + su.getId() + " AND LoginMethod=0");
    }

    public static AutoLogin getLogin4JWT(String clientId) {
        return get(AutoLogin.class, "lower(Via)='" + toCode(clientId).toLowerCase() + "'");
    }

    public static AutoLogin createLogin4JWT(TransactionManager tm, SystemUser su, String clientId, boolean signed) throws Exception {
        return new AutoLogin();
    }

    public void reissueJWT(TransactionManager tm) throws Exception {
    }

    public String generateJWT() {
        return generateJWT(signingSecret, encryptionSecret, systemUserId.toString(), "SO", via, 0, null);
    }

    public static String generateJWT(
            String signingSecretBase64,
            String encryptionSecretBase64,
            String subject,
            String issuer,
            String audience,
            long ttlSeconds,
            Map<String, Object> additionalClaims
    ) {
        return "";
    }
}
