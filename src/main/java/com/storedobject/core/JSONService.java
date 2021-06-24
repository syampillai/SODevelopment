package com.storedobject.core;

import com.storedobject.common.JSON;

import java.util.Map;

@FunctionalInterface
public interface JSONService {
	void execute(Device device, JSON json, Map<String, Object> result);
}