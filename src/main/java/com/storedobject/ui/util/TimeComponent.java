package com.storedobject.ui.util;

import com.storedobject.helper.LitComponent;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.JsModule;

@Tag( "so-time")
@JsModule("./so/time/so-time.js")
public class TimeComponent extends LitComponent {

    public TimeComponent(int timeDifference) {
        executeJS("initComp",timeDifference);
    }
}
