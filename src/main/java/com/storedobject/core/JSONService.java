package com.storedobject.core;

/**
 * Interface defining the JSON Service interface. Any logic handling a SO Connector should implement this interface.
 * This service handles the execution of JSON requests and responses.
 *
 * @author Syam
 */
@FunctionalInterface
public interface JSONService {

	/**
	 * This method should be implemented to serve the connector API call. It carries out the execution logic of the call.
	 *
	 * @param device Device on which the call is made. For those logics for which authentication is not required,
	 *                  the device value will be null.
	 * @param json JSON request object received from the device.
	 * @param result A JSONMap where response data is inserted. To highlight an error, respond with
	 * {@link JSONMap#error(String)}.
	 */
	void execute(Device device, JSON json, JSONMap result);

	/**
	 * A marker interface that indicates that the JSON service does not require authentication.
	 * If this interface is implemented, it means that the service does not require authentication.
	 */
	interface OpenAccess extends JSONService {
	}
}
