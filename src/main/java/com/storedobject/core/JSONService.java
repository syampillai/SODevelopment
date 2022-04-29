package com.storedobject.core;

import com.storedobject.common.JSON;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Map;

@FunctionalInterface
public interface JSONService {
	void execute(Device device, JSON json, Map<String, Object> result);

	static Date getDate(JSON json, String attribute) {
		try {
			return new Date(new SimpleDateFormat("yyyyMMdd").parse(json.getString(attribute)).getTime());
		} catch(Throwable e) {
			return null;
		}
	}
}
