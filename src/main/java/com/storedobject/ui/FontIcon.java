package com.storedobject.ui;

import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dependency.NpmPackage;

@NpmPackage(value = "@fortawesome/fontawesome-free", version = "7.2.0")
@CssImport("@fortawesome/fontawesome-free/css/all.min.css")
public class FontIcon extends com.vaadin.flow.component.icon.FontIcon {

    private static final String FONT_AWESOME = "fa-";

    public FontIcon(String iconName) {
        this(iconName, "regular");
    }

    public FontIcon(String iconName, String categoryName) {
        super(FONT_AWESOME + categoryName, FONT_AWESOME + iconName);
        getStyle().set("display", "inline-flex").set("align-items", "center")
                .set("margin-right", "var(--lumo-space-xs)");
    }

    public FontIcon set(String iconName, String categoryName) {
        String[] c = getIconClassNames();
        if(c == null) {
            return this;
        }
        if(categoryName != null && !categoryName.isBlank() && c.length > 1) {
            c[1] = FONT_AWESOME + categoryName;
        }
        if(iconName != null && !iconName.isBlank() && c.length > 0) {
            c[0] = FONT_AWESOME + iconName;
        }
        setIconClassNames(c);
        return this;
    }
}
