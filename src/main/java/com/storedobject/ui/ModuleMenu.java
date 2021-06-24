package com.storedobject.ui;

import com.storedobject.common.SORuntimeException;
import com.storedobject.core.*;
import com.storedobject.vaadin.*;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ModuleMenu extends View implements CloseableView, SingletonLogic {

    private final Breadcrumbs breadcrumbs = new Breadcrumbs(this);
    private final CenteredLayout body;
    private final Map<Breadcrumbs.Breadcrumb, List<ModuleLogic>> modules = new HashMap<>();

    public ModuleMenu(Application application) {
        this(application, application.getLogicTitle(null));
    }

    public ModuleMenu(Application application, String moduleName) {
        this(application, am(moduleName));
    }

    public ModuleMenu(Application application, ApplicationModule module) {
        if(module == null) {
            throw new SORuntimeException("Application Module missing!");
        }
        setCaption(module.getName());
        body = new CenteredLayout();
        body.setGap(10);
        VerticalLayout layout = new VerticalLayout(breadcrumbs, body);
        setComponent(layout);
        if(drawFailed(module)) {
            warning("Nothing found under: " + module.getName());
        }
    }

    private static ApplicationModule am(String moduleName) {
        if(moduleName != null) {
            moduleName = moduleName.trim();
        }
        if(moduleName == null || moduleName.isEmpty()) {
            throw new SORuntimeException("Module name missing!");
        }
        ApplicationModule am = ApplicationModule.get(moduleName);
        if(am == null) {
            throw new SORuntimeException("Application Module not found - " + moduleName);
        }
        return am;
    }

    private static List<ModuleLogic> listModules(StoredObject so) {
        ArrayList<ModuleLogic> modules = new ArrayList<>();
        so.listLinks(ModuleLogic.class, null, "DisplayOrder").collectAll(modules);
        return modules;
    }

    private boolean drawFailed(Name name) {
        Breadcrumbs.Breadcrumb bc = breadcrumbs.add(name.getName());
        bc.addRemovalListener(modules::remove);
        List<ModuleLogic> moduleList = listModules(name);
        modules.put(bc, moduleList);
        return drawFailed(moduleList);
    }

    private boolean drawFailed(List<ModuleLogic> moduleList) {
        body.removeAll();
        for(ModuleLogic m: moduleList) {
            body.add(new MenuBlock(m));
        }
        return moduleList.isEmpty();
    }

    @Override
    public void clicked(Component c) {
        if(c instanceof Breadcrumbs.Breadcrumb) {
            drawFailed(modules.get(c));
        }
    }

    @CssImport("./so/module-menu/module-menu.css")
    private class MenuBlock extends Div {

        private final ModuleLogic moduleLogic;
        private View view;

        private MenuBlock(ModuleLogic moduleLogic) {
            this.moduleLogic = moduleLogic;
            Paragraph p = new Paragraph(moduleLogic.getTitle());
            p.addClassName("menutext");
            MediaFile imageFile = moduleLogic.getMenuImage();
            if(imageFile == null || !imageFile.isImage()) {
                if(imageFile == null) {
                    warning("Image not found for: " + moduleLogic.getTitle());
                } else {
                    warning("Not an image: " + imageFile.getName() + " [Content type: " + imageFile.getContentType() + "]");
                }
                add(p);
            } else {
                Image image = new Image("media/" + imageFile.getFileName());
                image.addClassName("menuimage");
                add(image, p);
            }
            new Clickable<>(this, e -> execute());
            new Card(this);
            addClassName("menublock");
        }

        private void execute() {
            Logic logic = moduleLogic.getLogic();
            if(logic == null) {
                if(drawFailed(moduleLogic)) {
                    warning("Nothing found under - " + moduleLogic.getName());
                }
            } else {
                Application a = Application.get();
                if(view != null) {
                    View old = a.getActiveViews().filter(v -> v == view).findAny().orElse(null);
                    if(old != null) {
                        view.execute();
                        return;
                    }
                }
                Object object = a.getServer().execute(logic);
                if(object instanceof View) {
                    view = (View) object;
                }
            }
        }
    }
}
