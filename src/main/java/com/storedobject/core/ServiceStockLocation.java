package com.storedobject.core;

public final class ServiceStockLocation extends InventoryStockLocation {

	public ServiceStockLocation() {
	}
	
    public static void columns(Columns columns) {
    }
    
    public static ServiceStockLocation getForStore(Id storeId) {
    	return null;
    }
    
    public static ServiceStockLocation getForStore(InventoryStore store) {
    	return null;
    }
    
    public static ServiceStockLocation create(InventoryStore store, TransactionManager tm) {
    	return null;
    }
    
    public static ServiceStockLocation create(Id storeId, TransactionManager tm) {
    	return null;
    }
}