package com.storedobject.ui.tools;

import com.storedobject.common.Executable;
import com.storedobject.core.ExternalSystemUser;
import com.storedobject.ui.Application;
import com.storedobject.ui.ELabel;
import com.storedobject.ui.FormSubmit;
import com.storedobject.ui.util.SOServlet;
import com.storedobject.vaadin.Button;
import com.storedobject.vaadin.View;
import com.vaadin.flow.component.listbox.ListBox;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import java.util.List;

public class SwitchApplication implements Executable {

    private static List<ExternalSystemUser> servers() {
        return Application.get().getTransactionManager().getUser()
                .listLinks(ExternalSystemUser.class, "Verified").toList();
    }

    public static boolean canSwitch() {
        return !servers().isEmpty();
    }

    public static View createView() {
        List<ExternalSystemUser> servers = servers();
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
        FormSubmit form = new FormSubmit();
        v.add(form);
        select.addValueChangeListener(e -> switchTo(e.getValue(), form));
        v.add(select);
        Button cancel = new Button("Cancel", e -> view.close());
        v.add(cancel);
        v.setHorizontalComponentAlignment(FlexComponent.Alignment.CENTER, caption, select, cancel);
        view.setComponent(v);
        view.setWindowMode(true);
        return view;
    }

    private static void switchTo(ExternalSystemUser user, FormSubmit form) {
        if(user == null) {
            return;
        }
        String loginBlock = user.createLoginBlock();
        if(loginBlock == null) {
            return;
        }
        Application.get().closeAllViews(true);
        form.setSite(SOServlet.getURL() + "/" + user.getServer().getName());
        form.addData("loginBlock", loginBlock);
        form.submit();
        /*
        Application.get().close(SOServlet.getURL() + "/" + user.getServer().getName() + "/?loginBlock="
                + URLEncoder.encode(loginBlock, StandardCharsets.UTF_8));
        */
    }

    @Override
    public void execute() {
        View v = createView();
        if(v != null) {
            v.execute();
        }
    }
}
