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
    public final static int ALL = NEW | EDIT | DELETE | SEARCH | PRINT | TREEVIEW | VIEW | RELOAD | APPEND;

    public static int getActions(String actions) {
    	return 0;
    }

    public static int getActions(String actions, boolean developer) {
    	return 0;
    }
}
