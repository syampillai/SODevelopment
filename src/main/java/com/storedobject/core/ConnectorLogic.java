package com.storedobject.core;

public final class ConnectorLogic extends StoredObject implements RequiresApproval {

    public ConnectorLogic() {
    }

    public static void columns(Columns columns) {
    }

    public void setModuleName(String moduleName) {
    }

    public String getModuleName() {
    	return null;
    }

    public void setConnectorCommand(String connectorCommand) {
    }

    public String getConnectorCommand() {
    	return null;
    }
    
    public void setLogicName(String logicName) {
    }

    public String getLogicName() {
    	return null;
    }

    public void setActive(boolean active) {
    }
    
    public boolean getActive() {
    	return false;
    }

    public static ConnectorLogic get(String moduleName, String connectorCommand) {
    	return null;
    }
    
	public Class<JSONService> getServiceClass() {
    	return null;
    }
}