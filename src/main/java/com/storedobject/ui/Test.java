package com.storedobject.ui;

import com.storedobject.common.Executable;
import com.storedobject.core.*;

public class Test implements Executable {

    @Override
    public void execute() {
        FileData fd = StoredObject.get(FileData.class, "lower(Name)='testupload'");
        if(fd == null) {
            Application.warning("File 'TestUpload' not found!");
        } else {
            Application.get().view("File: Test Upload", fd);
        }
    }
}
