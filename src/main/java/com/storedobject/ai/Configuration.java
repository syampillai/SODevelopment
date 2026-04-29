package com.storedobject.ai;

import com.storedobject.core.*;
import com.storedobject.core.annotation.*;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class Configuration extends StoredObject {

    private static final Map<Id, Configuration> instances = new HashMap<>();
    private static final String[] modelProviderValues =
            new String[] {
                    "Deep Seek", "Open AI", "Google",
            };
    private int modelProvider = 0;
    private String name;
    private Id tokenId = Id.ZERO;
    private String baseURL;
    private String modelName;
    private boolean isActive;
    private APIToken token;

    public Configuration() {}

    public static void columns(Columns columns) {
        columns.add("ModelProvider", "int");
        columns.add("Name", "text");
        columns.add("Token", "id");
        columns.add("BaseURL", "text");
        columns.add("ModelName", "text");
        columns.add("IsActive", "boolean");
    }

    public static void indices(Indices indices) {
        indices.add("lower(Name)", true);
    }

    @Override
    public String getUniqueCondition() {
        return "lower(Name)='" + getName().trim().toLowerCase().replace("'", "''") + "'";
    }

    public static Configuration get(String name) {
        return StoredObjectUtility.get(Configuration.class, "Name", name, false);
    }

    public static ObjectIterator<Configuration> list(String name) {
        return StoredObjectUtility.list(Configuration.class, "Name", name, false);
    }

    public static int hints() {
        return ObjectHint.SMALL | ObjectHint.SMALL_LIST;
    }

    public void setModelProvider(int modelProvider) {
        this.modelProvider = modelProvider;
    }

    @Column(order = 50)
    public int getModelProvider() {
        return modelProvider;
    }

    public static String[] getModelProviderValues() {
        return modelProviderValues;
    }

    public static String getModelProviderValue(int value) {
        String[] s = getModelProviderValues();
        return s[value % s.length];
    }

    public String getModelProviderValue() {
        return getModelProviderValue(modelProvider);
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(style = "(code)", order = 100)
    public String getName() {
        return name;
    }

    public void setToken(Id tokenId) {
        this.tokenId = tokenId;
    }

    public void setToken(BigDecimal idValue) {
        setToken(new Id(idValue));
    }

    public void setToken(APIToken token) {
        setToken(token == null ? null : token.getId());
    }

    @Column(required = false, order = 200)
    public Id getTokenId() {
        return tokenId;
    }

    public APIToken getToken() {
        if(token == null) {
            token = getRelated(APIToken.class, tokenId);
        }
        return token;
    }

    public void setBaseURL(String baseURL) {
        this.baseURL = baseURL;
    }

    @Column(required = false, order = 300)
    public String getBaseURL() {
        return baseURL;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    @Column(order = 400)
    public String getModelName() {
        return modelName;
    }

    public void setIsActive(boolean isActive) {
        this.isActive = isActive;
    }

    public void setIsActive(Boolean isActive) {
        setIsActive(isActive != null && isActive);
    }

    @Column(order = 500)
    public boolean getIsActive() {
        return isActive;
    }

    @Override
    public void validateData(TransactionManager tm) throws Exception {
        if (StringUtility.isWhite(name)) {
            throw new Invalid_Value("Name");
        }
        name = toCode(name);
        checkForDuplicate("Name");
        tokenId = tm.checkType(this, tokenId, APIToken.class, true);
        if (StringUtility.isWhite(modelName)) {
            throw new Invalid_Value("Model Name");
        }
        super.validateData(tm);
    }

    @Override
    public void saved() {
        token = null;
        instances.put(getId(), this);
    }

    String token(SystemUser su) {
        return getToken().canAccess(su) ? token.getToken() : null;
    }

    static Configuration anyConfiguration(SystemUser su) {
        if(instances.isEmpty()) {
            list(Configuration.class, "IsActive").forEach(c -> instances.put(c.getId(), c));
        }
        for(Configuration c : instances.values()) {
            if(c.getToken().canAccess(su)) {
                return c;
            }
        }
        return null;
    }
}
