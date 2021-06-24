package com.storedobject.core;

import com.storedobject.common.MethodInvoker;

import java.lang.reflect.Method;

public class SerialNumberMethodInvoker implements MethodInvoker {
	
	public SerialNumberMethodInvoker() {
	}

	public SerialNumberMethodInvoker(String name) {
	}

	public SerialNumberMethodInvoker(String name, int width) {
	}

	public SerialNumberMethodInvoker(String name, int width, char pad) {
	}

	@Override
	public String getAttributeName() {
		return null;
	}
	
	@Override
	public Method getTail() {
		return null;
	}
	
	public String next() {
		return null;
	}

	@Override
	public String invoke(Object object) {
		return null;
	}
}
