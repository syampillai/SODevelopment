package com.storedobject.ui.util;

import com.storedobject.vaadin.ButtonIcon;
import com.storedobject.vaadin.ClickHandler;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.dom.Element;

@Tag("span")
public class AlertButton extends Component {

    private static final String STYLE0 = "color: black;";
    private static final String STYLE =
            "display:inline-block;" +
                    "text-align:center;" +
                    "color:black;" +
                    "background-color:yellow;" +
                    "position:relative;" +
                    "right:1.5em;" +
                    "padding:3px;" +
                    "border-radius:50%;" +
                    "font-size:0.5em;" +
                    "min-width:1.5em;";
    private int alertCount = 0;
    private final ButtonIcon icon;
    private final Element counter;

    public AlertButton(ClickHandler clickHandler) {
        clickHandler = ClickHandler.transfer(clickHandler, this);
        icon = new ButtonIcon("social:notifications-none", clickHandler);
        icon.getElement().getStyle().set("color", "var(--so-header-color)");
        icon.getElement().setAttribute("tabindex", "-1");
        getElement().setAttribute("title", "Alerts");
        counter = new Element("sup");
        getElement().appendChild(icon.getElement()).appendChild(counter);
    }

    public void setAlertCount(int alertCount) {
        this.alertCount = alertCount;
        if(alertCount == 0) {
            counter.setText("");
            setIcon("social:notifications-none");
            counter.setAttribute("style", STYLE0);
        } else {
            counter.setText(Integer.toString(alertCount));
            setIcon("social:notifications");
            counter.setAttribute("style", STYLE);
        }
    }

    public int getAlertCount() {
        return alertCount;
    }

    public void setIcon(String icon) {
        this.icon.setIcon(icon);
    }
}