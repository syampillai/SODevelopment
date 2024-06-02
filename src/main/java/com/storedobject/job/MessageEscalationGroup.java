package com.storedobject.job;

import java.util.List;

import com.storedobject.common.ArrayListSet;
import com.storedobject.core.*;

public final class MessageEscalationGroup extends Name {

    public MessageEscalationGroup() {
    }

    public static void columns(Columns columns) {
    }
    
    public static int hints() {
        return ObjectHint.SMALL | ObjectHint.SMALL_LIST;
    }

    public static void indices(Indices indices) {
        indices.add("lower(Name)", true);
    }

    public static MessageEscalationGroup get(String name) {
    	name = name(name);
        MessageEscalationGroup mg = StoredObjectUtility.get(MessageEscalationGroup.class, "Name", name, false);
        return mg == null ? list(name).single(false) : mg;
    }

    public static ObjectIterator < MessageEscalationGroup > list(String name) {
    	name = name(name);
        return StoredObjectUtility.list(MessageEscalationGroup.class, "Name", name, false);
    }

    public static String[] links() {
        return new String[] {
        	"Members (Persons)|com.storedobject.core.Person|||0",
        	"Members (System Groups)|com.storedobject.core.SystemUserGroup|||0",
        };
    }

    @Override
	public void validateData(TransactionManager tm) throws Exception {
        name = name(name);
        super.validateData(tm);
    }
    
    private static String name(String name) {
    	return toCode(name).replace('-', '_');
    }

    public ObjectIterator<Person> listMembers() {
    	ArrayListSet<Person> m = new ArrayListSet<>();
    	listLinks(Person.class).collectAll(m);
    	for(SystemUserGroup g: listLinks(SystemUserGroup.class)) {
    		for(SystemUser su: g.listLinks(SystemUser.class)) {
    			m.add(su.getPerson());
    		}
    	}
    	return ObjectIterator.create(m);
    }
    
    public List<SystemUser> listUsers() {
    	ArrayListSet<SystemUser> sus = new ArrayListSet<>();
    	listLinks(Person.class).forEach(p -> list(SystemUser.class, "Person=" + p.getId()).collectAll(sus));
    	for(SystemUserGroup g: listLinks(SystemUserGroup.class)) {
    		g.listLinks(SystemUser.class).collectAll(sus);
    	}
    	return sus;
    }
}