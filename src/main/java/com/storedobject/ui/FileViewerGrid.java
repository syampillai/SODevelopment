package com.storedobject.ui;

import com.storedobject.common.StringList;
import com.storedobject.core.FileData;
import com.storedobject.core.ObjectMemoryList;
import com.storedobject.core.StreamData;
import com.storedobject.vaadin.Button;
import com.storedobject.vaadin.CloseableView;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;

import java.util.HashMap;
import java.util.Map;

public class FileViewerGrid extends ObjectGrid<FileData> implements CloseableView {

    private final Map<Class<?>, ObjectEditor<?>> editors = new HashMap<>();

    public FileViewerGrid() {
        super(new ObjectMemoryList<>(FileData.class, true), StringList.create("Name"));
        setCaption("Documents");
        addConstructedListener(o -> con());
    }

    private void con() {
        addComponentColumn(this::createViewMenu).setFlexGrow(0).setWidth("120px");
        addComponentColumn(this::createDownloadMenu).setFlexGrow(0).setWidth("120px");
        addComponentColumn(this::createDetailsMenu).setFlexGrow(0).setWidth("120px");
        addColumn(v -> " ").setFlexGrow(0);
    }

    private Component createViewMenu(FileData file) {
        Component vc = createViewMenu2(file);
        return vc == null ? new Span() : vc;
    }

    private Component createViewMenu2(FileData file) {
        final StreamData sd = file.getFile();
        final String view, icon;
        if(sd != null) {
            if(sd.isAudio()) {
                view = "Play";
                icon = "volume_up";
            } else if(sd.isVideo()) {
                view = "Play";
                icon = "movie";
            } else if(sd.isImage() || sd.getMimeType().equals("application/pdf")){
                view = "View";
                icon = view;
            } else {
                return null;
            }
        } else {
            return null;
        }
        return FileViewer.b(new Button(view, icon, e -> view(file)));
    }

    private Component createDownloadMenu(FileData file) {
        return FileViewer.b(new Button("Download", e -> getApplication().download(file.getFile())));
    }

    private Component createDetailsMenu(FileData file) {
        if(file.getClass() == FileData.class) return new Span();
        return FileViewer.b(new Button("Details", VaadinIcon.ELLIPSIS_H, e -> details(file)));
    }

    public void add(String name, StreamData streamData) {
        FileData file = new FileData(name);
        file.setFile(streamData);
        file.makeVirtual();
        add(file);
    }

    protected void view(FileData fileData) {
        getApplication().view(fileData.getName(), fileData.getFile());
    }

    <O extends FileData> void details(FileData fileData) {
        if(fileData.getClass() == FileData.class) return;
        ObjectEditor<?> editor = editors.get(fileData.getClass());
        if (editor == null) {
            editor = ObjectEditor.create(fileData.getClass());
            editors.put(fileData.getClass(), editor);
        }
        @SuppressWarnings("unchecked") ObjectEditor<O> oe = (ObjectEditor<O>) editor;
        @SuppressWarnings("unchecked") O o = (O) fileData;
        oe.setCaption(o.getName());
        oe.viewObject(o);
    }
}
