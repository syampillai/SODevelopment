package com.storedobject.ui;

import com.storedobject.common.SORuntimeException;
import com.storedobject.core.StringUtility;
import com.storedobject.helper.ID;
import com.storedobject.helper.LitComponent;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.html.Input;

/**
 * Component that can submit HTML form-data to another site.
 *
 * @author Syam
 */
@Tag("so-form-submit")
@JsModule("./so/form-submit/form-submit.js")
public class FormSubmit extends LitComponent {

    private boolean site;

    public FormSubmit() {
        this(null);
    }

    public FormSubmit(String site) {
        getElement().setProperty("idForm", "soFORM" + ID.newID());
        setSite(site);
    }

    /**
     * Set site to which form-date should be sent.
     *
     * @param site Site.
     */
    public void setSite(String site) {
        getElement().setProperty("site", site);
        this.site = site != null && !site.isEmpty();
    }

    /**
     * Add form data.
     *
     * @param name Name. Should not be null or empty.
     * @param value Value. (A null value will remove the value that was already defined for the same name).
     */
    public void addData(String name, String value) {
        if(name != null && !name.isBlank()) {
            name = StringUtility.pack(name);
            Input input = new Input();
            input.getElement().setAttribute("type", "hidden").setAttribute("name", name)
                    .setAttribute("value", value);
            getElement().appendChild(input.getElement());
        }
    }

    /**
     * Submit the form-date to the site set via @{link #setSite(String)}. The application
     * may get redirected based on the response from the site. If you are switching away to another application or site,
     * it's better to invoke {@link Application#closeAllViews(boolean)} with "true" as the parameter before invoking
     * this method.
     */
    public void submit() {
        if(!site) {
            throw new SORuntimeException("'Site' not set");
        }
        executeJS("go");
    }
}
