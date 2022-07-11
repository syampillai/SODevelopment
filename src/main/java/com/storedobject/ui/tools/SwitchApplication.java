package com.storedobject.ui.tools;

import com.storedobject.common.Executable;
import com.storedobject.core.ExternalSystemUser;
import com.storedobject.core.ServerLink;
import com.storedobject.ui.Application;
import com.storedobject.ui.ELabel;
import com.storedobject.vaadin.Button;
import com.storedobject.vaadin.View;
import com.vaadin.flow.component.listbox.ListBox;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class SwitchApplication implements Executable {

    public static List<ExternalSystemUser> listUsers() {
        String fromURL = ServerLink.trim(Application.get().getURL());
        return Application.get().getTransactionManager().getUser()
                .listLinks(ExternalSystemUser.class, "Verified")
                .filter(u -> u.getServer().existsLinks(ServerLink.class, "FromLink='" + fromURL + "'",
                        false))
                .toList();
    }

    public static boolean canSwitch() {
        return !listUsers().isEmpty();
    }

    public static View createView() {
        List<ExternalSystemUser> servers = listUsers();
        if(servers.isEmpty()) {
            return null;
        }
        View view = new View("Switch to");
        ELabel caption = new ELabel("Switch to", "font-weight:bold;");
        VerticalLayout v = new VerticalLayout(caption);
        v.setMaxHeight("70vh");
        ListBox<ExternalSystemUser> select = new ListBox<>();
        select.setMaxHeight("40vh");
        if(servers.size() > 10) {
            select.setHeight("40vh");
        }
        select.setItems(servers);
        select.setItemLabelGenerator(u -> u.getServer().getDescription());
        select.addValueChangeListener(e -> switchToInt(e.getValue()));
        v.add(select);
        Button cancel = new Button("Cancel", e -> view.close());
        v.add(cancel);
        v.setHorizontalComponentAlignment(FlexComponent.Alignment.CENTER, caption, select, cancel);
        view.setComponent(v);
        view.setWindowMode(true);
        return view;
    }

    private static void switchToInt(ExternalSystemUser user) {
        if(user == null) {
            return;
        }
        String loginBlock = user.createLoginBlock();
        if(loginBlock == null) {
            return;
        }
        Application.get().closeAllViews(true);
        Application.get().close(user.getServer().listLinks(ServerLink.class, "FromLink='"
                + ServerLink.trim(Application.get().getURL()) + "'").findFirst().getToLink() + "/?loginBlock="
                + URLEncoder.encode(loginBlock, StandardCharsets.UTF_8));
    }

    @Override
    public void execute() {
        View v = createView();
        if(v != null) {
            v.execute();
        }
    }

    public static void switchTo(ExternalSystemUser user) {
        if(listUsers().stream().anyMatch(eu -> eu.getId().equals(user.getId()))) {
            switchToInt(user);
        }
    }
}
