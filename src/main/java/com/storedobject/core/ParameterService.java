package com.storedobject.core;

import com.storedobject.common.JSON;
import com.storedobject.common.StringList;

import java.io.PrintWriter;
import java.util.Map;

public interface ParameterService {
	
	public static final String UNAUTHORIZED = "Unauthorized";
	public static final String OK = "Ok";
	
	/**
	 * Name of the service.
	 * @return Name.
	 */
	public String getName();
	
	/**
	 * Set "Transaction Manager". This method will be invoked by the framework with the currently active Transaction Manager.
	 * @param tm Transaction Manager.
	 */
	public void setTransactionManager(TransactionManager tm);
	
	/**
	 * Gets the mandatory set of parameters to be present when calling this service. If any one of these parameters is not present, this
	 * service will not be invoked.
	 * @return List of mandatory parameters.
	 */
	public StringList getMandatoryParameters();

	/**
	 * This checks whether this service requires POST method or not (means no GET method is allowed). Default is "true" if the current
	 * Application Server is not in "developer" mode.
	 * @param applicationServer Current application server.
	 * @return True if the service requires POST method.
	 */
	public default boolean requiresPOST(ApplicationServer applicationServer) {
		return !applicationServer.isDeveloper();
	}
	
	/**
	 * Implement the service here. If the service requires JSON body received for processing, implement the other "serve" method that passes
	 * JOSN as an additional parameter. (The default implementation does nothing).
	 * @param parameters Map of the parameters received.
	 * @return Status of the service such as "Ok", "Created" etc. This will be send as JSON response. Example: { "Status": "Ok" }. If plain
	 * text needs to be sent as response, the status should be prefixed with "Plain:". Example: "Plain:Error while creating login".
	 * @throws Exception Any
	 */
	public default String serve(Map<String, String[]> parameters) throws Exception {
		return null;
	}
	
	/**
	 * Same like the other "serve" method except that additional JSON parameter received is also passed here. (The default implementation
	 * simply invokes the other "serve" method, ignoring the JSON parameter).
	 * @param parameters Map of the parameters received.
	 * @param jsonReceived JSON parameter received.
	 * @return Status like the other "serve" method.
	 * @throws Exception Any
	 */
	public default String serve(Map<String, String[]> parameters, JSON jsonReceived) throws Exception {
		return serve(parameters);
	}

	/**
	 * Mime type of the content. If this method returns non-null value, writeResponse method should be implemented and none of the "serve"
	 * methods will be invoked.
	 * @return Mime type of the content. Default implementation returns "null".
	 */
	public default String getContentType() {
		return null;
	}
	
	/**
	 * This will be invoked if the getContentType method returns a non-null value. In this case, none of the "serve" methods will be invoked.
	 * @param parameters Map of the parameters received.
	 * @param jsonReceived JSON parameter received.
	 * @param response Response needs to be written into this and format of that should match the mime type.
	 */
	public default void writeResponse(Map<String, String[]> parameters, JSON jsonReceived, PrintWriter response) {
	}
}
