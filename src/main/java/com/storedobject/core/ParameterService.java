package com.storedobject.core;

import com.storedobject.common.JSON;
import com.storedobject.common.StringList;

import jakarta.servlet.http.HttpServletRequest;
import java.io.PrintWriter;
import java.util.Map;

/**
 * Parameter-driven web service interface. However, such a service can use just JSON too.
 *
 * @author Syam
 */
public interface ParameterService {
	
	String UNAUTHORIZED = "Unauthorized";
	String OK = "Ok";
	
	/**
	 * Name of the service.
	 * @return Name.
	 */
	String getName();
	
	/**
	 * Set the "Transaction Manager". This method will be invoked by the platform with the currently active Transaction
	 * Manager.
	 * @param tm Transaction Manager.
	 */
	void setTransactionManager(TransactionManager tm);

	/**
	 * Gets the mandatory set of parameters to be present when calling this service. If any one of these parameters is
	 * not present, this service will not be invoked.
	 * @return List of mandatory parameters.
	 */
	StringList getMandatoryParameters();

	/**
	 * This checks whether this service requires a POST method or not (means no GET method is allowed). The default is "true"
	 * if the current Application Server is not in "developer" mode.
	 * @param applicationServer Current application server.
	 * @return True if the service requires a POST method.
	 */
	default boolean requiresPOST(ApplicationServer applicationServer) {
		return !applicationServer.isDeveloper();
	}

	/**
	 * Sets the headers for the service. This method allows a map of header
	 * key-value pairs to be specified for the service.
	 *
	 * @param headers A map containing the headers to be set, where the key
	 *                represents the header name, and the value represents
	 *                the corresponding header value. You can add additional headers here.
	 */
	default void setHeaders(Map<String, String> headers) {
	}

	/**
	 * Implement the service here. If the service requires JSON body received for processing, implement the other
	 * "serve" method that passes JSON as an additional parameter. (The default implementation does nothing).
	 * @param parameters Map of the parameters received.
	 * @return Status of the service such as "Ok", "Created" etc. This will be sent as a JSON response.
	 * Example: { "Status": "Ok" }. If plain text needs to be sent as a response, the status should be prefixed with
	 * "Plain:". Example: "Plain:Error while creating login".
	 * @throws Exception Any
	 */
	default String serve(Map<String, String[]> parameters) throws Exception {
		return null;
	}
	
	/**
	 * Same like the other "serve" method except that an additional JSON parameter received is also passed here.
	 * (The default implementation simply invokes the other "serve" method, ignoring the JSON parameter).
	 * @param parameters Map of the parameters received.
	 * @param jsonReceived JSON parameter received. This could be null.
	 * @return Status like the other "serve" method.
	 * @throws Exception Any
	 */
	default String serve(Map<String, String[]> parameters, JSON jsonReceived) throws Exception {
		return serve(parameters);
	}

	/**
	 * Mime type of the content. If this method returns a non-null value, the writeResponse method should be implemented
	 * and none of the "serve" methods will be invoked.
	 * @return Mime type of the content. The default implementation returns "null".
	 */
	default String getContentType() {
		return null;
	}
	
	/**
	 * This will be invoked if the getContentType method returns a non-null value and the request contains JSON.
	 * In this case, none of the "serve" methods will be invoked.
	 * @param parameters Map of the parameters received.
	 * @param jsonReceived JSON parameter received.
	 * @param response Response needs to be written into this, and a format of that should match the mime type.
	 */
	default void writeResponse(Map<String, String[]> parameters, JSON jsonReceived, PrintWriter response) {
	}


	/**
	 * This will be invoked if the getContentType method returns a non-null value and the request does not contain JSON.
	 * In this case, none of the "serve" methods will be invoked.
	 * @param request Request received.
	 * @param parameters Map of the parameters received.
	 * @param response Response needs to be written into this, and a format of that should match the mime type.
	 */
	default void writeResponse(HttpServletRequest request, Map<String, String[]> parameters, PrintWriter response) {
	}
}
