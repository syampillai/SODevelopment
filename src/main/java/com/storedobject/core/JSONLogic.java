package com.storedobject.core;

import com.storedobject.common.JSON;

import java.util.Map;

public class JSONLogic implements JSONService {
	
	protected JSON json;
	protected Map<String, Object> result;
	protected Device device;
	protected TransactionManager tm;

	@Override
	public void execute(Device device, JSON json, Map<String, Object> result) {
	}
	
	public void error(String error) {
	}
	
	public void dataError() {
	}
	
	public void dataError(String data) {
	}
	
	public boolean nullAction() {
		return false;
	}
	
	public boolean action(String action) {
		return false;
	}
	
	public boolean process() {
		return false;
	}
}