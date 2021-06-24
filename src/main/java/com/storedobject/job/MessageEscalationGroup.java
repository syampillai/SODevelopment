package com.storedobject.job;

import java.util.List;

import com.storedobject.core.Columns;
import com.storedobject.core.Name;
import com.storedobject.core.ObjectIterator;
import com.storedobject.core.Person;
import com.storedobject.core.SystemUser;

public class MessageEscalationGroup extends Name {

    public MessageEscalationGroup() {
    }

    public static void columns(Columns columns) {
    }
    
    public static MessageEscalationGroup get(String name) {
    	return null;
    }

    public static ObjectIterator < MessageEscalationGroup > list(String name) {
    	return null;
    }

    public ObjectIterator<Person> listMembers() {
    	return null;
    }
    
    public List<SystemUser> listUsers() {
    	return null;
    }
}