package com.storedobject.ui;

import com.storedobject.common.StringList;
import com.storedobject.core.FileData;
import com.storedobject.core.ObjectMemoryList;
import com.storedobject.core.StreamData;
import com.storedobject.vaadin.Button;
import com.storedobject.vaadin.CloseableView;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Span;

public class FileViewerGrid extends ObjectGrid<FileData> implements CloseableView {

    public FileViewerGrid() {
        super(new ObjectMemoryList<>(FileData.class, true), StringList.create("Name"));
        setCaption("Documents");
        addConstructedListener(o -> con());
    }

    private void con() {
        addComponentColumn(this::createViewMenu).setFlexGrow(0).setWidth("120px");
        addComponentColumn(this::createDownloadMenu).setFlexGrow(0).setWidth("150px");
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
        return new Button(view, icon, e -> getApplication().view(file.getName(), sd)).asSmall();
    }

    private Component createDownloadMenu(FileData file) {
        return new Button("Download", e -> getApplication().download(file.getFile())).asSmall();
    }

    public void add(String name, StreamData streamData) {
        FileData file = new FileData(name);
        file.setFile(streamData);
        file.makeVirtual();
        add(file);
    }
}
