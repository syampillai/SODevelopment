package com.storedobject.ui;

import com.storedobject.vaadin.ClickHandler;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.server.AbstractStreamResource;
import com.vaadin.flow.shared.Registration;

import java.util.Optional;

@Tag("div")
public class ImageMap extends Component {

    public static class AreaClickEvent extends ComponentEvent<ImageMap> {

        public AreaClickEvent(ImageMap source, Area area, boolean fromClient) {
            super(source, fromClient);
        }

        public Area getArea() {
            return null;
        }
    }

    public static class Area {

        private Area(ImageMap owner, int x, int y, int width, int height) {
        }

        public Area setLeft(int left) {
            return this;
        }

        public Area setTop(int top) {
            return this;
        }

        public Area setWidth(int width) {
            return this;
        }

        public Area setHeight(int height) {
            return this;
        }

        public Registration addClickListener(ComponentEventListener<AreaClickEvent> listener) {
            return null;
        }

        public Area setTitle(String title) {
            return this;
        }

        public Area setBackground(String background) {
            return this;
        }

        public Area setHoverBackground(String hoverBackground) {
            return this;
        }

        public Area setBorder(String border) {
            return this;
        }

        public Area setHoverBorder(String hoverBorder) {
            return this;
        }
    }

    public ImageMap() {
    }

    public ImageMap(String source) {
    }

    public ImageMap(AbstractStreamResource source) {
    }

    public Area addArea(int x, int y, int width, int height) {
        return null;
    }

    public void removeArea(Area area) {
    }

    public Registration addClickHandler(ClickHandler clickHandler) {
        return null;
    }

    public void setSource(String source) {
    }

    public void setSource(AbstractStreamResource source) {
    }

    public void setAlt(String alt) {
    }

    public Optional<String> getAlt() {
        return Optional.empty();
    }

    public void setHoverBackground(String hoverBackground) {
    }

    public void setHoverBorder(String hoverBorder) {
    }

    public void setAreaBackground(String areaBackground) {
    }

    public void setAreaBorder(String areaBorder) {
    }
}