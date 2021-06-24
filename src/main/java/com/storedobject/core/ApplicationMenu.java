package com.storedobject.core;

public interface ApplicationMenu {
	
	public void add(Logic logic);

	public void add(LogicGroup logicGroup);
	
	public default ApplicationMenu createGroupMenu(LogicGroup logicGroup) {
		return null;
	}
	
	public default ApplicationMenu createFullMenu() {
		return null;
	}
	
	public default ApplicationMenu createQuickMenu() {
		return null;
	}
}
