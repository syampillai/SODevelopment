package com.storedobject.ui;

import com.storedobject.common.SORuntimeException;
import com.storedobject.core.*;
import com.storedobject.vaadin.*;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.dom.Style;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * An image-based menu to be created from the logic defined via an {@link ApplicationModule} instance.
 *
 * @author Syam
 */
public class ModuleMenu extends View implements CloseableView, SingletonLogic, ViewSelected {

    private final Breadcrumbs breadcrumbs = new Breadcrumbs(this);
    private final CenteredLayout body;
    private final Map<Breadcrumbs.Breadcrumb, List<ModuleLogic>> modules = new HashMap<>();
    private final Application application;
    private final int size;
    private final String fontSize;

    /**
     * Constructor.
     * <p>Note: The caption of the currently running logic will be taken as the name of the application module.</p>
     *
     * @param application Application.
     */
    public ModuleMenu(Application application) {
        this(application, application.getLogicTitle(null));
    }

    /**
     * Constructor.
     *
     * @param application Application.
     * @param moduleName Name of the application module where the logic definitions are stored.
     */
    public ModuleMenu(Application application, String moduleName) {
        this(application, am(moduleName));
    }

    /**
     * Constructor.
     *
     * @param application Application.
     * @param module Application module where the logic definitions are stored.
     */
    public ModuleMenu(Application application, ApplicationModule module) {
        this.application = application;
        if(module == null) {
            throw new SORuntimeException("Application Module missing!");
        }
        size = module.getSize();
        fontSize = module.getFontSize();
        setCaption(module.getName());
        body = new CenteredLayout();
        body.setGap(10);
        VerticalLayout layout = new VerticalLayout(breadcrumbs, body);
        setComponent(layout);
        if(drawFailed(module)) {
            warning("Nothing found under: " + module.getName());
        } else {
            application.closeMenu();
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

    private boolean drawFailed(StoredObject name) {
        String s = name instanceof Name n ? n.getName() : (name instanceof ModuleLogic m ? m.getName() : "");
        Breadcrumbs.Breadcrumb bc = breadcrumbs.add(s);
        bc.addRemovalListener(modules::remove);
        List<ModuleLogic> moduleList = listModules(name);
        modules.put(bc, moduleList);
        return drawFailed(moduleList);
    }

    private boolean drawFailed(List<ModuleLogic> moduleList) {
        application.closeMenu();
        body.removeAll();
        for(ModuleLogic m: moduleList) {
            body.add(new MenuBlock(m));
        }
        return moduleList.isEmpty();
    }

    @Override
    public void viewSelected() {
        application.closeMenu();
    }

    @Override
    public void clicked(Component c) {
        if(c instanceof Breadcrumbs.Breadcrumb) {
            drawFailed(modules.get(c));
        }
    }

    /**
     * Get the size of the menu block in pixels to be shown on the screen.
     *
     * @return Default is 250 if not defined in the {@link ApplicationModule#getSize()}.
     */
    public int getSize() {
        return size;
    }

    /**
     * Get the font-size of the menu title.
     *
     * @return The CSS value representing the font-size. Default is "larger" if not defined in
     * the {@link ApplicationModule#getFontSize()}.
     */
    public String getFontSize() {
        return fontSize;
    }

    @CssImport("./so/module-menu/module-menu.css")
    private class MenuBlock extends Div {

        private final ModuleLogic moduleLogic;
        private View view;

        private MenuBlock(ModuleLogic moduleLogic) {
            this.moduleLogic = moduleLogic;
            Paragraph p = new Paragraph(moduleLogic.getTitle());
            p.addClassName("menutext");
            String fontSize = getFontSize();
            if(fontSize != null && !fontSize.isBlank()) {
                p.getStyle().set("font-size", fontSize);
            }
            style(p.getStyle(), "width");
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
                style(image.getStyle(), "width");
                style(image.getStyle(), "height", getSize() - 50);
            }
            new Clickable<>(this, e -> execute());
            new Card(this);
            addClassName("menublock");
            style(getStyle());
        }

        private void style(Style style) {
            style(style, "width");
            style(style, "height");
        }

        private void style(Style style, String param) {
            style(style, param, getSize());
        }

        private void style(Style style, String param, int s) {
            if(s == 0) {
                return;
            }
            style.set("min-" + param, s + "px").set("max-" + param, s + "px");
        }

        private void execute() {
            application.closeMenu();
            Logic logic = moduleLogic.getLogic();
            if(logic == null) {
                if(drawFailed(moduleLogic)) {
                    warning("Nothing found under - " + moduleLogic.getName());
                }
            } else {
                if(view != null) {
                    View old = application.getActiveViews().filter(v -> v == view).findAny().orElse(null);
                    if(old != null) {
                        view.execute();
                        return;
                    }
                }
                Object object = application.getServer().execute(logic);
                if(object instanceof View) {
                    view = (View) object;
                }
            }
        }
    }
}
