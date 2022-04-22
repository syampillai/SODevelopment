package com.storedobject.ui;

import com.storedobject.common.Executable;
import com.storedobject.common.JSON;
import com.storedobject.core.FileFolder;
import com.storedobject.core.Person;
import com.storedobject.core.StoredObject;
import com.storedobject.core.SystemUser;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Test implements Executable {

    @Override
    public void execute() {
        /*
        List<SystemUser> list = StoredObject.list(SystemUser.class).skip(3).limit(1).toList();
        Map<String, Object> map = new HashMap<>();
        try {
            list.forEach(su -> {
                try {
                    su.save(map, true, true);
                } catch(Throwable e) {
                    throw new RuntimeException(e);
                }
            });
            System.err.println(new JSON(map).toPrettyString());
        } catch(Throwable e) {
            throw new RuntimeException(e);
        }
         */
        new ObjectViewer(Application.get()).view(FileFolder.getRoot());
    }
}
