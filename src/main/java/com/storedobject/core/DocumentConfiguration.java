package com.storedobject.core;

import com.storedobject.core.annotation.Column;

import java.lang.reflect.Modifier;

public final class DocumentConfiguration extends StoredObject {

    private String name;
    private String fileClass;

    public DocumentConfiguration() {
    	fileClass = "com.storedobject.core.FileData";
    }

    public static void columns(Columns columns) {
        columns.add("Name", "text");
        columns.add("FileClass", "text");
    }

    public static void indices(Indices indices) {
        indices.add("lower(Name)", true);
    }

    @Override
	public String getUniqueCondition() {
        return "lower(Name)='" + getName().trim().toLowerCase().replace("'", "''") + "'";
    }

    public static DocumentConfiguration get(String name) {
        return StoredObjectUtility.get(DocumentConfiguration.class, "Name", name, false);
    }

    public static ObjectIterator < DocumentConfiguration > list(String name) {
        return StoredObjectUtility.list(DocumentConfiguration.class, "Name", name, false);
    }

    public static int hints() {
        return ObjectHint.SMALL | ObjectHint.SMALL_LIST;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(order = 100)
    public String getName() {
        return name;
    }

    public void setFileClass(String fileClass) {
        this.fileClass = fileClass;
    }

    @Column(order = 200)
    public String getFileClass() {
        return fileClass;
    }

    @Override
	public void validateData(TransactionManager tm) throws Exception {
        if(StringUtility.isWhite(fileClass)) {
            throw new Invalid_Value("File Class");
        }
        if(!JavaClassLoader.exists(fileClass)) {
            throw new Invalid_Value("File Class does not exist!");
        }
        if(getFileDataClass() == null) {
            throw new Invalid_Value("File Class");        	
        }
        if(StringUtility.isWhite(name)) {
            throw new Invalid_Value("Name");
        }
        super.validateData(tm);
    }
    
    @SuppressWarnings("unchecked")
	public Class<? extends FileData> getFileDataClass() {
    	try {
    		Class<? extends FileData> fc = (Class<? extends FileData>)JavaClassLoader.getLogic(fileClass);
    		if(Modifier.isAbstract(fc.getModifiers())) {
    			return null;
    		}
    		return fc;
		} catch(Throwable error) {
			return null;
		}
    }
    
    @Override
	public String toString() {
    	return name;
    }
    
    public boolean matches(FileData file) {
    	return file.getClass() == getFileDataClass();
    }
}
