package com.storedobject.oauth;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.util.Base64;

public class OAuth {

    private static final SecureRandom random;
    static {
        SecureRandom sr;
        try {
            sr = SecureRandom.getInstance("SHA1PRNG");
        } catch (NoSuchAlgorithmException e) {
            sr = new SecureRandom();
        }
        random = sr;
    }
    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final String ourUrl;
    private final URI serverUrl;
    private final Cipher aesCipher;
    private SecretKey secretKey;
    private final int state;

    public OAuth(String ourUrl, String secretKey) {
        this(ourUrl, null, secretKey);
    }

    public OAuth(String ourUrl, String serverUrl, String secretKey) {
        if(serverUrl == null) {
            serverUrl = ourUrl;
        } else if(!serverUrl.endsWith("/oauth/callback")) {
            if(!serverUrl.endsWith("/")) {
                serverUrl += "/";
            }
            if(!serverUrl.endsWith("oauth/")) {
                serverUrl += "oauth/";
            }
            serverUrl += "callback";
        }
        if(!ourUrl.startsWith("https://")) {
            ourUrl = "https://" + ourUrl;
        }
        this.ourUrl = ourUrl;
        if(!serverUrl.startsWith("https://")) {
            serverUrl = "https://" + serverUrl;
        }
        Cipher aesCipher;
        try {
            aesCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        } catch (Exception e) {
            aesCipher = null;
        }
        this.aesCipher = aesCipher;
        URI uri;
        try {
            uri = new URI(serverUrl);
        } catch (URISyntaxException e) {
            uri = null;
        }
        this.serverUrl = uri;
        if(uri == null || aesCipher == null) {
            state = -1;
            return;
        }
        if(secretKey == null || secretKey.isEmpty()) {
            state = -1;
            this.secretKey = genSecretKey();
            return;
        } else {
            this.secretKey = new SecretKeySpec(Base64.getDecoder().decode(secretKey), "AES");
        }
        state = 1;
    }

    public String getSecret() {
        return state == -1 ? (ourUrl + "|" + Base64.getEncoder().encodeToString(secretKey.getEncoded())) : null;
    }

    private static SecretKey genSecretKey() {
        KeyGenerator keyGen;
        try {
            keyGen = KeyGenerator.getInstance("AES");
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
        keyGen.init(256);
        return keyGen.generateKey();
    }

    private String send(String data) {
        if(isError()) {
            return null;
        }
        data = ourUrl + "|" + encrypt(data);
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(serverUrl)
                    .header("Content-Type", "text/plain")
                    .POST(HttpRequest.BodyPublishers.ofString(data))
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                return "ERROR:Response code: "+ response.statusCode();
            }
            return decrypt(response.body());
        } catch (IOException | InterruptedException e) {
            return null;
        }
    }

    private static String e(String s) {
        return URLEncoder.encode(s, StandardCharsets.UTF_8);
    }

    public String getURL(String provider) {
        String url = send("action=requestURL\nprovider=" + provider + "\n");
        return url != null && url.startsWith("OK:") ? url.substring(3) : null;
    }

    public boolean isError() {
        return state == -1;
    }

    private String encrypt(String data) {
        byte[] iv = new byte[16];
        random.nextBytes(iv);
        IvParameterSpec ivSpec = new IvParameterSpec(iv);
        try {
            aesCipher.init(Cipher.ENCRYPT_MODE, secretKey, ivSpec);
            byte[] ciphertext = aesCipher.doFinal(data.getBytes(StandardCharsets.UTF_8));
            byte[] ivAndCiphertext = new byte[iv.length + ciphertext.length];
            System.arraycopy(iv, 0, ivAndCiphertext, 0, iv.length);
            System.arraycopy(ciphertext, 0, ivAndCiphertext, iv.length, ciphertext.length);
            return Base64.getEncoder().encodeToString(ivAndCiphertext);
        } catch (Exception e) {
            return null;
        }
    }

    private String decrypt(String data) {
        byte[] ivAndCiphertext = Base64.getDecoder().decode(data);
        byte[] iv = new byte[16];
        System.arraycopy(ivAndCiphertext, 0, iv, 0, iv.length);
        IvParameterSpec ivSpec = new IvParameterSpec(iv);
        byte[] ciphertext = new byte[ivAndCiphertext.length - iv.length];
        System.arraycopy(ivAndCiphertext, iv.length, ciphertext, 0, ciphertext.length);
        try {
            aesCipher.init(Cipher.DECRYPT_MODE, secretKey, ivSpec);
            return new String(aesCipher.doFinal(ciphertext), StandardCharsets.UTF_8);
        } catch (Exception e) {
            return null;
        }
    }

    public String addProvider(String provider, String clientId, String clientSecret, String authUrl, String tokenUrl,
                              String userInfoUrl, String scope) {
        if(isError()) {
            return "Not initialized";
        }
        StringBuilder s = new StringBuilder();
        s.append("action=provider\n");
        s.append("provider=").append(e(provider)).append("\n");
        s.append("clientId=").append(e(clientId)).append("\n");
        s.append("clientSecret=").append(e(clientSecret)).append("\n");
        if(authUrl != null) {
            s.append("authUrl=").append(e(authUrl)).append("\n");
        }
        if(tokenUrl != null) {
            s.append("tokenUrl=").append(e(tokenUrl)).append("\n");
        }
        if(userInfoUrl != null) {
            s.append("userInfoUrl=").append(e(userInfoUrl)).append("\n");
        }
        if(scope != null) {
            s.append("scope=").append(e(scope)).append("\n");
        }
        String r = send(s.toString());
        if(r == null) {
            return "Unknown error";
        }
        if(r.startsWith("ERROR:")) {
            return r.substring(6);
        }
        return null;
    }

    public String getUserInfo(String key) {
        key = decrypt(key);
        int p;
        if(key == null || (p = key.indexOf("|")) < 1) {
            return null;
        }
        String userInfo = send("action=oauth\noauth=" + key + "\n");
        return userInfo == null || !userInfo.startsWith("OK:") ? null :
                (key.substring(0, p + 1) + userInfo.substring(3));
    }
}