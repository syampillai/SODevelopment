package com.storedobject.ui;

import com.storedobject.common.SORuntimeException;
import com.storedobject.core.*;
import com.storedobject.vaadin.*;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.shared.Registration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ModuleMenu extends View implements CloseableView {

    private final Breadcrumbs breadcrumbs = new Breadcrumbs(this);
    private final Body body;
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
        body = new Body();
        CSSGrid div = new CSSGrid();
        div.getStyle().set("grid-template-rows", "10% auto");
        div.justify(breadcrumbs, CSSGrid.Position.START);
        div.align(breadcrumbs, CSSGrid.Position.START);
        div.add(breadcrumbs, body);
        setComponent(div);
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
        body.clear();
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

    private class Body extends GridLayout {

        private Registration r;
        private int blockCount = 0, columns = 1, possibleColumns;

        private Body() {
            super(1);
            setGap(20);
            setWidthFull();
        }

        private void width(int w) {
            possibleColumns = w / 290;
            if(possibleColumns < 1) {
                possibleColumns = 1;
            }
            if(possibleColumns < columns) {
                setColumns(possibleColumns);
            } else if(possibleColumns > columns) {
                readjustColumns();
            }
        }

        private void readjustColumns() {
            if(possibleColumns > columns) {
                if(blockCount > columns) {
                    int c = blockCount;
                    switch(c) {
                        case 4:
                            c = 2;
                            break;
                        case 5:
                        case 6:
                            c = 3;
                            break;
                        case 7:
                        case 8:
                            c = 4;
                            break;
                    }
                    setColumns(c);
                } else {
                    setColumns(blockCount);
                }
            }
        }

        @Override
        public void setColumns(int columns) {
            columns = Math.min(columns, possibleColumns);
            if(columns == this.columns) {
                return;
            }
            this.columns = columns;
            super.setColumns(this.columns);
        }

        @Override
        protected void onAttach(AttachEvent attachEvent) {
            super.onAttach(attachEvent);
            if(r == null) {
                width(Application.get().getDeviceWidth());
                r = Application.get().addContentResizedListener((w, h) -> width(w));
            }
        }

        @Override
        protected void onDetach(DetachEvent detachEvent) {
            super.onDetach(detachEvent);
            if(r != null) {
                r.remove();
                r = null;
            }
        }

        private void add(MenuBlock m) {
            super.add(m);
            ++blockCount;
            readjustColumns();
        }

        private void clear() {
            blockCount = 0;
            removeAll();
            setColumns(1);
        }
    }

    @CssImport("./so/module-menu/module-menu.css")
    private class MenuBlock extends Div {

        private final ModuleLogic moduleLogic;

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
                //noinspection ResultOfMethodCallIgnored
                Application.get().getServer().execute(logic);
            }
        }
    }
}
