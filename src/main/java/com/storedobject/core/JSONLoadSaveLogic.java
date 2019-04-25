package com.storedobject.core;

public abstract class JSONLoadSaveLogic extends JSONLoadLogic {

	protected boolean decodeData() {
		return true;
	}

	protected abstract boolean saveData();
	
	protected boolean loadAfterSave() {
		return true;
	}
}