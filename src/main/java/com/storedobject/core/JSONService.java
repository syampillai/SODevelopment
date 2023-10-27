package com.storedobject.core;

/**
 * Interface defining the JSON Service interface. (SO Connector logic should implement this interface).
 *
 * @author Syam
 */
@FunctionalInterface
public interface JSONService {

	/**
	 * Method to be implemented to serve the connector API call.
	 *
	 * @param device Device on which the call is made.
	 * @param json JSON request.
	 * @param result Response should be added to this map. To indicate an error invoke
	 * {@link JSONMap#error(String)}.
	 */
	void execute(Device device, JSON json, JSONMap result);
}
