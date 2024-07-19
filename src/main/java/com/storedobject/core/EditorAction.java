package com.storedobject.core;

public class EditorAction {

	public final static int ALLOW_ANY = 1;
	public final static int NEW = 2;
	public final static int ADD = 2;
    public final static int EDIT = 4;
    public final static int DELETE = 8;
    public final static int SEARCH = 16;
    public final static int RELOAD = 64;
    public final static int VIEW = 128;
    public final static int TREEVIEW = 256;
    public final static int REMOVE = 512;
    public final static int SELECT = 1024;
	public final static int PDF = 8192;
	public final static int PRINT = 8192;
	public final static int EXCEL = 16384;
	public final static int AUDIT = 32768;
	public final static int APPEND = 65536;
	public final static int NO_EXIT = 131072;
	public final static int LEDGER = 262144;
    public final static int ALL = NEW | EDIT | DELETE | SEARCH | PRINT | TREEVIEW | VIEW | RELOAD | APPEND | LEDGER;

    public static int getActions(String actions) {
    	return getActions(actions, false);
    }

    public static int getActions(String actions, boolean developer) {
    	if(actions == null) {
    		return ALL | (developer ? PDF : 0);
    	}
		if(actions.contains("ALLOW_ANY")) {
			actions = actions.replace("ALLOW_ANY", "ANY");
		}
		if(actions.contains("NO_EXIT")) {
			actions = actions.replace("NO_EXIT", "EXIT");
		}
    	int a = 0;
    	if(actions.contains("ALL")) {
    		a = ALL;
    	}
    	if(actions.contains("ANY")) {
    		a |= ALLOW_ANY;
    	}
    	if(actions.contains("NEW") || actions.contains("ADD")) {
    		a |= NEW;
    	}
    	if(actions.contains("EDIT")) {
    		a |= EDIT;
    	}
    	if(actions.contains("DELETE")) {
    		a |= DELETE;
    	}
    	if(actions.contains("SEARCH")) {
    		a |= SEARCH;
    	}
    	if(actions.contains("LOAD")) {
    		a |= RELOAD;
    	}
    	if(actions.contains("VIEW")) {
    		a |= VIEW;
    	}
    	if(actions.contains("TREEVIEW")) {
    		a |= TREEVIEW;
    	}
    	if(actions.contains("REMOVE")) {
    		a |= REMOVE;
    	}
    	if(developer || actions.contains("PDF")) {
    		a |= PDF;
    	}
    	if(actions.contains("EXCEL")) {
    		a |= EXCEL;
    	}
		if(actions.contains("AUDIT")) {
			a |= AUDIT;
		}
		if(actions.contains("APPEND")) {
			a |= APPEND;
		}
		if(actions.contains("EXIT")) {
			a |= NO_EXIT;
		}
		if(actions.contains("LEDGER")) {
			a |= LEDGER;
		}
    	return a;
    }
}
