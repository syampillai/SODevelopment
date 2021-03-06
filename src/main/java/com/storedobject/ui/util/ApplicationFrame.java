package com.storedobject.ui.util;

import com.storedobject.core.*;
import com.storedobject.ui.Application;
import com.storedobject.ui.ApplicationLayout;
import com.storedobject.ui.Image;
import com.storedobject.vaadin.ApplicationMenu;
import com.storedobject.vaadin.ButtonIcon;
import com.storedobject.vaadin.SpeakerButton;
import com.storedobject.vaadin.View;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasText;
import com.vaadin.flow.component.HtmlComponent;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.combobox.dataview.ComboBoxListDataView;
import com.vaadin.flow.component.contextmenu.ContextMenu;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.ListDataProvider;

import java.util.Collection;

@CssImport("./so/shared-styles.css")
@CssImport("./so/animation/animation.css")
public class ApplicationFrame extends com.storedobject.vaadin.ApplicationFrame implements ApplicationLayout {

    private final Div progressBars = new Div();
    private SearchMenu searchMenu;

    public ApplicationFrame() {
        new ContextMenu(this);
    }

    @Override
    protected ApplicationMenu createMenu() {
        String menuLogic = ApplicationServer.getGlobalProperty("application.logic.menu", "");
        if(menuLogic.isEmpty()) {
            return super.createMenu();
        }
        try {
            Class<?> menuLogicClass = JavaClassLoader.getLogic(menuLogic);
            return (ApplicationMenu) menuLogicClass.getDeclaredConstructor(ApplicationFrame.class).
                    newInstance(this);
        } catch(Throwable e) {
            ApplicationServer.log(e);
        }
        return new EmptyMenu();
    }

    @Override
    public Div getProgressBarHolder() {
        return progressBars;
    }

    @Override
    public Component getMenuSearcher() {
        if(searchMenu == null) {
            searchMenu = new SearchMenu();
        }
        return searchMenu;
    }

    @Override
    public void viewDetached(View view) {
        if(searchMenu != null) {
            searchMenu.resetLogic(view);
        }
        if(!isMenuOpened() && Application.get().getActiveViewCount() == 0) {
            openMenu();
        }
    }

    @Override
    public boolean isMenuOpened() {
        return isDrawerOpened();
    }

    @Override
    public Component getLogo() {
        return Image.createFromMedia("application-logo");
    }

    @Override
    public void loggedin(com.storedobject.vaadin.Application application) {
        Application a = (Application) application;
        TransactionManager tm = ((Application)application).getTransactionManager();
        SystemUser su = tm.getUser();
        application.setLocale(su.getLocale());
        Person p = su.getPerson();
        application.speak("Welcome " + p);
        StringBuilder sb = new StringBuilder();
        sb.append(su.getId()).append(':').append(su.getLogin()).append(" (").append(p.getName()).append(')');
        SystemEntity se = tm.getEntity();
        if(se != null) {
            Entity e = se.getEntity();
            sb.append(", ").append(e.getName()).append(", ").append(e.getLocation());
        }
        HasText user = getUserNameComponent();
        user.setText(su.getName());
        ((HtmlComponent)user).setTitle(sb.toString());
        ButtonIcon logoutButton = new ButtonIcon("sign-out", e -> a.logout());
        logoutButton.setStyle("color", "var(--lumo-error-color)");
        logoutButton.getElement().setAttribute("title", "Sign out");
        logoutButton.getElement().setAttribute("tabindex", "-1");
        getToolbox().add(new SpeakerButton(), ((Application)application).getAlertButton(),
                new CompactSwitcher(a).icon, logoutButton);
    }

    @Override
    public void initialized() {
        if(searchMenu != null) {
            searchMenu.setVisible(true);
        }
        openMenu();
    }

    private static class CompactSwitcher {

        private final ButtonIcon icon;

        public CompactSwitcher(Application a) {
            icon = new ButtonIcon("vaadin:" + (a.isCompactTheme() ? "expand" : "compress") + "_square", e -> cSwitch());
            icon.getElement().getStyle().set("color", "var(--so-header-color)");
            icon.getElement().setAttribute("tabindex", "-1");
            icon.getElement().setAttribute("title", "Toggle compact view");
        }

        private void cSwitch() {
            Application a = Application.get();
            if(a != null) {
                a.setCompactTheme(!a.isCompactTheme());
                icon.setIcon("vaadin:" + (a.isCompactTheme() ? "expand" : "compress") + "_square");
            }
        }
    }

    private static class SearchMenu extends ComboBox<Logic> {

        private boolean itemsSet = false;

        private SearchMenu() {
            setPlaceholder("Search menu");
            setWidthFull();
            super.setVisible(false);
        }

        @Override
        public void setVisible(boolean visible) {
            super.setVisible(itemsSet && visible);
        }

        @Override
        public ComboBoxListDataView<Logic> setItems(Collection<Logic> items) {
            itemsSet = !items.isEmpty();
            return super.setItems(items);
        }

        private void resetLogic(View view) {
            DataProvider<?, ?> dp = getDataProvider();
            if(dp != null) {
                //noinspection unchecked
                ((ListDataProvider<Logic>)dp).getItems().stream().
                        filter(logic -> logic.getExecutable() == view).findAny().
                        ifPresent(logic -> logic.setExecutable(null));
            }
        }
    }

    private static class EmptyMenu extends Div implements ApplicationMenu {
    }
}