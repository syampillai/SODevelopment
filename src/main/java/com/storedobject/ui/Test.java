package com.storedobject.ui;

import com.storedobject.ai.Knowledge;
import com.storedobject.ai.KnowledgeModule;
import com.storedobject.common.Executable;
import com.storedobject.core.*;
import com.storedobject.ui.ai.ChatView;
import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;

public class Test extends Knowledge implements Executable {

    public Test(Device device) {
        super(device);
        addDataClass(Person.class);
        addDataClass("User", SystemUser.class);
        addDataClass("Role", SystemUserGroup.class);
        addDataClass("UserGroup", SystemUserGroup.class);
        addModules(new KM());
    }

    @Override
    public void execute() {
        new ChatView(this, "Persons & Users").execute();
    }

    @SuppressWarnings("unused")
    private class KM implements KnowledgeModule {

        @Tool("Get roles/groups of the specified user Id as JSON")
        public String listRoles(@P("User ID") String userId) {
            JSONMap map = new JSONMap();
            Id id = getId(map, userId);
            if(id != null) {
                SystemUser user = StoredObject.get(SystemUser.class, id);
                if (user == null) {
                    user = StoredObject.get(SystemUser.class, "Person.Id=" + id);
                }
                if (user == null) {
                    map.put("error", "User not found for ID = " + userId);
                } else {
                    save(map, "roles", user.listGroups());
                }
            }
            return map.toJSON().toString();
        }
    }
}
