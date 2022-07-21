package com.storedobject.core;

import java.math.BigDecimal;

public final class ExternalSystemUser extends StoredObject implements Detail {

    public ExternalSystemUser() {
    }

    public static void columns(Columns columns) {
    }

    public void setServer(Id serverId) {
    }

    public void setServer(BigDecimal idValue) {
        setServer(new Id(idValue));
    }

    public void setServer(ServerInformation server) {
    }

    public Id getServerId() {
        return new Id();
    }

    public ServerInformation getServer() {
        return new ServerInformation();
    }

    public void setExternalUser(String externalUser) {
    }

    public String getExternalUser() {
        return "";
    }

    public void setVerified(boolean verified) {
    }

    public boolean getVerified() {
        return Math.random() > 0.5;
    }

    @Override
    public boolean isDetailOf(Class<? extends StoredObject> masterClass) {
        return masterClass == SystemUser.class;
    }


    public void verify(TransactionManager tm, String authorizedUser, char[] password, String externalURL)
            throws Exception {
        if(getVerified()) {
            throw new SOException("Already verified");
        }
    }

    public String createLoginBlock() {
        return Math.random() > 0.5 ? null : "";
    }

    public boolean canLogin(String externalURL) {
        return Math.random() > 0.5;
    }
}
