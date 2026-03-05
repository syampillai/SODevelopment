package com.storedobject.core;

import java.lang.reflect.Modifier;

public final class ConnectorLogic extends StoredObject implements RequiresApproval {

    private String connectorCommand;
    private String logicName;
    private boolean active;
    private Class<JSONService> serviceClass;

    public ConnectorLogic() {
    	active = true;
    }

    public static void columns(Columns columns) {
        columns.add("ConnectorCommand", "text");
        columns.add("LogicName", "text");
        columns.add("Active", "boolean");
    }

    public static void indices(Indices indices) {
        indices.add("lower(ConnectorCommand)", true);
        indices.add("lower(LogicName)");
    }

    @Override
	public String getUniqueCondition() {
        return "lower(ConnectorCommand)='" + getConnectorCommand().trim().toLowerCase().
                replace("'", "''") + "'";
    }

    public void setConnectorCommand(String connectorCommand) {
        this.connectorCommand = connectorCommand;
    }

    public String getConnectorCommand() {
        return connectorCommand;
    }

    public void setLogicName(String logicName) {
        this.logicName = logicName;
    }

    public String getLogicName() {
        return logicName;
    }
    
    public void setActive(boolean active) {
    	this.active = active;
    }
    
    public boolean getActive() {
    	return active;
    }

    @Override
	public void validateData(TransactionManager tm) throws Exception {
        connectorCommand = StringUtility.pack(connectorCommand);
        if(StringUtility.isWhite(connectorCommand)) {
            throw new Invalid_Value("Connector Command");
        }
        if(StringUtility.isWhite(logicName)) {
            throw new Invalid_Value("Logic Name");
        }
        super.validateData(tm);
    }
    
    public static ConnectorLogic get(String connectorCommand) {
    	return get(ConnectorLogic.class, "lower(ConnectorCommand)='" + connectorCommand.toLowerCase() + "'");
    }
    
	public Class<JSONService> getServiceClass(Device device) {
    	if(serviceClass != null) {
    		return serviceClass;
    	}
    	try {
    		@SuppressWarnings("unchecked")
            Class<JSONService> sc = (Class<JSONService>)JavaClassLoader.getLogic(logicName, true,
                    device.getServer().getTransactionManager());
    		if(Modifier.isAbstract(sc.getModifiers())) {
    			return null;
    		}
            if(sc.getName().indexOf(JavaClassLoader.VERSION_SEPARATOR) < 0) {
                serviceClass = sc;
            }
    		return sc;
		} catch(Throwable error) {
			return null;
		}
    }
}